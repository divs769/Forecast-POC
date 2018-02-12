package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public class StockForecastingModels {

    public static Stream<ProductStockData> naivePrediction(Stream<ProductStockData> historicData, Stream<Date> datesToPredict){
        Optional<ProductStockData> lastStockData = historicData.max(Comparator.comparing(ProductStockData::getDate));
        if(lastStockData.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, lastStockData.get().getStockValue());
        }else{
            return Stream.of();
        }
    }

    public static Stream<ProductStockData> averagePrediction(Stream<ProductStockData> historicData, Stream<Date> datesToPredict){
        OptionalDouble average = historicData.map(sp -> sp.getStockValue()).mapToLong(x -> x).average();
        if(average.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, Math.round(average.getAsDouble()));
        }else{
            return Stream.of();
        }
    }

    private static Stream<ProductStockData> repeatValueOnProductStockDates(Stream<Date> datesToPredict, long stockValue) {
        return datesToPredict.map(date -> new ProductStockData(date, stockValue));
    }

}
