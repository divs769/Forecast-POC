package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.dao.StockDao;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.PastForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockForecastingServiceTest {

    private StockForecastingService stockForecastingService;

    @Mock
    private StockDao stockDao;

    @Before
    public void setUp() throws Exception {
        stockForecastingService = new StockForecastingService(stockDao);
    }

    @Test
    public void testPastPredictionsForBestModel() {
        LocalDate d1 = LocalDate.of(2018, Month.JANUARY, 22);
        LocalDate d2 = LocalDate.of(2018, Month.JANUARY, 29);
        LocalDate d3 = LocalDate.of(2018, Month.FEBRUARY, 05);
        LocalDate d4 = LocalDate.of(2018, Month.FEBRUARY, 12);
        LocalDate d5 = LocalDate.of(2017, Month.DECEMBER, 25);
        LocalDate d6 = LocalDate.of(2018, Month.JANUARY, 1);
        LocalDate d7 = LocalDate.of(2018, Month.JANUARY, 8);
        LocalDate d8 = LocalDate.of(2018, Month.JANUARY, 15);

        ProductStockData prod1 = new ProductStockData(d1, 50);
        ProductStockData prod2 = new ProductStockData(d2, 60);
        ProductStockData prod3 = new ProductStockData(d3, 70);
        ProductStockData prod4 = new ProductStockData(d4, 80);
        ProductStockData prod5 = new ProductStockData(d5, 10);
        ProductStockData prod6 = new ProductStockData(d6, 20);
        ProductStockData prod7 = new ProductStockData(d7, 30);
        ProductStockData prod8 = new ProductStockData(d8, 40);
        when(stockDao.getProductStockData()).thenReturn(Arrays.asList(
                prod5, prod6, prod7, prod8, prod1, prod2, prod3, prod4
        ));

        PastForecastingResult result = stockForecastingService.getPastForecasting(4);
        Assert.assertEquals(4, result.getHistoricData().size());
        compareProductStock(prod5, result.getHistoricData().get(0));
        compareProductStock(prod6, result.getHistoricData().get(1));
        compareProductStock(prod7, result.getHistoricData().get(2));
        compareProductStock(prod8, result.getHistoricData().get(3));
        Assert.assertEquals(4, result.getActualValues().size());
        compareProductStock(prod1, result.getActualValues().get(0));
        compareProductStock(prod2, result.getActualValues().get(1));
        compareProductStock(prod3, result.getActualValues().get(2));
        compareProductStock(prod4, result.getActualValues().get(3));
        List<ForecastingModelResult> results = result.getForecastings();
        Assert.assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(d1, 40),
                new ProductStockData(d2, 40),
                new ProductStockData(d3, 40),
                new ProductStockData(d4, 40)
        ), 36.007, "naive");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(d1, 25),
                new ProductStockData(d2, 25),
                new ProductStockData(d3, 25),
                new ProductStockData(d4, 25)
        ), 59.419, "average");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    private void compareForecastingResult(ForecastingModelResult expected, ForecastingModelResult actual){
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getError(), actual.getError(), 0.001);
        Assert.assertEquals(expected.getForecastedValues().size(), actual.getForecastedValues().size());
        for(int i = 0; i < expected.getForecastedValues().size(); i++){
            compareProductStock(expected.getForecastedValues().get(i), actual.getForecastedValues().get(i));
        }
    }

    private void compareProductStock(ProductStockData expected, ProductStockData actual){
        Assert.assertEquals(expected.getDate(), actual.getDate());
        Assert.assertEquals(expected.getStockValue(), actual.getStockValue());
    }
}