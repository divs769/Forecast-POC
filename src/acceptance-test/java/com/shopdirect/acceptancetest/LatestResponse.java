package com.shopdirect.acceptancetest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LatestResponse {

    private ResponseEntity responseEntity;

    public ResponseEntity getResponse() {
        return responseEntity;
    }

    public void setResponse(ResponseEntity output) {
        this.responseEntity = output;
    }
}
