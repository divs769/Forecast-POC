package com.shopdirect.forecastpoc.infrastructure.service;

import com.shopdirect.forecastpoc.infrastructure.dao.CustomisedModelsDao;
import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomiseModelsService {
    private final CustomisedModelsDao customisedModelsDao;

    @Autowired
    public CustomiseModelsService(CustomisedModelsDao customisedModelsDao) {
        this.customisedModelsDao = customisedModelsDao;
    }

    public List<CustomisedModel> getCustomisedModels(HierarchyItem item) {
        return customisedModelsDao.getCustomisedModels(item);
    }

    public long saveModel(CustomisedModel data) throws Exception {
        return customisedModelsDao.saveCustomisedModel(data);
    }
}
