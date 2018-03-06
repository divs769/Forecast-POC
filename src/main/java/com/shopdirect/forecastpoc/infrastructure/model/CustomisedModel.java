package com.shopdirect.forecastpoc.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class CustomisedModel {
    private long id;
    private String name;
    private String clonedModel;
    private HierarchyItem item;
    private List<StockDataItem> forecastedValues;
    private String comment;

    @JsonCreator
    public CustomisedModel(@JsonProperty("id") @JsonInclude(NON_NULL) long id,
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

    public void setName(String name) {
        this.name = name;
    }
    
    public void setForecastedValues(List<StockDataItem> forecastedValues) {
        this.forecastedValues = forecastedValues;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
