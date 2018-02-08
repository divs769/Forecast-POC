package com.shopdirect.swagger.page;

import com.shopdirect.acceptancetest.LatestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SwaggerPageCaller {

public static final String BASE_URI = "http://localhost:8080";
public static final String SWAGGER_PATH = "/swagger-ui.html";
public static final String SWAGGER_URI = BASE_URI + SWAGGER_PATH;

    @Autowired RestTemplate restTemplate;
    @Autowired
    LatestResponse latestResponse;

    public void get() {
        ResponseEntity<String> swaggerResponseEntity = restTemplate.getForEntity(SWAGGER_URI, String.class);
        latestResponse.setResponse(swaggerResponseEntity);
    }

    public String getTitle() {
        String swaggerTitle = (String) latestResponse.getResponse().getBody();
        return swaggerTitle;
    }
}
