package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class CustomisedModelResult extends ForecastingModelResult {
    private long id;

    public CustomisedModelResult(long id, List<StockDataItem> forecastedValues, Double error, String name) {
        super(forecastedValues, error, name);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
