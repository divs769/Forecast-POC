package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;

import java.util.List;
import java.util.Optional;

public interface CustomisedModelsDao {
    public List<CustomisedModel> getCustomisedModels(HierarchyItem item);

    public Optional<CustomisedModel> getCustomisedModel(long id);

    public void editCustomisedModel(CustomisedModel model);

    public long saveCustomisedModel(CustomisedModel model);

    public boolean nameExists(CustomisedModel model);
}
