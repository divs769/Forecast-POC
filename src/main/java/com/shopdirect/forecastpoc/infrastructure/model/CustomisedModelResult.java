package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class CustomisedModelResult extends ForecastingModelResult {
    private long id;
    private String clonedModel;

    public CustomisedModelResult(long id, List<StockDataItem> forecastedValues, Double error, String name, String clonedModel) {
        super(forecastedValues, error, name);
        this.id = id;
        this.clonedModel = clonedModel;
    }

    public long getId() {
        return id;
    }

    public String getClonedModel() {
        return clonedModel;
    }
}
