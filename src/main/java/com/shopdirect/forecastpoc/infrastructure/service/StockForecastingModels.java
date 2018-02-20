package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public class StockForecastingModels {

    public static Stream<ProductStockData> naivePrediction(Stream<ProductStockData> historicData, Stream<LocalDate> datesToPredict, String lineNumber){
        Optional<ProductStockData> lastStockData = historicData.max(Comparator.comparing(ProductStockData::getDate));
        if(lastStockData.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, lastStockData.get().getStockValue(), lineNumber);
        }else{
            return Stream.of();
        }
    }

    public static Stream<ProductStockData> averagePrediction(Stream<ProductStockData> historicData, Stream<LocalDate> datesToPredict, String lineNumber){
        OptionalDouble average = historicData.map(sp -> sp.getStockValue()).mapToLong(x -> x).average();
        if(average.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, Math.round(average.getAsDouble()), lineNumber);
        }else{
            return Stream.of();
        }
    }

    private static Stream<ProductStockData> repeatValueOnProductStockDates(Stream<LocalDate> datesToPredict, long stockValue, String lineNumber) {
        return datesToPredict.map(date -> new ProductStockData(date, stockValue, lineNumber));
    }

}
