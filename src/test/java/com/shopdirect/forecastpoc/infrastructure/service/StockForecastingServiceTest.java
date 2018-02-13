package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import com.shopdirect.forecastpoc.infrastructure.repository.ProductRepository;
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
    private ProductRepository repository;

    @Before
    public void setUp() throws Exception {
        stockForecastingService = new StockForecastingService(repository);
    }

    @Test
    public void testPastPredictionsForBestModel() {
        LocalDate initialDate = LocalDate.of(2017, Month.DECEMBER, 25);
        ProductStockData prod1 = new ProductStockData(initialDate, 10);
        ProductStockData prod2 = new ProductStockData(initialDate.plusDays(7), 20);
        ProductStockData prod3 = new ProductStockData(initialDate.plusDays(14), 30);
        ProductStockData prod4 = new ProductStockData(initialDate.plusDays(21), 40);
        ProductStockData prod5 = new ProductStockData(initialDate.plusDays(28), 50);
        ProductStockData prod6 = new ProductStockData(initialDate.plusDays(35), 60);
        ProductStockData prod7 = new ProductStockData(initialDate.plusDays(42), 70);
        ProductStockData prod8 = new ProductStockData(initialDate.plusDays(49), 80);

        when(repository.findAll()).thenReturn(Arrays.asList(
                prod1, prod2, prod3, prod4, prod5, prod6, prod7, prod8
        ));

        ForecastingResult result = stockForecastingService.getForecastings(4);
        Assert.assertEquals(8, result.getHistoricData().size());
        compareProductStock(prod1, result.getHistoricData().get(0));
        compareProductStock(prod2, result.getHistoricData().get(1));
        compareProductStock(prod3, result.getHistoricData().get(2));
        compareProductStock(prod4, result.getHistoricData().get(3));
        compareProductStock(prod5, result.getHistoricData().get(4));
        compareProductStock(prod6, result.getHistoricData().get(5));
        compareProductStock(prod7, result.getHistoricData().get(6));
        compareProductStock(prod8, result.getHistoricData().get(7));
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