package com.shopdirect.forecastpoc.infrastructure.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.shopdirect.forecastpoc.infrastructure.config.DynamoDBConfig.TABLE;
import static java.util.stream.Collectors.toList;

@Component
public class DynamoDbProductStockDao implements ProductStockDao {

    private AmazonDynamoDB db;

    @Autowired
    public DynamoDbProductStockDao(AmazonDynamoDB db) {
        this.db = db;
    }

    public List<ProductStockData> getAll(){
        ScanResult result = db.scan(new ScanRequest().withTableName(TABLE));
        return result.getItems().stream().map(item ->
                new ProductStockData(LocalDate.parse(item.get("date").getS()),
                        Long.parseLong(item.get("stock").getN()))).collect(toList());
    }


    public void saveItem(Map<String, AttributeValue> values) {
        db.putItem(TABLE, values);
    }
}
