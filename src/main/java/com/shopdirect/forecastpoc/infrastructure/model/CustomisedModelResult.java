package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class CustomisedModelResult extends ForecastingModelResult {
    private long id;
    private String clonedModel;
    private String comment;

    public CustomisedModelResult(long id, List<StockDataItem> forecastedValues,
                                 Double error, String name, String clonedModel,
                                 String comment) {
        super(forecastedValues, error, name);
        this.id = id;
        this.clonedModel = clonedModel;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public String getClonedModel() {
        return clonedModel;
    }

    public String getComment() {
        return comment;
    }
}
