package com.shopdirect.forecastpoc.infrastructure.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.shopdirect.forecastpoc.infrastructure.dao.LineStockDao;
import com.shopdirect.forecastpoc.infrastructure.dao.ProductsAndCategoriesDao;
import com.shopdirect.forecastpoc.infrastructure.model.*;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class StockForecastingService {
    private final LineStockDao lineStockDao;
    private final ProductsAndCategoriesDao productsAndCategoriesDao;
    private final CustomiseModelsService customiseModelsService;
    private Map<String, BiFunction<Stream<StockDataItem>, Stream<LocalDate>, Stream<StockDataItem>>> forecastingMethods;

    @Autowired
    public StockForecastingService(LineStockDao lineStockDao
            , ProductsAndCategoriesDao productAndCategoriesDao, CustomiseModelsService customiseModelsService) {
        this.lineStockDao = lineStockDao;
        this.productsAndCategoriesDao = productAndCategoriesDao;
        this.customiseModelsService = customiseModelsService;
        this.forecastingMethods = new HashMap<>();
        forecastingMethods.put("naive", StockForecastingModels::naivePrediction);
        forecastingMethods.put("average", StockForecastingModels::averagePrediction);
    }

    public ForecastingResult getForecastings(int numWeeks, String lineNumber){
        return this.getForecastings(numWeeks, lineNumber, null);
    }

    public ForecastingResult getForecastings(int numWeeks, String lineNumber, LocalDate startDate) {
        return getForecastings(numWeeks, ProductHierarchy.LINE_NUMBER, lineNumber, startDate);
    }
    public ForecastingResult getForecastings(int numWeeks, ProductHierarchy type, String hierarchyValue) {
        return getForecastings(numWeeks, type, hierarchyValue, null);
    }

    public ForecastingResult getForecastings(int numWeeks, ProductHierarchy type, String hierarchyValue, LocalDate startDate) {
        List<StockDataItem> fullProductStockData = getProductStockData(type, hierarchyValue);

        if(fullProductStockData == null || fullProductStockData.isEmpty()){
            return new ForecastingResult(Arrays.asList(), fullProductStockData);
        }
        List<ForecastingModelResult> forecastings = calculatePastForecastings(fullProductStockData, startDate);
        List<StockDataItem> filteredProductStockData = filterByStartDate(startDate, fullProductStockData);

        List<LocalDate> nextDates = getNextWeekDates(numWeeks, fullProductStockData.get(fullProductStockData.size() - 1), startDate)
                .collect(Collectors.toList());
        
         for(String name : forecastingMethods.keySet()){
             Stream<StockDataItem> newForecastings = forecastingMethods.get(name)
                     .apply(fullProductStockData.stream(), nextDates.stream());

             getForecastedValuesByName(forecastings, name)
                     .addAll(newForecastings.collect(Collectors.toList()));
         }

        List<ForecastingModelResult> forecastingsByCustomisedModels = getForecastingsCustomisedModels(
                numWeeks,
                new HierarchyItem(type, hierarchyValue),
                forecastings,
                filteredProductStockData);
        List<ForecastingModelResult> allForecastings;
        if(forecastingsByCustomisedModels.size() > 0){
             allForecastings = Stream.of(forecastings, forecastingsByCustomisedModels)
                    .flatMap(item -> item.stream())
                    .sorted(Comparator.comparing(ForecastingModelResult::getError))
                    .collect(toList());
        }else{
            allForecastings = forecastings;
        }

        return new ForecastingResult(allForecastings, filteredProductStockData );
    }

    public List<StockDataItem> getForecastedValuesByName(List<ForecastingModelResult> forecastings, String name) {
        return forecastings.stream()
                .filter(forecastingResult -> forecastingResult.getName().equals(name))
                .limit(1)
                .collect(Collectors.toList())
                .get(0)
                .getForecastedValues();
    }

    private List<ForecastingModelResult> getForecastingsCustomisedModels(int numWeeks,
                                                                         HierarchyItem item,
                                                                         List<ForecastingModelResult> forecastings,
                                                                         List<StockDataItem> filteredProductStockData) {
        List<CustomisedModel> customisedModels = customiseModelsService.getCustomisedModels(item);
        Map<LocalDate, StockDataItem> actualValues = filteredProductStockData.stream()
                .collect(toMap(StockDataItem::getDate, stockItem -> stockItem));
        return customisedModels.stream()
                .map(customisedModel ->
                    getForecastingModelResultFromCustomisation(numWeeks, forecastings, customisedModel, actualValues))
                .collect(toList());
    }

    private ForecastingModelResult getForecastingModelResultFromCustomisation(int numWeeks,
                                                                            List<ForecastingModelResult> forecastings,
                                                                             CustomisedModel customisedModel,
                                                                             Map<LocalDate, StockDataItem> actualValues) {

        Map<LocalDate, StockDataItem> customisedItemsMap = customisedModel.getForecastedValues().stream()
                .collect(toMap(StockDataItem::getDate, item -> item));
        List<StockDataItem> forecastedValues = getForecastedValuesByName(forecastings, customisedModel.getClonedModel());
        List<StockDataItem> allCustomisedForecastedValues = forecastedValues.stream()
                .map(item -> {
                    if(customisedItemsMap.containsKey(item.getDate())){
                        return customisedItemsMap.get(item.getDate());
                    }else{
                        return item;
                    }
                }).collect(toList());
        Stream<StockDataItem> pastItems = allCustomisedForecastedValues
                .stream().limit(forecastedValues.size() - numWeeks);
        Double error = calculateError(pastItems, actualValues);
        return new CustomisedModelResult(customisedModel.getId(), allCustomisedForecastedValues, error, customisedModel.getName(), customisedModel.getClonedModel());
    }

    private List<StockDataItem> getProductStockData(ProductHierarchy type, String hierarchyValue) {
        List<StockDataItem> products = null;
        if(type == ProductHierarchy.LINE_NUMBER){
            products = getByLineNumber(hierarchyValue).collect(Collectors.toList());
        }else if(type == ProductHierarchy.CATEGORY || type == ProductHierarchy.PRODUCT){
            Stream<String> lineNumbers;
            if(type == ProductHierarchy.CATEGORY){
                lineNumbers = productsAndCategoriesDao.getLineNumbersByCategory(hierarchyValue);
            }else{
                lineNumbers = productsAndCategoriesDao.getLineNumbersByProduct(hierarchyValue);
            }
            Map<LocalDate, Long> map = lineNumbers.map(lineNumber -> getByLineNumber(lineNumber))
                    .flatMap(listProductStock -> listProductStock)
                    .collect(Collectors.groupingBy(StockDataItem::getDate,
                            Collectors.summingLong(StockDataItem::getStock)));
            products = map.entrySet().stream()
                    .map(mapping ->
                        new StockDataItem(mapping.getKey(), mapping.getValue())
                    )
                    .sorted(Comparator.comparing(StockDataItem::getDate))
                    .collect(Collectors.toList());
        }
        return products;

    }

    private Stream<StockDataItem> getByLineNumber(String hierarchyValue) {
        return Lists.newArrayList(lineStockDao.getByLineNumber(hierarchyValue)).stream()
                .map(prod -> new StockDataItem(prod.getDate(), prod.getStockValue()))
                .sorted(Comparator.comparing(StockDataItem::getDate));
    }

    private List<StockDataItem> filterByStartDate(LocalDate startDate, List<StockDataItem> products){
        if(startDate != null){
            return products.stream()
                    .filter(prod -> prod.getDate().compareTo(startDate) >= 0)
                    .collect(Collectors.toList());
        }else{
            return products;
        }
    }

    /*
    -should get past forecasting values from a db in the future
    -For now, it recalculates the whole forecasting history
    */
    private Map<String, List<StockDataItem>> getPastForecastings(List<StockDataItem> fullProductStockData, LocalDate startDate){
        Stream<StockDataItem> forecasting, forecastings;
        LocalDate date;
        int productsBeforeStartDate = 1;
        Map<String, List<StockDataItem>> result = new HashMap<>();
        if(startDate != null){
            productsBeforeStartDate = (int) fullProductStockData.stream().filter(prod -> prod.getDate().isBefore(startDate)).count();
        }
        for(String name : forecastingMethods.keySet()) {
            forecastings = Stream.of();
            for (int i = productsBeforeStartDate; i < fullProductStockData.size(); i++) {
                date = fullProductStockData.get(i).getDate();
                forecasting = forecastingMethods.get(name)
                        .apply(fullProductStockData.stream().limit(i),
                        Stream.of(date));
                forecastings = Stream.concat(forecastings, forecasting);
            }
            result.put(name, forecastings.collect(Collectors.toList()));
        }
        return result;
    }

    public List<ForecastingModelResult> calculatePastForecastings(List<StockDataItem> fullProductStockData, LocalDate startDate){
        Map<LocalDate, StockDataItem> actualValues = fullProductStockData.stream().collect(toMap(StockDataItem::getDate, prod -> prod));

        Map<String, List<StockDataItem>> pastForecastings = getPastForecastings(fullProductStockData, startDate);
        return pastForecastings.entrySet().stream()
                .map(map -> {
                    List<StockDataItem> forecastings = map.getValue();
                    Double error = calculateError(forecastings, actualValues);
                    return new ForecastingModelResult(forecastings, error, map.getKey());
                })
                .sorted(Comparator.comparing(ForecastingModelResult::getError, Comparator.nullsLast(naturalOrder())))
                .collect(Collectors.toList());
    }

    private static Stream<LocalDate> getNextWeekDates(int numberWeeks, StockDataItem lastStockData, LocalDate startDate){
        LocalDate firstDate;
        if(startDate == null
                || startDate.isBefore(lastStockData.getDate())){
            firstDate = lastStockData.getDate().plusDays(7);
        }else{
            firstDate = startDate;
        }
        return Stream
                .iterate(firstDate,
                        localDate -> localDate.plusDays(7))
                .limit(numberWeeks);
    }

    private Double calculateError(Stream<StockDataItem> forecastedValues, Map<LocalDate, StockDataItem> actualValues){
        OptionalDouble error = forecastedValues.map(prod ->
                ((double) Math.abs(prod.getStock() - actualValues.get(prod.getDate()).getStock()))
                        / (1 + actualValues.get(prod.getDate()).getStock()))
                .mapToDouble(elem -> elem)
                .average();

        return error.isPresent() ? 100 * error.getAsDouble() : null;
    }

    private Double calculateError(List<StockDataItem> forecastedValues, Map<LocalDate, StockDataItem> actualValues){
        return calculateError(forecastedValues.stream(), actualValues);
    }

    public void saveStockData(LineStockData data) {
        lineStockDao.saveItem(ImmutableMap.<String, AttributeValue>builder()
                .put("lineNumber", new AttributeValue(data.getLineNumber()))
                .put("date", new AttributeValue(data.getDate().toString()))
                .put("stock", new AttributeValue().withN(String.valueOf(data.getStockValue())))
                .build());
    }
}
