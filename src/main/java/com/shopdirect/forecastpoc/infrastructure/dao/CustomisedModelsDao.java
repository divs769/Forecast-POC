package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;

import java.util.List;

public interface CustomisedModelsDao {
    public List<CustomisedModel> getCustomisedModels(HierarchyItem item);

    public long saveCustomisedModel(CustomisedModel model) throws Exception;
}
