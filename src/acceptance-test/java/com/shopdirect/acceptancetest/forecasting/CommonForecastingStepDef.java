package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.shopdirect.acceptancetest.LatestResponse;
import com.shopdirect.forecastpoc.infrastructure.model.LineStockData;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommonForecastingStepDef extends BaseForecastingStepDef {

    @Autowired
    public CommonForecastingStepDef(RestTemplate restTemplate, LatestResponse latestResponse, AmazonDynamoDB db) {
        super(restTemplate, latestResponse, db);
    }

    @Given("^the database has been initialised and is running$")
    public void theDatabaseHasBeenInitialised() throws Throwable {
        Table table = createTable(db);
        table.waitForActive();
    }

    @Then("^the response is success$")
    public void theResponseIsSuccess() throws Throwable {
        assertEquals(HttpStatus.OK, latestResponse.getResponse().getStatusCode());
    }

    @After
    public void cleanupTable() throws Exception {
        List<LineStockData> scanResult = dynamoDBMapper.scan(LineStockData.class, new DynamoDBScanExpression());
        List<DynamoDBMapper.FailedBatch> failedBatchList = dynamoDBMapper.batchDelete(scanResult);
        System.out.println("-----> FAILED BATCH: " + failedBatchList);
    }
}
