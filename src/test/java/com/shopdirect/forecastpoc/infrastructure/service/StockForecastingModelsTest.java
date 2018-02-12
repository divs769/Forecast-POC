package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
public class StockForecastingModelsTest {
    @Test
    public void testNaivePrediction(){
        Date d1 = new GregorianCalendar(2017, 01, 06).getTime();
        Date d2 = new GregorianCalendar(2017, 01, 13).getTime();
        Date d3 = new GregorianCalendar(2017, 01, 20).getTime();
        Date d4 = new GregorianCalendar(2017, 01, 27).getTime();
        Stream<ProductStockData> result = StockForecastingModels.naivePrediction(Stream.of(
                new ProductStockData(new GregorianCalendar(2017, 01, 05).getTime(), 15),
                new ProductStockData(new GregorianCalendar(2017, 01, 06).getTime(), 25),
                new ProductStockData(new GregorianCalendar(2017, 01, 04).getTime(), 20)

        ), Stream.of(d1, d2, d3, d4));
        List<ProductStockData> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new ProductStockData(d1, 25),
                productStockData.get(0));
        compareProductStock(new ProductStockData(d2, 25),
                productStockData.get(1));
        compareProductStock(new ProductStockData(d3, 25),
                productStockData.get(2));
        compareProductStock(new ProductStockData(d4, 25),
                productStockData.get(3));
    }

    @Test
    public void testIntResultAverage(){
        Date d1 = new GregorianCalendar(2017, 01, 06).getTime();
        Date d2 = new GregorianCalendar(2017, 01, 13).getTime();
        Date d3 = new GregorianCalendar(2017, 01, 20).getTime();
        Date d4 = new GregorianCalendar(2017, 01, 27).getTime();

        Stream<ProductStockData> result = StockForecastingModels.averagePrediction(Stream.of(
                new ProductStockData(new GregorianCalendar(2017, 01, 05).getTime(), 15),
                new ProductStockData(new GregorianCalendar(2017, 01, 03).getTime(), 25),
                new ProductStockData(new GregorianCalendar(2017, 01, 04).getTime(), 20)

        ), Stream.of(d1, d2, d3, d4));

        List<ProductStockData> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new ProductStockData(d1, 20),
                productStockData.get(0));
        compareProductStock(new ProductStockData(d2, 20),
                productStockData.get(1));
        compareProductStock(new ProductStockData(d3, 20),
                productStockData.get(2));
        compareProductStock(new ProductStockData(d4, 20),
                productStockData.get(3));
    }

    @Test
    public void testLowerRoundingAverage(){
        Date d1 = new GregorianCalendar(2017, 01, 06).getTime();
        Date d2 = new GregorianCalendar(2017, 01, 13).getTime();
        Date d3 = new GregorianCalendar(2017, 01, 20).getTime();
        Date d4 = new GregorianCalendar(2017, 01, 27).getTime();

        Stream<ProductStockData> result = StockForecastingModels.averagePrediction(Stream.of(
                new ProductStockData(new GregorianCalendar(2017, 01, 05).getTime(), 10),
                new ProductStockData(new GregorianCalendar(2017, 01, 03).getTime(), 10),
                new ProductStockData(new GregorianCalendar(2017, 01, 04).getTime(), 20)

        ), Stream.of(d1, d2, d3, d4));

        List<ProductStockData> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new ProductStockData(d1, 13),
                productStockData.get(0));
        compareProductStock(new ProductStockData(d2, 13),
                productStockData.get(1));
        compareProductStock(new ProductStockData(d3, 13),
                productStockData.get(2));
        compareProductStock(new ProductStockData(d4, 13),
                productStockData.get(3));
    }

    @Test
    public void testUpperRoundingAverage(){
        Date d1 = new GregorianCalendar(2017, 01, 06).getTime();
        Date d2 = new GregorianCalendar(2017, 01, 13).getTime();
        Date d3 = new GregorianCalendar(2017, 01, 20).getTime();
        Date d4 = new GregorianCalendar(2017, 01, 27).getTime();

        Stream<ProductStockData> result = StockForecastingModels.averagePrediction(Stream.of(
                new ProductStockData(new GregorianCalendar(2017, 01, 05).getTime(), 11),
                new ProductStockData(new GregorianCalendar(2017, 01, 03).getTime(), 10),
                new ProductStockData(new GregorianCalendar(2017, 01, 04).getTime(), 20)

        ), Stream.of(d1, d2, d3, d4));

        List<ProductStockData> productStockData = result.collect(toList());
        Assert.assertEquals(4, productStockData.size());
        compareProductStock(new ProductStockData(d1, 14),
                productStockData.get(0));
        compareProductStock(new ProductStockData(d2, 14),
                productStockData.get(1));
        compareProductStock(new ProductStockData(d3, 14),
                productStockData.get(2));
        compareProductStock(new ProductStockData(d4, 14),
                productStockData.get(3));
    }

    private void compareProductStock(ProductStockData expected, ProductStockData actual){
        Assert.assertEquals(expected.getDate(), actual.getDate());
        Assert.assertEquals(expected.getStockValue(), actual.getStockValue());
    }
}