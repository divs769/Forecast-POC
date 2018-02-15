package com.shopdirect.forecastpoc.infrastructure.dao;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;

import java.util.List;
import java.util.Map;

public interface ProductStockDao {
    List<ProductStockData> getAll();

    void saveItem(Map<String, AttributeValue> values);
}
