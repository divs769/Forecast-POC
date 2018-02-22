package com.shopdirect.forecastpoc.infrastructure.dao;

import java.util.stream.Stream;

public interface ProductsAndCategoriesDao {
    Stream<String> getLineNumbersByCategory(String category);
    Stream<String> getLineNumbersByProduct(String product);
}
