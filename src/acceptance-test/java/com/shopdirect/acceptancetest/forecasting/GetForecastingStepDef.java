package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.shopdirect.acceptancetest.LatestResponse;

import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetForecastingStepDef extends BaseForecastingStepDef {
    public final String ENDPOINT = "http://localhost:8080/forecast";
    private List<ProductStockData> products;

    @Autowired
    public GetForecastingStepDef(RestTemplate restTemplate, LatestResponse latestResponse, @Qualifier("testClient") AmazonDynamoDB db) {
        super(restTemplate, latestResponse, db);
    }

    @Given("^the database has been initialised and is running$")
    public void theDatabaseHasBeenInitialised() throws Throwable {
        TableDescription tableDescription = createTable(db).getDescription();
    }

    @Then("^the response is success$")
    public void theResponseIsSuccess() throws Throwable {
        assertEquals(HttpStatus.OK, latestResponse.getResponse().getStatusCode());
    }

    @Given("^the endpoint is called with a number of weeks greater than 0 passed as input$")
    public void theEndpointIsCalled() throws Throwable {
        URI request = UriComponentsBuilder.fromUriString(ENDPOINT).pathSegment("4").build().toUri();
        latestResponse.setResponse(restTemplate.getForEntity(request, ForecastingResult.class));
    }

    @And("^multiple stock values have been inserted$")
    public void multipleStockValuesHaveBeenInserted() throws Throwable {
        products = Arrays.asList(
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 12), 100),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 05), 80),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 29), 120),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 22), 90),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 15), 70),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 8), 60),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 1), 150),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 25), 140),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 18), 130),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 11), 110),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 4), 50),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 27), 20),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 20), 30),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 13), 40),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 6), 60),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 30), 64),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 23), 36),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 16), 42),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 9), 90),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 2), 100)
        );
        for(ProductStockData product : products){
            addUpdateItem(product);
        }
    }

    @And("^the result contains the historic data$")
    public void theResultContainsTheHistoricData() throws Throwable {
       ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
       List<ProductStockData> historicData = result.getHistoricData();
       assertEquals(products.size(), historicData.size());
       for(int i = 0; i < products.size(); i++){
           assertProductStocks(products.get(products.size() - 1 - i), historicData.get(i));
       }
    }

    private void assertProductStocks(ProductStockData expected, ProductStockData actual){
        assertEquals(expected.getStockValue(), actual.getStockValue());
        assertEquals(expected.getDate(), actual.getDate());
    }

    @And("^the result contains the correct predictions for the naive model$")
    public void theResultContainsTheCorrectNaivePredictions() throws Throwable {
        List<ProductStockData> expectedForecastingValuesNaive = Arrays.asList(
                new ProductStockData(LocalDate.of(2018, Month.MARCH, 12), 100),
                new ProductStockData(LocalDate.of(2018, Month.MARCH, 5), 100),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 26), 100),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 19), 100),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 12), 80),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 05), 120),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 29), 90),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 22), 70),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 15), 60),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 8), 150),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 1), 140),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 25), 130),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 18), 110),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 11), 50),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 4), 20),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 27), 30),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 20), 40),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 13), 60),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 6), 64),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 30), 36),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 23), 42),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 16), 90),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 9), 100)
        );
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        List<ProductStockData> forecastingData = result.getForecastings().get(0).getForecastedValues();
        assertEquals(expectedForecastingValuesNaive.size(), forecastingData.size());
        for(int i = 0; i < forecastingData.size(); i++){
            assertProductStocks(expectedForecastingValuesNaive.get(forecastingData.size() -1 - i), forecastingData.get(i));
        }
    }

    @And("^the result contains the correct predictions for the average model$")
    public void theResultContainsTheCorrectAveragePredictions() throws Throwable {
        List<ProductStockData> expectedForecastingValues = Arrays.asList(
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 9), 100),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 16), 95),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 23), 77)
        );
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        List<ProductStockData> forecastingData = result.getForecastings().get(1).getForecastedValues();
        assertEquals(23, forecastingData.size());
        for(int i = 0; i < expectedForecastingValues.size(); i++){
            assertProductStocks(expectedForecastingValues.get(i), forecastingData.get(i));
        }
    }

    @And("^the result contains one prediction for each model$")
    public void theResultContainsOnePredictionForEachModel() throws Throwable {
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        List<ForecastingModelResult> forecastingData = result.getForecastings();
        assertEquals(2, forecastingData.size());
    }

    @And("^the result contains the forecasted values for the best model first$")
    public void theResultContainsTheForecastedValuesForTheBestModelFirst() throws Throwable {
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        List<ForecastingModelResult> forecastingData = result.getForecastings();
        assertTrue(forecastingData.get(0).getError() <= forecastingData.get(1).getError());
    }

    @After
    public void cleanupTable() throws Exception {
        List<ProductStockData> scanResult = dynamoDBMapper.scan(ProductStockData.class, new DynamoDBScanExpression());
        List<DynamoDBMapper.FailedBatch> failedBatchList = dynamoDBMapper.batchDelete(scanResult);
        System.out.println("-----> FAILED BATCH: " + failedBatchList);
    }
}
