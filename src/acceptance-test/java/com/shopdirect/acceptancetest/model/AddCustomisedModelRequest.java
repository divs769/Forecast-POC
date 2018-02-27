package com.shopdirect.acceptancetest.model;

import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;
import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;

import java.util.List;

public class AddCustomisedModelRequest {
    private String name;
    private String clonedModel;
    private HierarchyItem item;
    private List<StockDataItem> forecastedValues;
    private String comment;

    public AddCustomisedModelRequest(String name, String clonedModel, HierarchyItem item, List<StockDataItem> forecastedValues, String comment) {
        this.name = name;
        this.clonedModel = clonedModel;
        this.item = item;
        this.forecastedValues = forecastedValues;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getClonedModel() {
        return clonedModel;
    }

    public HierarchyItem getItem() {
        return item;
    }

    public List<StockDataItem> getForecastedValues() {
        return forecastedValues;
    }

    public String getComment() {
        return comment;
    }
}
