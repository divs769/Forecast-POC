package com.shopdirect.forecastpoc.infrastructure.service;

import com.google.common.collect.Lists;
import com.shopdirect.forecastpoc.infrastructure.dao.ProductStockDao;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component
public class StockForecastingService {

    private final ProductStockDao productStockDao;

    @Autowired
    public StockForecastingService(ProductStockDao productStockDao) {
        this.productStockDao = productStockDao;
    }

    public ForecastingResult getForecastings(int numWeeks){
        return this.getForecastings(numWeeks, null);
    }

    public ForecastingResult getForecastings(int numWeeks, LocalDate startDate){
        List<ProductStockData> fullProductStockData =  Lists.newArrayList(productStockDao.getAll()).stream()
                .sorted(Comparator.comparing(ProductStockData::getDate)).collect(Collectors.toList());

        List<ForecastingModelResult> forecastings = calculatePastForecastings(fullProductStockData, startDate);
        List<LocalDate> nextDates = getNextWeekDates(numWeeks, fullProductStockData.get(fullProductStockData.size() - 1))
                .collect(Collectors.toList());
        Stream<ProductStockData> naivePredictions = StockForecastingModels.naivePrediction(fullProductStockData.stream(),
                nextDates.stream());
        int indexNaive = forecastings.get(0).getName().equals("naive") ? 0 : 1;
        forecastings.get(indexNaive).getForecastedValues()
                .addAll(naivePredictions.collect(Collectors.toList()));

        Stream<ProductStockData> averagePredictions = StockForecastingModels.averagePrediction(fullProductStockData.stream(),
                nextDates.stream());
        forecastings.get(Math.abs(indexNaive - 1)).getForecastedValues().addAll(averagePredictions.collect(Collectors.toList()));

        return new ForecastingResult(forecastings, filterByStartDate(startDate, fullProductStockData) );
    }

    private List<ProductStockData> filterByStartDate(LocalDate startDate, List<ProductStockData> products){
        if(startDate != null){
            return products.stream()
                    .filter(prod -> prod.getDate().compareTo(startDate) >= 0)
                    .collect(Collectors.toList());
        }else{
            return products;
        }
    }

    /*
    -should get past forecasting values from a db in the future
    -For now, it recalculates the whole forecasting history
    */
    private List<List<ProductStockData>> getPastForecastings(List<ProductStockData> fullProductStockData, LocalDate startDate){
        Stream<ProductStockData> naiveForecastings = Stream.of();
        Stream<ProductStockData> averageForecastings = Stream.of();
        Stream<ProductStockData> naiveForecasting, averageForecasting;
        LocalDate date;
        int productsBeforeStartDate = 1;
        if(startDate != null){
            productsBeforeStartDate = (int) fullProductStockData.stream().filter(prod -> prod.getDate().isBefore(startDate)).count();
        }
        for(int i = productsBeforeStartDate; i < fullProductStockData.size(); i++){
            date = fullProductStockData.get(i).getDate();
            naiveForecasting = StockForecastingModels.naivePrediction(fullProductStockData.stream().limit(i),
                    Stream.of(date));
            naiveForecastings = Stream.concat(naiveForecastings, naiveForecasting);

            averageForecasting = StockForecastingModels.averagePrediction(fullProductStockData.stream().limit(i),
                    Stream.of(date));
            averageForecastings = Stream.concat(averageForecastings, averageForecasting);

        }
        return Arrays.asList(naiveForecastings.collect(Collectors.toList()),
                averageForecastings.collect(Collectors.toList()));
    }

    public List<ForecastingModelResult> calculatePastForecastings(List<ProductStockData> fullProductStockData, LocalDate startDate){
        Map<LocalDate, ProductStockData> actualValues = fullProductStockData.stream().collect(toMap(ProductStockData::getDate, prod -> prod));

        List<List<ProductStockData>> pastForecastings = getPastForecastings(fullProductStockData, startDate);
        List<ProductStockData> naiveForecastings = pastForecastings.get(0);
        List<ProductStockData> averageForecastings = pastForecastings.get(1);
        Double naiveError = calculateError(naiveForecastings, actualValues);
        Double averageError = calculateError(averageForecastings, actualValues);

        return Stream.of(new ForecastingModelResult(naiveForecastings, naiveError, "naive"),
                new ForecastingModelResult(averageForecastings, averageError, "average"))
                .sorted(Comparator.comparing(ForecastingModelResult::getError)).collect(Collectors.toList());
    }

    private static Stream<LocalDate> getNextWeekDates(int numberWeeks, ProductStockData lastStockData){
        return Stream
                .iterate(lastStockData.getDate().plusDays(7),
                        localDate -> localDate.plusDays(7))
                .limit(numberWeeks);
    }

    private Double calculateError(List<ProductStockData> forecastedValues, Map<LocalDate, ProductStockData> actualValues){
        OptionalDouble error = forecastedValues.stream().map(prod ->
                ((double) Math.abs(prod.getStockValue() - actualValues.get(prod.getDate()).getStockValue()))
                            / (1 + actualValues.get(prod.getDate()).getStockValue()))
                .mapToDouble(elem -> elem)
                .average();

        return error.isPresent() ? 100 * error.getAsDouble() : null;
    }
}
