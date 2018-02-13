package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class PastForecastingResult {
    private List<ForecastingModelResult> forecastings;

    private List<ProductStockData> historicData;

    private List<ProductStockData> actualValues;

    public PastForecastingResult(List<ForecastingModelResult> forecastings,
                                 List<ProductStockData> historicData,
                                 List<ProductStockData> actualValues) {
        this.forecastings = forecastings;
        this.historicData = historicData;
        this.actualValues = actualValues;
    }

    public List<ForecastingModelResult> getForecastings() {
        return forecastings;
    }

    public List<ProductStockData> getHistoricData() {
        return historicData;
    }

    public List<ProductStockData> getActualValues() {
        return actualValues;
    }
}
