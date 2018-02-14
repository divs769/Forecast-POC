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
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component
public class StockForecastingService {

    private final ProductStockDao productStockDao;
    private Map<String, BiFunction<Stream<ProductStockData>, Stream<LocalDate>, Stream<ProductStockData>>> forecastingMethods;

    @Autowired
    public StockForecastingService(ProductStockDao productStockDao) {
        this.productStockDao = productStockDao;
        this.forecastingMethods = new HashMap<>();
        forecastingMethods.put("naive", StockForecastingModels::naivePrediction);
        forecastingMethods.put("average", StockForecastingModels::averagePrediction);
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

         for(String name : forecastingMethods.keySet()){
             Stream<ProductStockData> newForecastings = forecastingMethods.get(name)
                     .apply(fullProductStockData.stream(), nextDates.stream());

             forecastings.stream()
                     .filter(forecastingResult -> forecastingResult.getName().equals(name))
                     .limit(1)
                     .collect(Collectors.toList())
                     .get(0)
                     .getForecastedValues()
                     .addAll(newForecastings.collect(Collectors.toList()));
         }

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
    private Map<String, List<ProductStockData>> getPastForecastings(List<ProductStockData> fullProductStockData, LocalDate startDate){
        Stream<ProductStockData> forecasting, forecastings;
        LocalDate date;
        int productsBeforeStartDate = 1;
        Map<String, List<ProductStockData>> result = new HashMap<>();
        if(startDate != null){
            productsBeforeStartDate = (int) fullProductStockData.stream().filter(prod -> prod.getDate().isBefore(startDate)).count();
        }
        for(String name : forecastingMethods.keySet()) {
            forecastings = Stream.of();
            for (int i = productsBeforeStartDate; i < fullProductStockData.size(); i++) {
                date = fullProductStockData.get(i).getDate();
                forecasting = forecastingMethods.get(name).apply(fullProductStockData.stream().limit(i),
                        Stream.of(date));
                forecastings = Stream.concat(forecastings, forecasting);
            }
            result.put(name, forecastings.collect(Collectors.toList()));
        }
        return result;
    }

    public List<ForecastingModelResult> calculatePastForecastings(List<ProductStockData> fullProductStockData, LocalDate startDate){
        Map<LocalDate, ProductStockData> actualValues = fullProductStockData.stream().collect(toMap(ProductStockData::getDate, prod -> prod));

        Map<String, List<ProductStockData>> pastForecastings = getPastForecastings(fullProductStockData, startDate);
        return pastForecastings.entrySet().stream()
                .map(map -> {
                    List<ProductStockData> forecastings = map.getValue();
                    Double error = calculateError(forecastings, actualValues);
                    return new ForecastingModelResult(forecastings, error, map.getKey());
                })
                .sorted(Comparator.comparing(ForecastingModelResult::getError))
                .collect(Collectors.toList());
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
