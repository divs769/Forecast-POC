package com.shopdirect.forecastpoc.infrastructure.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ForecastResource {

    @RequestMapping(method = GET, path = "/{weeks}")
    public List getForecastResult(@PathVariable int weeks) {
        return Collections.emptyList();
    }
}
