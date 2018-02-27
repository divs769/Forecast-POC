package com.shopdirect.forecastpoc.infrastructure.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shopdirect.forecastpoc.infrastructure.dao.LineStockDao;
import com.shopdirect.forecastpoc.infrastructure.dao.ProductsAndCategoriesDao;
import com.shopdirect.forecastpoc.infrastructure.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockForecastingServiceTest {

    private StockForecastingService stockForecastingService;
    private LocalDate initialDate = LocalDate.of(2017, Month.DECEMBER, 25);
    private LineStockData[] prods;

    @Mock
    private LineStockDao lineStockDao;

    @Mock
    private ProductsAndCategoriesDao productsAndCategoriesDao;

    @Captor
    private ArgumentCaptor<Map<String, AttributeValue>> captor;

    @Before
    public void setUp() throws Exception {
        stockForecastingService = new StockForecastingService(lineStockDao, productsAndCategoriesDao);
    }

    @Test
    public void testPredictionsWithStartDate(){
        initData();
        ForecastingResult result = stockForecastingService.getForecastings(4, "8M417", initialDate.plusWeeks(4));
        assertEquals(4, result.getHistoricData().size());
        compareProductStock(prods[4], result.getHistoricData().get(0));
        compareProductStock(prods[5], result.getHistoricData().get(1));
        compareProductStock(prods[6], result.getHistoricData().get(2));
        compareProductStock(prods[7], result.getHistoricData().get(3));
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(28), 40),
                new StockDataItem(initialDate.plusDays(35), 50),
                new StockDataItem(initialDate.plusDays(42), 60),
                new StockDataItem(initialDate.plusDays(49), 70),
                new StockDataItem(initialDate.plusDays(56), 80),
                new StockDataItem(initialDate.plusDays(63), 80),
                new StockDataItem(initialDate.plusDays(70), 80),
                new StockDataItem(initialDate.plusDays(77), 80)
        ), 15.607, "naive");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(28), 25),
                new StockDataItem(initialDate.plusDays(35), 30),
                new StockDataItem(initialDate.plusDays(42), 35),
                new StockDataItem(initialDate.plusDays(49), 40),
                new StockDataItem(initialDate.plusDays(56), 45),
                new StockDataItem(initialDate.plusDays(63), 45),
                new StockDataItem(initialDate.plusDays(70), 45),
                new StockDataItem(initialDate.plusDays(77), 45)
        ), 49.219, "average");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void testPastPredictionsForBestModel() {
        initData();
        ForecastingResult result = stockForecastingService.getForecastings(4, "8M417");
        assertEquals(8, result.getHistoricData().size());
        compareProductStock(prods[0], result.getHistoricData().get(0));
        compareProductStock(prods[1], result.getHistoricData().get(1));
        compareProductStock(prods[2], result.getHistoricData().get(2));
        compareProductStock(prods[3], result.getHistoricData().get(3));
        compareProductStock(prods[4], result.getHistoricData().get(4));
        compareProductStock(prods[5], result.getHistoricData().get(5));
        compareProductStock(prods[6], result.getHistoricData().get(6));
        compareProductStock(prods[7], result.getHistoricData().get(7));
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 20),
                new StockDataItem(initialDate.plusDays(21), 30),
                new StockDataItem(initialDate.plusDays(28), 40),
                new StockDataItem(initialDate.plusDays(35), 50),
                new StockDataItem(initialDate.plusDays(42), 60),
                new StockDataItem(initialDate.plusDays(49), 70),
                new StockDataItem(initialDate.plusDays(56), 80),
                new StockDataItem(initialDate.plusDays(63), 80),
                new StockDataItem(initialDate.plusDays(70), 80),
                new StockDataItem(initialDate.plusDays(77), 80)
        ), 23.814, "naive");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 15),
                new StockDataItem(initialDate.plusDays(21), 20),
                new StockDataItem(initialDate.plusDays(28), 25),
                new StockDataItem(initialDate.plusDays(35), 30),
                new StockDataItem(initialDate.plusDays(42), 35),
                new StockDataItem(initialDate.plusDays(49), 40),
                new StockDataItem(initialDate.plusDays(56), 45),
                new StockDataItem(initialDate.plusDays(63), 45),
                new StockDataItem(initialDate.plusDays(70), 45),
                new StockDataItem(initialDate.plusDays(77), 45)
        ), 48.809, "average");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void dataShouldConvertBeforeSave() throws Exception {
        LineStockData data = new LineStockData(LocalDate.now(), 100);
        stockForecastingService.saveStockData(data);

        verify(lineStockDao).saveItem(captor.capture());

        Map<String, AttributeValue> item = captor.getValue();

        assertEquals(data.getLineNumber(), item.get("lineNumber").getS());
        assertEquals(data.getDate().toString(), item.get("date").getS());
        assertEquals(data.getStockValue(), Long.parseLong(item.get("stock").getN()));
    }

    @Test
    public void testHistoryWith0Products(){
        when(lineStockDao.getByLineNumber("8M417")).thenReturn(Arrays.asList());
        ForecastingResult result = stockForecastingService.getForecastings(4, "8M417");
        assertEquals(0, result.getHistoricData().size());
        assertEquals(0, result.getForecastings().size());
    }

    @Test
    public void testHistoryWith1Product(){
        LineStockData prod = new LineStockData(initialDate, 10);
        when(lineStockDao.getByLineNumber("8M417")).thenReturn(Arrays.asList(prod));
        ForecastingResult result = stockForecastingService.getForecastings(4, "8M417");
        assertEquals(1, result.getHistoricData().size());
        compareProductStock(prod, result.getHistoricData().get(0));
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 10),
                new StockDataItem(initialDate.plusDays(21), 10),
                new StockDataItem(initialDate.plusDays(28), 10)
        ), null, "average");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 10),
                new StockDataItem(initialDate.plusDays(21), 10),
                new StockDataItem(initialDate.plusDays(28), 10)
        ), null, "naive");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void testFutureStartDate(){
        LineStockData prod = new LineStockData(initialDate, 10);
        LineStockData prod2 = new LineStockData(initialDate.plusDays(7), 20);
        when(lineStockDao.getByLineNumber("8M417")).thenReturn(Arrays.asList(prod, prod2));
        ForecastingResult result = stockForecastingService.getForecastings(2, "8M417", initialDate.plusWeeks(3));
        assertEquals(0, result.getHistoricData().size());
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusWeeks(3), 15),
                new StockDataItem(initialDate.plusWeeks(4), 15)
        ), null, "average");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusWeeks(3), 20),
                new StockDataItem(initialDate.plusWeeks(4), 20)
        ), null, "naive");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void testForecastingsSameError(){
        LineStockData prod = new LineStockData(initialDate, 10);
        LineStockData prod2 = new LineStockData(initialDate.plusDays(7), 10);
        when(lineStockDao.getByLineNumber("8M417")).thenReturn(Arrays.asList(prod, prod2));
        ForecastingResult result = stockForecastingService.getForecastings(4, "8M417");
        assertEquals(2, result.getHistoricData().size());
        compareProductStock(prod, result.getHistoricData().get(0));
        compareProductStock(prod2, result.getHistoricData().get(1));
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 10),
                new StockDataItem(initialDate.plusDays(21), 10),
                new StockDataItem(initialDate.plusDays(28), 10),
                new StockDataItem(initialDate.plusDays(35), 10)
        ), 0.0, "average");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), 10),
                new StockDataItem(initialDate.plusDays(14), 10),
                new StockDataItem(initialDate.plusDays(21), 10),
                new StockDataItem(initialDate.plusDays(28), 10),
                new StockDataItem(initialDate.plusDays(35), 10)
        ), 0.0, "naive");
        compareForecastingResult(result1, results.get(0));
        compareForecastingResult(result2, results.get(1));
    }

    @Test
    public void testProductsForecasting(){
        String product = "shirts";
        List<String> lineNumbers = Arrays.asList("LN1", "LN2", "LN3");
        Map<String, List<LineStockData>> mockedData = mockDataDaos(product, lineNumbers);
        ForecastingResult result = stockForecastingService.getForecastings(1, ProductHierarchy.PRODUCT, product);
        List<LineStockData> expectedHistoricData = Arrays.asList(
                new LineStockData(initialDate,
                        mockedData.get("LN1").get(0).getStockValue() +
                                mockedData.get("LN2").get(0).getStockValue() +
                                mockedData.get("LN3").get(0).getStockValue(), null),
                new LineStockData(initialDate.plusWeeks(1),
                                mockedData.get("LN2").get(1).getStockValue() +
                                mockedData.get("LN3").get(1).getStockValue(), null),
                new LineStockData(initialDate.plusWeeks(2),
                                mockedData.get("LN3").get(2).getStockValue(), null)
        );
        assertEquals(expectedHistoricData.size(), result.getHistoricData().size());
        for(int i = 0; i < expectedHistoricData.size(); i++){
            compareProductStock(expectedHistoricData.get(i), result.getHistoricData().get(i));
        }
        List<ForecastingModelResult> results = result.getForecastings();
        assertEquals(2, results.size());
        ForecastingModelResult result1 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), expectedHistoricData.get(0).getStockValue()),
                new StockDataItem(initialDate.plusDays(14),
                        Math.round((expectedHistoricData.get(0).getStockValue() +
                                expectedHistoricData.get(1).getStockValue()) / 2)),
                new StockDataItem(initialDate.plusDays(21),
                        Math.round(expectedHistoricData.stream()
                                .map(obj -> obj.getStockValue())
                                .mapToLong(num -> num)
                                .average().getAsDouble()))), null, "average");
        ForecastingModelResult result2 = new ForecastingModelResult(Arrays.asList(
                new StockDataItem(initialDate.plusDays(7), expectedHistoricData.get(0).getStockValue()),
                new StockDataItem(initialDate.plusDays(14), expectedHistoricData.get(1).getStockValue()),
                new StockDataItem(initialDate.plusDays(21), expectedHistoricData.get(2).getStockValue())
        ), null, "naive");

        compareForecastingResultIgnoringError(result1, results.get(0));
        compareForecastingResultIgnoringError(result2, results.get(1));
    }

    private Map<String, List<LineStockData>> mockDataDaos(String product, List<String> lineNumbers) {
        when(productsAndCategoriesDao.getLineNumbersByProduct(product))
                .thenReturn(lineNumbers.stream());
        Map<String, List<LineStockData>> mockedData = new HashMap<>();
        for(int i = 0; i < lineNumbers.size(); i++){
            String lineNumber = lineNumbers.get(i);
            List<LineStockData> mockedProducts = new ArrayList<>();
            for(int j = 0; j < i + 1; j++){
                mockedProducts.add(new LineStockData(initialDate.plusWeeks(j), 10 * (i + j + 1)));
            }
            when(lineStockDao.getByLineNumber(lineNumber))
                    .thenReturn(mockedProducts);
            mockedData.put(lineNumber, mockedProducts);
        }
        return mockedData;
    }

    private void compareForecastingResult(ForecastingModelResult expected, ForecastingModelResult actual){
        compareForecastingResultIgnoringError(expected, actual);
        if(expected.getError() != null){
            assertEquals(expected.getError(), actual.getError(), 0.001);
        }else{
            assertEquals(expected.getError(), actual.getError());
        }

    }

    private void compareForecastingResultIgnoringError(ForecastingModelResult expected, ForecastingModelResult actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getForecastedValues().size(), actual.getForecastedValues().size());
        for(int i = 0; i < expected.getForecastedValues().size(); i++){
            compareProductStock(expected.getForecastedValues().get(i), actual.getForecastedValues().get(i));
        }
    }

    private void compareProductStock(LineStockData expected, StockDataItem actual){
        compareProductStock(new StockDataItem(expected.getDate(), expected.getStockValue()), actual);
    }

    private void compareProductStock(StockDataItem expected, StockDataItem actual){
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getStock(), actual.getStock());
    }

    private void initData() {
        prods = new LineStockData[8];
        prods[0] = new LineStockData(initialDate, 10);
        prods[1] = new LineStockData(initialDate.plusDays(7), 20);
        prods[2] = new LineStockData(initialDate.plusDays(14), 30);
        prods[3] = new LineStockData(initialDate.plusDays(21), 40);
        prods[4] = new LineStockData(initialDate.plusDays(28), 50);
        prods[5] = new LineStockData(initialDate.plusDays(35), 60);
        prods[6] = new LineStockData(initialDate.plusDays(42), 70);
        prods[7] = new LineStockData(initialDate.plusDays(49), 80);

        when(lineStockDao.getByLineNumber("8M417")).thenReturn(Arrays.asList(prods));
    }
}