package com.shopdirect.forecastpoc.infrastructure.model;

import java.util.List;

public class ForecastingResult {
    private List<ForecastingModelResult> forecastings;

    private List<StockDataItem> historicData;

    public ForecastingResult(List<ForecastingModelResult> forecastings,
                             List<StockDataItem> historicData) {
        this.forecastings = forecastings;
        this.historicData = historicData;
    }

    public ForecastingResult() {}

    public List<ForecastingModelResult> getForecastings() {
        return forecastings;
    }

    public List<StockDataItem> getHistoricData() {
        return historicData;
    }
}
