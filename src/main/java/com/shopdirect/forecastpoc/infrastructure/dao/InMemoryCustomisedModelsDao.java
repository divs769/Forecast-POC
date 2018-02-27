package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;
import com.shopdirect.forecastpoc.infrastructure.model.ProductHierarchy;
import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component
public class InMemoryCustomisedModelsDao implements CustomisedModelsDao {
    private Map<String, List<CustomisedModel>> models;
    private int nextId;

    public InMemoryCustomisedModelsDao(Map<String, List<CustomisedModel>> models) {
        this.models = models;
        this.nextId = 1;
    }

    @Autowired
    public InMemoryCustomisedModelsDao() {
        this.models = new HashMap<>();
        HierarchyItem item = new HierarchyItem(ProductHierarchy.LINE_NUMBER, "8M419");
        LocalDate initialDate = LocalDate.of(2018, Month.FEBRUARY, 12);
        models.put(item.toString(), Arrays.asList(
                new CustomisedModel(1,
                        "Customised 1","naive", item,
                        Arrays.asList(new StockDataItem(initialDate, 1080),
                                new StockDataItem(initialDate.plusWeeks(1), 1000),
                                new StockDataItem(initialDate.plusWeeks(2), 1020)
                        ), "Comment 1")
        ));
        this.nextId = 2;
    }

    @Override
    public List<CustomisedModel> getCustomisedModels(HierarchyItem item) {
        List<CustomisedModel> customisedModels = models.get(item.toString());
        if(customisedModels == null){
            return new ArrayList<>();
        }else{
            return customisedModels;
        }
    }

    @Override
    public long saveCustomisedModel(CustomisedModel model) throws Exception {
        String key = model.getItem().toString();
        List<CustomisedModel> customisedModels = models.get(key);
        if(customisedModels == null){
            customisedModels = new ArrayList<>();
            models.put(key, customisedModels);
        }
        model.setId(nextId);
        customisedModels.add(model);
        nextId++;
        return model.getId();
    }
}
