package com.shopdirect.forecastpoc.infrastructure.dao;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shopdirect.forecastpoc.infrastructure.model.LineStockData;

import java.util.List;
import java.util.Map;

public interface LineStockDao {
    List<LineStockData> getAll();

    List<LineStockData> getByLineNumber(String lineNumber);

    void saveItem(Map<String, AttributeValue> values);
}
