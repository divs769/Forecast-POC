package com.shopdirect.forecastpoc.infrastructure.repository;

import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@EnableScan
public interface ProductRepository extends CrudRepository<ProductStockData, UUID> {
}
