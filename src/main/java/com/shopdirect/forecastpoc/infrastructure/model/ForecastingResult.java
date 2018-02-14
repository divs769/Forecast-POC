package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class ForecastingResult {
    private List<ForecastingModelResult> forecastings;

    private List<ProductStockData> historicData;

    public ForecastingResult(List<ForecastingModelResult> forecastings,
                             List<ProductStockData> historicData) {
        this.forecastings = forecastings;
        this.historicData = historicData;
    }

    public ForecastingResult() {}

    public List<ForecastingModelResult> getForecastings() {
        return forecastings;
    }

    public List<ProductStockData> getHistoricData() {
        return historicData;
    }
}
