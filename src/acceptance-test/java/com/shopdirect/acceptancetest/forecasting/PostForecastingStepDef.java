package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.shopdirect.acceptancetest.LatestResponse;
import com.shopdirect.acceptancetest.model.AddStockDataRequest;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class PostForecastingStepDef extends BaseForecastingStepDef {

    private AddStockDataRequest request;

    @Autowired
    public PostForecastingStepDef(RestTemplate restTemplate, LatestResponse latestResponse, AmazonDynamoDB db) {
        super(restTemplate, latestResponse, db);
    }

    @Given("^a payload with stock data$")
    public void aPayloadWithStockData() throws Throwable {
        request = new AddStockDataRequest("LINE123", LocalDate.now(), 100);
    }

    @When("^the post endpoint is called$")
    public void thePostEndpointIsCalled() throws Throwable {
        latestResponse.setResponse(restTemplate.postForEntity(ENDPOINT, request, String.class));
    }

    @And("^the database contains the new value$")
    public void theDatabaseContainsTheNewValue() throws Throwable {
        Table table = db.getTable(TABLE);
        ItemCollection<ScanOutcome> items = table.scan();
        Iterator<Item> iter = items.iterator();
        Item item = iter.next();

        assertEquals(request.getLineNumber(), item.get("lineNumber"));
        assertEquals(request.getDate().toString(), item.get("date"));
        assertEquals(request.getStockValue(), ((BigDecimal)item.get("stock")).intValue());
    }
}
