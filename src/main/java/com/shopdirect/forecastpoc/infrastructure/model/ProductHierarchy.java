package com.shopdirect.forecastpoc.infrastructure.model;

public enum ProductHierarchy {
    CATEGORY,
    PRODUCT,
    LINE_NUMBER;

    public static ProductHierarchy getProductHierarchy(String hierarchy){
        if(hierarchy.equals("category")){
            return CATEGORY;
        }else if(hierarchy.equals("product")){
            return PRODUCT;
        }else if(hierarchy.equals("lineNumber")){
            return LINE_NUMBER;
        }else{
            return null;
        }
    }
}
