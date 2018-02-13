package com.shopdirect.forecastpoc.infrastructure.service;

import com.google.common.collect.Lists;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingModelResult;
import com.shopdirect.forecastpoc.infrastructure.model.PastForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import com.shopdirect.forecastpoc.infrastructure.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component
public class StockForecastingService {

    private final ProductRepository repository;

    @Autowired
    public StockForecastingService(ProductRepository repository) {
        this.repository = repository;
    }

    public PastForecastingResult getPastForecasting(int numWeeks) {
        List<ProductStockData> fullProductStockData = Lists.newArrayList(repository.findAll());

        Map<LocalDate, ProductStockData> actualValues = getLastNProductStocks(numWeeks, fullProductStockData)
                .collect(toMap(ProductStockData::getDate, prod -> prod));
        Stream<ProductStockData> historicData = removeLastNDates(numWeeks, fullProductStockData);
        Stream<LocalDate> dates = getLastNDates(numWeeks, fullProductStockData);
        List<ProductStockData> naivePredictions = StockForecastingModels.naivePrediction(historicData, dates)
                .collect(Collectors.toList());
        Double naiveError = calculateError(naivePredictions, actualValues);

        historicData = removeLastNDates(numWeeks, fullProductStockData);
        dates = getLastNDates(numWeeks, fullProductStockData);
        List<ProductStockData> averagePredictions = StockForecastingModels.averagePrediction(historicData, dates)
                .collect(Collectors.toList());
        Double averageError = calculateError(averagePredictions, actualValues);

        List<ForecastingModelResult> forecastings = Stream.of(new ForecastingModelResult(naivePredictions, naiveError, "naive"),
                new ForecastingModelResult(averagePredictions, averageError, "average"))
                .sorted(Comparator.comparing(ForecastingModelResult::getError)).collect(Collectors.toList());
        List<ProductStockData> historicDataList = removeLastNDates(numWeeks, fullProductStockData).collect(Collectors.toList());
        List<ProductStockData> actualValuesList = actualValues.values().stream()
                .sorted(Comparator.comparing(ProductStockData::getDate))
                .collect(Collectors.toList());
        return new PastForecastingResult(forecastings, historicDataList, actualValuesList);
    }

    private Stream<ProductStockData> getLastNProductStocks(int numWeeks, List<ProductStockData> fullProductStockData) {
        return fullProductStockData.stream()
                .sorted(Comparator.comparing(ProductStockData::getDate))
                .skip(Math.max(0, fullProductStockData.size() - numWeeks));
    }

    private Stream<LocalDate> getLastNDates(int numWeeks, List<ProductStockData> fullProductStockData) {
        return getLastNProductStocks(numWeeks, fullProductStockData)
                    .map(ps -> ps.getDate());
    }

    private Stream<ProductStockData> removeLastNDates(int numWeeks, List<ProductStockData> fullProductStockData) {
        return fullProductStockData.stream()
                    .sorted(Comparator.comparing(ProductStockData::getDate))
                    .limit(Math.max(0, fullProductStockData.size() - numWeeks));
    }

    private Double calculateError(List<ProductStockData> forecastedValues, Map<LocalDate, ProductStockData> actualValues){
        OptionalDouble error = forecastedValues.stream().map(prod ->
                ((double) Math.abs(prod.getStockValue() - actualValues.get(prod.getDate()).getStockValue()))
                            / (1 + actualValues.get(prod.getDate()).getStockValue()))
                .mapToDouble(elem -> elem
                )
                .average();

        return error.isPresent() ? 100 * error.getAsDouble() : null;
    }
}
