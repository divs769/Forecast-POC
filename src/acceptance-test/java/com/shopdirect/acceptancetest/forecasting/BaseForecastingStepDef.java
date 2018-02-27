package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.*;
import com.shopdirect.acceptancetest.CucumberStepsDefinition;
import com.shopdirect.acceptancetest.LatestResponse;
import com.shopdirect.acceptancetest.configuration.TestResponseErrorHandler;
import com.shopdirect.forecastpoc.infrastructure.model.LineStockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static com.shopdirect.acceptancetest.configuration.TestConfiguration.STOCK_DATA_TB;

public abstract class BaseForecastingStepDef extends CucumberStepsDefinition {

    protected static final String BASE_ENDPOINT = "http://localhost:8080";
    protected static final String ENDPOINT = BASE_ENDPOINT+"/forecast";
    protected static final String TABLE = "forecast_stock";

    protected RestTemplate restTemplate;
    protected LatestResponse latestResponse;
    protected DynamoDB db;
    protected DynamoDBMapper dynamoDBMapper;

    @Autowired
    public BaseForecastingStepDef(RestTemplate restTemplate, LatestResponse latestResponse, @Qualifier("testClient") AmazonDynamoDB db) {
        this.restTemplate = restTemplate;
        this.latestResponse = latestResponse;
        this.db = new DynamoDB(db);
        this.dynamoDBMapper = new DynamoDBMapper(db);
        this.restTemplate.setErrorHandler(new TestResponseErrorHandler());
    }

    protected Table createTable(DynamoDB db) {
        TableCollection<ListTablesResult> tables = db.listTables();
        for (Table table: tables) {
            if (STOCK_DATA_TB.equals(table.getTableName())) {
                return table;
            }
        }
        Table table = db.createTable(createTableRequest());
        try {
            table.waitForActive();
            return table;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CreateTableRequest createTableRequest() {
        return new CreateTableRequest()
                .withTableName(STOCK_DATA_TB)
                .withKeySchema(Arrays.asList(new KeySchemaElement()
                                .withAttributeName("lineNumber")
                                .withKeyType(KeyType.HASH),
                        new KeySchemaElement()
                                .withAttributeName("date")
                                .withKeyType(KeyType.RANGE)
                ))
                .withAttributeDefinitions(Arrays.asList(
                        new AttributeDefinition()
                                .withAttributeName("lineNumber")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("date")
                                .withAttributeType(ScalarAttributeType.S)))
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L));
    }

    protected void addItem(LineStockData lineStockData) {
        Table table = db.getTable(STOCK_DATA_TB);
        Item item = new Item()
                .withString("lineNumber", lineStockData.getLineNumber())
                .withString("date", lineStockData.getDate().toString())
                .withLong("stock", lineStockData.getStockValue());
        table.putItem(item);
    }
}
