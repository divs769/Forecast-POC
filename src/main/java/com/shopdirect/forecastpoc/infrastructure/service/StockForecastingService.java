package com.shopdirect.forecastpoc.infrastructure.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.shopdirect.forecastpoc.infrastructure.dao.ProductStockDao;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import com.shopdirect.forecastpoc.infrastructure.util.TriFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toMap;

@Component
public class StockForecastingService {

    private final ProductStockDao productStockDao;
    private Map<String, TriFunction<Stream<ProductStockData>, Stream<LocalDate>, String, Stream<ProductStockData>>> forecastingMethods;

    @Autowired
    public StockForecastingService(ProductStockDao productStockDao) {
        this.productStockDao = productStockDao;
        this.forecastingMethods = new HashMap<>();
        forecastingMethods.put("naive", StockForecastingModels::naivePrediction);
        forecastingMethods.put("average", StockForecastingModels::averagePrediction);
    }

    public ForecastingResult getForecastings(int numWeeks, String lineNumber){
        return this.getForecastings(numWeeks, lineNumber, null);
    }

    public ForecastingResult getForecastings(int numWeeks, String lineNumber, LocalDate startDate) {
        List<ProductStockData> fullProductStockData =  Lists.newArrayList(productStockDao.getByLineNumber(lineNumber)).stream()
                .sorted(Comparator.comparing(ProductStockData::getDate)).collect(Collectors.toList());

        if(fullProductStockData == null || fullProductStockData.isEmpty()){
            return new ForecastingResult(Arrays.asList(), fullProductStockData);
        }

        List<ForecastingModelResult> forecastings = calculatePastForecastings(fullProductStockData, startDate);
        List<LocalDate> nextDates = getNextWeekDates(numWeeks, fullProductStockData.get(fullProductStockData.size() - 1), startDate)
                .collect(Collectors.toList());

         for(String name : forecastingMethods.keySet()){
             Stream<ProductStockData> newForecastings = forecastingMethods.get(name)
                     .apply(fullProductStockData.stream(), nextDates.stream(), fullProductStockData.get(0).getLineNumber());

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
                forecasting = forecastingMethods.get(name)
                        .apply(fullProductStockData.stream().limit(i),
                        Stream.of(date),
                        fullProductStockData.get(i).getLineNumber());
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
                .sorted(Comparator.comparing(ForecastingModelResult::getError, Comparator.nullsLast(naturalOrder())))
                .collect(Collectors.toList());
    }

    private static Stream<LocalDate> getNextWeekDates(int numberWeeks, ProductStockData lastStockData, LocalDate startDate){
        LocalDate firstDate;
        if(startDate == null
                || startDate.isBefore(lastStockData.getDate())){
            firstDate = lastStockData.getDate().plusDays(7);
        }else{
            firstDate = startDate;
        }
        return Stream
                .iterate(firstDate,
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

    public void saveStockData(ProductStockData data) {
        productStockDao.saveItem(ImmutableMap.<String, AttributeValue>builder()
                .put("lineNumber", new AttributeValue(data.getLineNumber()))
                .put("date", new AttributeValue(data.getDate().toString()))
                .put("stock", new AttributeValue().withN(String.valueOf(data.getStockValue())))
                .build());
    }
}
