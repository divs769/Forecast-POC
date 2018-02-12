package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.Date;

public class ProductStockData {
    private Date date;
    private long stockValue;

    public ProductStockData(Date date, long stockValue) {
        this.date = date;
        this.stockValue = stockValue;
    }

    public Date getDate() {
        return date;
    }

    public long getStockValue() {
        return stockValue;
    }
}
