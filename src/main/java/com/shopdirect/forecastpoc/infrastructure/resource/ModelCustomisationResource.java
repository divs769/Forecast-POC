package com.shopdirect.forecastpoc.infrastructure.resource;

import com.shopdirect.forecastpoc.infrastructure.model.CustomisedModel;
import com.shopdirect.forecastpoc.infrastructure.service.CustomiseModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin
@RequestMapping("/model")
public class ModelCustomisationResource {

    private final CustomiseModelsService customiseModelsService;

    @Autowired
    public ModelCustomisationResource(CustomiseModelsService customiseModelsService) {
        this.customiseModelsService = customiseModelsService;
    }

    @RequestMapping(method = POST)
    public ResponseEntity<String> saveCustomisedData(@RequestBody CustomisedModel data) {
        try {
            Long id = customiseModelsService.saveModel(data);
            return ResponseEntity.ok(id.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected error!");
        }
    }
}
