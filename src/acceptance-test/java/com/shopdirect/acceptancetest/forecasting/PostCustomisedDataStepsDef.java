package com.shopdirect.acceptancetest.forecasting;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.shopdirect.acceptancetest.LatestResponse;
import com.shopdirect.acceptancetest.model.AddCustomisedModelRequest;
import com.shopdirect.forecastpoc.infrastructure.dao.CustomisedModelsDao;
import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.model.HierarchyItem;
import com.shopdirect.forecastpoc.infrastructure.model.ProductHierarchy;
import com.shopdirect.forecastpoc.infrastructure.model.StockDataItem;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostCustomisedDataStepsDef extends BaseForecastingStepDef {

    private CustomisedModelsDao customisedModelsDao;
    private AddCustomisedModelRequest request;
    private static final String CUSTOMISATION_ENDPOINT = BASE_ENDPOINT+"/model";

    @Autowired
    public PostCustomisedDataStepsDef(RestTemplate restTemplate, LatestResponse latestResponse,
                                      AmazonDynamoDB db,
                                      CustomisedModelsDao customisedModelsDao) {
        super(restTemplate, latestResponse, db);
        this.customisedModelsDao = customisedModelsDao;
    }

    @Given("^there is no customised model inserted for hierarchy \"([^\"]*)\" and value \"([^\"]*)\"$")
    public void thereIsNoCustomisedModelInsertedForHierarchyAndValue(String hierarchyType, String value) throws Throwable {
        assertEquals(0, customisedModelsDao.getCustomisedModels(
                new HierarchyItem(ProductHierarchy.getProductHierarchy(hierarchyType), value)).size());
    }

    @Given("^a payload with customised data for hierarchy \"([^\"]*)\" and value \"([^\"]*)\"$")
    public void aPayloadWithCustomisedDataForHierarchyAndValue(String hierarchyType, String value) throws Throwable {
        HierarchyItem item = new HierarchyItem(ProductHierarchy.getProductHierarchy(hierarchyType), value);
        request = new AddCustomisedModelRequest("Customised 1", "naive",
                item, Arrays.asList(new StockDataItem(LocalDate.of(2018, Month.FEBRUARY, 2), 10),
                new StockDataItem(LocalDate.of(2018, Month.FEBRUARY, 9), 20)
        ) ,"Comment 1");
    }

    @When("^the post customised data endpoint is called$")
    public void thePostEndpointIsCalled() throws Throwable {
        latestResponse.setResponse(restTemplate.postForEntity(CUSTOMISATION_ENDPOINT, request, String.class));
    }

    @And("^new customisation is inserted$")
    public void newCustomisationIsInserted() throws Throwable {
        List<CustomisedModel> models = customisedModelsDao.getCustomisedModels(request.getItem());
        assertEquals(1,  models.size());
        CustomisedModel model = models.get(0);
        assertTrue(model.getId() > 0);
        assertEquals(request.getName(), model.getName());
        assertEquals(request.getClonedModel(), model.getClonedModel());
        assertEquals(request.getComment(), model.getComment());
        assertEquals(request.getItem().toString(), model.getItem().toString());
        assertEquals(request.getForecastedValues().size(), model.getForecastedValues().size());
        for(int i = 0; i < request.getForecastedValues().size(); i++){
            assertStockDataItems(request.getForecastedValues().get(i), model.getForecastedValues().get(i));
        }
    }

    private void assertStockDataItems(StockDataItem expected, StockDataItem actual){
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getStock(), actual.getStock());
    }

    @And("^the id of the new customisation is returned$")
    public void theIdOfTheNewCustomisationIsReturned() throws Throwable {
        CustomisedModel model = customisedModelsDao.getCustomisedModels(request.getItem()).get(0);
        assertEquals(model.getId()+"", latestResponse.getResponse().getBody());
    }
}
