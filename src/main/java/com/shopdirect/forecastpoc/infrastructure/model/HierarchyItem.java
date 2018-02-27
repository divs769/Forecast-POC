package com.shopdirect.forecastpoc.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HierarchyItem {
    private ProductHierarchy hierarchyType;

    private String item;

    @JsonCreator
    public HierarchyItem(@JsonProperty("type") ProductHierarchy hierarchyType, @JsonProperty("value") String item) {
        this.hierarchyType = hierarchyType;
        this.item = item;
    }

    public ProductHierarchy getHierarchyType() {
        return hierarchyType;
    }

    public String getItem() {
        return item;
    }

    @Override
    public String toString() {
        return hierarchyType.toString() + "/" +item;
    }
}
