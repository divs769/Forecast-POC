package com.shopdirect.acceptancetest.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class TestResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
    }
}
