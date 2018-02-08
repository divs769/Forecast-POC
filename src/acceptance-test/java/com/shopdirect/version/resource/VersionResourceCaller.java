package com.shopdirect.version.resource;

import com.jayway.jsonpath.JsonPath;
import com.shopdirect.acceptancetest.LatestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VersionResourceCaller {

    public static final String BASE_URI = "http://localhost:8080";
    public static final String VERSION_PATH = "/version";
    public static final String VERSION_URI = BASE_URI + VERSION_PATH;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LatestResponse latestResponse;


    public void get() {
        ResponseEntity<String> namesResponseEntity = restTemplate.getForEntity(VERSION_URI, String.class);
        latestResponse.setResponse(namesResponseEntity);

    }

    public String getLastVersion() {
            String json = (String) latestResponse.getResponse().getBody();

        return JsonPath.read(json, "$.version.ServiceVersion");


    }

}
