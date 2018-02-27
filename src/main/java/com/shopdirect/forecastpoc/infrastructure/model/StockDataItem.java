package com.shopdirect.forecastpoc.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class StockDataItem {
    private LocalDate date;
    private long stock;

    @JsonCreator
    public StockDataItem(@JsonProperty("date") LocalDate date, @JsonProperty("stock") long stock) {
        this.date = date;
        this.stock = stock;
    }

    public StockDataItem() {}

    public LocalDate getDate() {
        return date;
    }

    public long getStock() {
        return stock;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }
}
