package com.shopdirect.acceptancetest.model;

import java.time.LocalDate;

public class AddStockDataRequest {

    private String lineNumber;
    private LocalDate date;
    private long stockValue;

    public AddStockDataRequest(String lineNumber, LocalDate date, long stockValue) {
        this.lineNumber = lineNumber;
        this.date = date;
        this.stockValue = stockValue;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getStockValue() {
        return stockValue;
    }
}
