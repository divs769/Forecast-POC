package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;
import com.shopdirect.forecastpoc.infrastructure.model.ProductHierarchy;
import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.Assert.*;

public class InMemoryCustomisedModelsDaoTest {

    private CustomisedModelsDao dao;
    private LocalDate initialDate = LocalDate.of(2018, Month.FEBRUARY, 2);
    private List<CustomisedModel> basicModels;

    public InMemoryCustomisedModelsDaoTest(){
        HierarchyItem item = new HierarchyItem(ProductHierarchy.CATEGORY, "shirts");
        basicModels = Arrays.asList(
                new CustomisedModel(3,
                "Customised 1","naive", item,
                Arrays.asList(new StockDataItem(initialDate, 10),
                        new StockDataItem(initialDate.plusWeeks(1), 20)
                ), "Comment 1"),
                new CustomisedModel(4,
                "Customised 2","average", item,
                Arrays.asList(new StockDataItem(initialDate, 15),
                        new StockDataItem(initialDate.plusWeeks(1), 25)), "Comment 2"),
                new CustomisedModel(5,
                        "Customised 3","average", new HierarchyItem(ProductHierarchy.LINE_NUMBER, "testLine"),
                        Arrays.asList(new StockDataItem(initialDate, 15),
                                new StockDataItem(initialDate.plusWeeks(1), 25)), "Comment 3"));
    }

    public Map<String, List<CustomisedModel>> initMapWithValues(boolean emptyMap){
        Map<String, List<CustomisedModel>> models = new HashMap<>();
        HierarchyItem item = basicModels.get(0).getItem();
        if(!emptyMap){
            models.put(item.toString(), basicModels);
        }
        dao = new InMemoryCustomisedModelsDao(models);
        return models;
    }

    @Test
    public void getCustomisedModels() {
        HierarchyItem item = basicModels.get(0).getItem();
        Map<String, List<CustomisedModel>> models = initMapWithValues(false);
        List<CustomisedModel> actualModels = dao.getCustomisedModels(item);

        assertListsCustomisedModelsEquals(models.get(item.toString()), actualModels);
    }

    @Test
    public void testSaves() throws Exception {
        initMapWithValues(true);

        testSave(0, 0, 1);
        testSave(1, 0, 2);
        testSave(2, 2, 3);
    }

    private CustomisedModel cloneModel(CustomisedModel model){
        return new CustomisedModel(model.getId(), model.getName(), model.getClonedModel(),
                model.getItem(), model.getForecastedValues(), model.getComment());
    }

    public void testSave(int item, int subListStart, int subListEnd) throws Exception {
        CustomisedModel model = basicModels.get(item);
        dao.saveCustomisedModel(cloneModel(model));
        model.setId(item + 1);
        List<CustomisedModel> models = dao.getCustomisedModels(model.getItem());
        assertListsCustomisedModelsEquals(basicModels.subList(subListStart, subListEnd), models);
    }

    private void assertListsCustomisedModelsEquals(List<CustomisedModel> expectedModels,
                                                   List<CustomisedModel> actualModels){
        assertEquals(expectedModels.size(), actualModels.size());
        for(int i = 0; i < expectedModels.size(); i++){
            assertEquals(expectedModels.get(i).getId(), actualModels.get(i).getId());
            assertEquals(expectedModels.get(i).getName(), actualModels.get(i).getName());
            assertEquals(expectedModels.get(i).getClonedModel(), actualModels.get(i).getClonedModel());
            assertListsForecastedValuesEquals(expectedModels.get(i).getForecastedValues(),
                    actualModels.get(i).getForecastedValues());
        }
    }

    private void assertListsForecastedValuesEquals(List<StockDataItem> expectedValues,
                                                   List<StockDataItem> actualValues){
        assertEquals(expectedValues.size(), actualValues.size());
        for(int i = 0; i < expectedValues.size(); i++){
            assertEquals(expectedValues.get(i).getDate(), actualValues.get(i).getDate());
            assertEquals(expectedValues.get(i).getStock(), actualValues.get(i).getStock());
        }
    }
}