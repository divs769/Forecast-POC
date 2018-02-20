package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.shopdirect.acceptancetest.LatestResponse;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetForecastingStepDef extends BaseForecastingStepDef {

    private List<ProductStockData> products;
    private URI request;

    @Autowired
    public GetForecastingStepDef(RestTemplate restTemplate, LatestResponse latestResponse, @Qualifier("testClient") AmazonDynamoDB db) {
        super(restTemplate, latestResponse, db);
    }

    @Given("^a payload with (\\d+) weeks and \"([^\"]*)\" as line number number$")
    public void aPayloadWithTheFollowingWeeksAndLineNumber(int weeks, String lineNumber) throws Throwable {
        request = UriComponentsBuilder.fromUriString(ENDPOINT).pathSegment(weeks + "")
                .path(lineNumber)
                .build().toUri();
    }

    @Given("^the get endpoint is called$")
    public void theEndpointIsCalled() throws Throwable {
        latestResponse.setResponse(restTemplate.getForEntity(request, ForecastingResult.class));
    }

    @And("^multiple stock values have been inserted$")
    public void multipleStockValuesHaveBeenInserted() throws Throwable {
        products = Arrays.asList(
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 2), 100),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 9), 90),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 16), 42),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 23), 36),
                new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 30), 64),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 6), 60),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 13), 40),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 20), 30),
                new ProductStockData(LocalDate.of(2017, Month.NOVEMBER, 27), 20),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 4), 50),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 11), 110),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 18), 130),
                new ProductStockData(LocalDate.of(2017, Month.DECEMBER, 25), 140),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 1), 150),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 8), 60),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 15), 70),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 22), 90),
                new ProductStockData(LocalDate.of(2018, Month.JANUARY, 29), 120),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 05), 80),
                new ProductStockData(LocalDate.of(2018, Month.FEBRUARY, 12), 100)
        );
        for(ProductStockData product : products){
            addItem(product);
        }
        addItem(new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 2), 150, "8M215"));
        addItem(new ProductStockData(LocalDate.of(2017, Month.OCTOBER, 2), 200, "8M214"));
    }

    @Given("^the database has rows with \"([^\"]*)\" as line number$")
    public void theDatabaseHasRowsWithAsLineNumber(String lineNumber) throws Throwable {
        products = Arrays.asList(
                new ProductStockData(LocalDate.of(2017, Month.FEBRUARY, 5), 150, lineNumber),
                new ProductStockData(LocalDate.of(2017, Month.FEBRUARY, 12), 150, lineNumber));
        addItem(products.get(0));
        addItem(products.get(1));
    }

    @And("^the result contains the historic data$")
    public void theResultContainsTheHistoricData() throws Throwable {
       ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
       List<ProductStockData> historicData = result.getHistoricData();
       assertEquals(products.size(), historicData.size());
       for(int i = 0; i < products.size(); i++){
           assertProductStocks(products.get(i), historicData.get(i));
       }
    }

    @And("^the historic data and forecastings are empty$")
    public void theHistoricDataIsEmpty() throws Throwable {
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        assertEquals(0, result.getHistoricData().size());
        assertEquals(0, result.getForecastings().size());
    }

    private void assertProductStocks(ProductStockData expected, ProductStockData actual){
        assertEquals(expected.getStockValue(), actual.getStockValue());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getLineNumber(), actual.getLineNumber());
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

    @Then("^the response is failure$")
    public void theResponseIsFailure() throws Throwable {
        assertThat(latestResponse.getResponse().getStatusCode().is4xxClientError(), is(true));
    }

    @And("^the result contains only predictions with \"([^\"]*)\" as line number$")
    public void theResultContainsOnlyPredictionsWithAsLineNumber(String lineNumber) throws Throwable {
        ForecastingResult result = (ForecastingResult) latestResponse.getResponse().getBody();
        List<ForecastingModelResult> forecastings = result.getForecastings();
        assertEquals(2, forecastings.size());
        assertEquals(products.size() + 3, forecastings.get(0).getForecastedValues().size());
        assertEquals(products.size() + 3, forecastings.get(1).getForecastedValues().size());
        Optional foundAny = forecastings
                .stream().flatMap(l -> l.getForecastedValues().stream())
                .filter(prodStockData -> !prodStockData.getLineNumber().equals(lineNumber))
                .findAny();
        assertEquals(false, foundAny.isPresent());
    }
}
