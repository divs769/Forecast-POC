package com.shopdirect.forecastpoc.infrastructure.resource;

import com.shopdirect.forecastpoc.infrastructure.model.PastForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.service.StockForecastingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ForecastResource {

    private final StockForecastingService service;

    @Autowired
    public ForecastResource(StockForecastingService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/{weeks}")
    public PastForecastingResult getForecastResult(@PathVariable int weeks) {
        return service.getPastForecasting(weeks);
    }
}
