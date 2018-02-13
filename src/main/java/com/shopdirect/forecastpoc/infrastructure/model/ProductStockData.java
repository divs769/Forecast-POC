package com.shopdirect.forecastpoc.infrastructure.model;

import java.time.LocalDate;

public class ProductStockData {
    private LocalDate date;
    private long stockValue;

    public ProductStockData(LocalDate date, long stockValue) {
        this.date = date;
        this.stockValue = stockValue;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getStockValue() {
        return stockValue;
    }
}
