package com.shopdirect.forecastpoc.infrastructure.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopdirect.forecastpoc.infrastructure.config.DynamoDBConfig;
import com.shopdirect.forecastpoc.infrastructure.model.converter.LocalDateConverter;

import java.time.LocalDate;

@DynamoDBTable(tableName = DynamoDBConfig.TABLE)
public class ProductStockData {
    @DynamoDBAttribute
    @DynamoDBHashKey
    private String lineNumber;
    @DynamoDBAttribute
    @DynamoDBRangeKey
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    private LocalDate date;
    @DynamoDBAttribute
    private long stockValue;

    @JsonCreator
    public ProductStockData(@JsonProperty("date") LocalDate date, @JsonProperty("stock") long stockValue) {
        this.date = date;
        this.stockValue = stockValue;
        this.lineNumber = "8M417";
    }

    public ProductStockData() {}

    public LocalDate getDate() {
        return date;
    }

    public long getStockValue() {
        return stockValue;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    @JsonProperty("lineNumber")
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStockValue(long stockValue) {
        this.stockValue = stockValue;
    }
}
