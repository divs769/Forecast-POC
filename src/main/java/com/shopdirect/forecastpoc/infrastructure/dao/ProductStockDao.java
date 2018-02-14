package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;

import java.util.List;

public interface ProductStockDao {
    List<ProductStockData> getAll();
}
