package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

public class StockForecastingModels {

    public static Stream<StockDataItem> naivePrediction(Stream<StockDataItem> historicData, Stream<LocalDate> datesToPredict){
        Optional<StockDataItem> lastStockData = historicData.max(Comparator.comparing(StockDataItem::getDate));
        if(lastStockData.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, lastStockData.get().getStock());
        }else{
            return Stream.of();
        }
    }

    public static Stream<StockDataItem> averagePrediction(Stream<StockDataItem> historicData, Stream<LocalDate> datesToPredict){
        OptionalDouble average = historicData.map(sp -> sp.getStock()).mapToLong(x -> x).average();
        if(average.isPresent()){
            return repeatValueOnProductStockDates(datesToPredict, Math.round(average.getAsDouble()));
        }else{
            return Stream.of();
        }
    }

    private static Stream<StockDataItem> repeatValueOnProductStockDates(Stream<LocalDate> datesToPredict, long stockValue) {
        return datesToPredict.map(date -> new StockDataItem(date, stockValue));
    }

}
