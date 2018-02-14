package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.dao.ProductStockDao;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
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
    private LocalDate initialDate = LocalDate.of(2017, Month.DECEMBER, 25);
    private ProductStockData[] prods;

    @Mock
    private ProductStockDao productStockDao;

    @Before
    public void setUp() throws Exception {
        stockForecastingService = new StockForecastingService(productStockDao);
        initialDate = LocalDate.of(2017, Month.DECEMBER, 25);
        prods = new ProductStockData[8];
        prods[0] = new ProductStockData(initialDate, 10);
        prods[1] = new ProductStockData(initialDate.plusDays(7), 20);
        prods[2] = new ProductStockData(initialDate.plusDays(14), 30);
        prods[3] = new ProductStockData(initialDate.plusDays(21), 40);
        prods[4] = new ProductStockData(initialDate.plusDays(28), 50);
        prods[5] = new ProductStockData(initialDate.plusDays(35), 60);
        prods[6] = new ProductStockData(initialDate.plusDays(42), 70);
        prods[7] = new ProductStockData(initialDate.plusDays(49), 80);

        when(productStockDao.getAll()).thenReturn(Arrays.asList(prods));
    }

    @Test
    public void testPredictionsWithStartDate(){
        ForecastingResult result = stockForecastingService.getForecastings(4, initialDate.plusWeeks(4));
        Assert.assertEquals(4, result.getHistoricData().size());
        compareProductStock(prods[4], result.getHistoricData().get(0));
        compareProductStock(prods[5], result.getHistoricData().get(1));
        compareProductStock(prods[6], result.getHistoricData().get(2));
        compareProductStock(prods[7], result.getHistoricData().get(3));
        List<ForecastingModelResult> results = result.getForecastings();
        Assert.assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(initialDate.plusDays(28), 40),
                new ProductStockData(initialDate.plusDays(35), 50),
                new ProductStockData(initialDate.plusDays(42), 60),
                new ProductStockData(initialDate.plusDays(49), 70),
                new ProductStockData(initialDate.plusDays(56), 80),
                new ProductStockData(initialDate.plusDays(63), 80),
                new ProductStockData(initialDate.plusDays(70), 80),
                new ProductStockData(initialDate.plusDays(77), 80)
        ), 15.607, "naive");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(initialDate.plusDays(28), 25),
                new ProductStockData(initialDate.plusDays(35), 30),
                new ProductStockData(initialDate.plusDays(42), 35),
                new ProductStockData(initialDate.plusDays(49), 40),
                new ProductStockData(initialDate.plusDays(56), 45),
                new ProductStockData(initialDate.plusDays(63), 45),
                new ProductStockData(initialDate.plusDays(70), 45),
                new ProductStockData(initialDate.plusDays(77), 45)
        ), 49.219, "average");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void testPastPredictionsForBestModel() {
        ForecastingResult result = stockForecastingService.getForecastings(4);
        Assert.assertEquals(8, result.getHistoricData().size());
        compareProductStock(prods[0], result.getHistoricData().get(0));
        compareProductStock(prods[1], result.getHistoricData().get(1));
        compareProductStock(prods[2], result.getHistoricData().get(2));
        compareProductStock(prods[3], result.getHistoricData().get(3));
        compareProductStock(prods[4], result.getHistoricData().get(4));
        compareProductStock(prods[5], result.getHistoricData().get(5));
        compareProductStock(prods[6], result.getHistoricData().get(6));
        compareProductStock(prods[7], result.getHistoricData().get(7));
        List<ForecastingModelResult> results = result.getForecastings();
        Assert.assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(initialDate.plusDays(7), 10),
                new ProductStockData(initialDate.plusDays(14), 20),
                new ProductStockData(initialDate.plusDays(21), 30),
                new ProductStockData(initialDate.plusDays(28), 40),
                new ProductStockData(initialDate.plusDays(35), 50),
                new ProductStockData(initialDate.plusDays(42), 60),
                new ProductStockData(initialDate.plusDays(49), 70),
                new ProductStockData(initialDate.plusDays(56), 80),
                new ProductStockData(initialDate.plusDays(63), 80),
                new ProductStockData(initialDate.plusDays(70), 80),
                new ProductStockData(initialDate.plusDays(77), 80)
        ), 23.814, "naive");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new ProductStockData(initialDate.plusDays(7), 10),
                new ProductStockData(initialDate.plusDays(14), 15),
                new ProductStockData(initialDate.plusDays(21), 20),
                new ProductStockData(initialDate.plusDays(28), 25),
                new ProductStockData(initialDate.plusDays(35), 30),
                new ProductStockData(initialDate.plusDays(42), 35),
                new ProductStockData(initialDate.plusDays(49), 40),
                new ProductStockData(initialDate.plusDays(56), 45),
                new ProductStockData(initialDate.plusDays(63), 45),
                new ProductStockData(initialDate.plusDays(70), 45),
                new ProductStockData(initialDate.plusDays(77), 45)
        ), 48.809, "average");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    private void compareForecastingResult(ForecastingModelResult expected, ForecastingModelResult actual){
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getForecastedValues().size(), actual.getForecastedValues().size());
        for(int i = 0; i < expected.getForecastedValues().size(); i++){
            compareProductStock(expected.getForecastedValues().get(i), actual.getForecastedValues().get(i));
        }
        Assert.assertEquals(expected.getError(), actual.getError(), 0.001);
    }

    private void compareProductStock(ProductStockData expected, ProductStockData actual){
        Assert.assertEquals(expected.getDate(), actual.getDate());
        Assert.assertEquals(expected.getStockValue(), actual.getStockValue());
    }
}