package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class ForecastingModelResult {
    private List<StockDataItem> forecastedValues;

    private Double error;

    private String name;

    public ForecastingModelResult(List<StockDataItem> forecastedValues, Double error, String name) {
        this.forecastedValues = forecastedValues;
        this.error = error;
        this.name = name;
    }

    public ForecastingModelResult(){}
    
    public List<StockDataItem> getForecastedValues() {
        return forecastedValues;
    }

    public Double getError() {
        return error;
    }

    public String getName() {
        return name;
    }
}
