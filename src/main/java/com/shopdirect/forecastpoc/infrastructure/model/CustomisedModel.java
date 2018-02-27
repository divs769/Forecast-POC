package com.shopdirect.forecastpoc.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class CustomisedModel {
    private long id;
    private String name;
    private String clonedModel;
    private HierarchyItem item;
    private List<StockDataItem> forecastedValues;
    private String comment;

    @JsonCreator
    public CustomisedModel(@JsonProperty("id") long id,
                            @JsonProperty("name") String name,
                           @JsonProperty("cloneModel") String clonedModel,
                           @JsonProperty("item") HierarchyItem item,
                           @JsonProperty("values") List<StockDataItem> forecastedValues,
                           @JsonProperty("comment") String comment) {
        this.id = id;
        this.name = name;
        this.clonedModel = clonedModel;
        this.item = item;
        this.forecastedValues = forecastedValues;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
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

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
