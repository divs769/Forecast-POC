package com.shopdirect.health.resource;

import com.shopdirect.acceptancetest.LatestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthResourceCaller {

    public static final String BASE_URI = "http://localhost:8080";
    public static final String HEALTH_PATH = "/health";
    public static final String HEALTH_URI = BASE_URI + HEALTH_PATH;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LatestResponse latestResponse;

    public void get() {
        ResponseEntity<String> healthResponseEntity = restTemplate.getForEntity(HEALTH_URI, String.class);
        latestResponse.setResponse(healthResponseEntity);
    }

    public HttpStatus getHttpStatus() {
        HttpStatus healthStatus = latestResponse.getResponse().getStatusCode();
        return healthStatus;
    }

    public String getBody() {
        String healthBody = (String) latestResponse.getResponse().getBody();
        return healthBody;
    }
}
