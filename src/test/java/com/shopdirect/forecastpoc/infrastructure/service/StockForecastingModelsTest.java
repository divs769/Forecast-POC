package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
public class StockForecastingModelsTest {
    private LocalDate d1 = LocalDate.of(2018, Month.JANUARY, 06);
    private LocalDate d2 = LocalDate.of(2018, Month.JANUARY, 13);
    private LocalDate d3 = LocalDate.of(2018, Month.JANUARY, 20);
    private LocalDate d4 = LocalDate.of(2018, Month.JANUARY, 27);
    private LocalDate d5 = LocalDate.of(2017, Month.DECEMBER, 11);
    private LocalDate d6 = LocalDate.of(2017, Month.DECEMBER, 18);
    private LocalDate d7 = LocalDate.of(2017, Month.DECEMBER, 25);

    @Test
    public void testNaivePrediction(){
        Stream<StockDataItem> result = StockForecastingModels.naivePrediction(Stream.of(
                new StockDataItem(d7, 15),
                new StockDataItem(d5, 25),
                new StockDataItem(d6, 20)

        ), Stream.of(d1, d2, d3, d4));
        List<StockDataItem> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new StockDataItem(d1, 15),
                productStockData.get(0));
        compareProductStock(new StockDataItem(d2, 15),
                productStockData.get(1));
        compareProductStock(new StockDataItem(d3, 15),
                productStockData.get(2));
        compareProductStock(new StockDataItem(d4, 15),
                productStockData.get(3));
    }

    @Test
    public void testIntResultAverage(){
        Stream<StockDataItem> result = StockForecastingModels.averagePrediction(Stream.of(
                new StockDataItem(d5, 15),
                new StockDataItem(d7, 25),
                new StockDataItem(d6, 20)

        ), Stream.of(d1, d2, d3, d4));

        List<StockDataItem> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new StockDataItem(d1, 20),
                productStockData.get(0));
        compareProductStock(new StockDataItem(d2, 20),
                productStockData.get(1));
        compareProductStock(new StockDataItem(d3, 20),
                productStockData.get(2));
        compareProductStock(new StockDataItem(d4, 20),
                productStockData.get(3));
    }

    @Test
    public void testLowerRoundingAverage(){
        Stream<StockDataItem> result = StockForecastingModels.averagePrediction(Stream.of(
                new StockDataItem(d7, 10),
                new StockDataItem(d6, 10),
                new StockDataItem(d5, 20)

        ), Stream.of(d1, d2, d3, d4));

        List<StockDataItem> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new StockDataItem(d1, 13),
                productStockData.get(0));
        compareProductStock(new StockDataItem(d2, 13),
                productStockData.get(1));
        compareProductStock(new StockDataItem(d3, 13),
                productStockData.get(2));
        compareProductStock(new StockDataItem(d4, 13),
                productStockData.get(3));
    }

    @Test
    public void testUpperRoundingAverage(){
        Stream<StockDataItem> result = StockForecastingModels.averagePrediction(Stream.of(
                new StockDataItem(d7, 11),
                new StockDataItem(d5, 10),
                new StockDataItem(d6, 20)

        ), Stream.of(d1, d2, d3, d4));

        List<StockDataItem> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new StockDataItem(d1, 14),
                productStockData.get(0));
        compareProductStock(new StockDataItem(d2, 14),
                productStockData.get(1));
        compareProductStock(new StockDataItem(d3, 14),
                productStockData.get(2));
        compareProductStock(new StockDataItem(d4, 14),
                productStockData.get(3));
    }

    private void compareProductStock(StockDataItem expected, StockDataItem actual){
        Assert.assertEquals(expected.getDate(), actual.getDate());
        Assert.assertEquals(expected.getStock(), actual.getStock());
    }
}