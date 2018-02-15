package com.shopdirect.forecastpoc.infrastructure.resource;

import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductStockData;
import com.shopdirect.forecastpoc.infrastructure.service.StockForecastingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin
@RequestMapping("/forecast")
public class ForecastResource {

    private final StockForecastingService service;

    @Autowired
    public ForecastResource(StockForecastingService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/{weeks}/{startDate}")
    public ForecastingResult getForecastResult(@PathVariable int weeks, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return service.getForecastings(weeks, startDate);
    }

    @RequestMapping(method = GET, path = "/{weeks}")
    public ForecastingResult getForecastResult(@PathVariable int weeks) {
        return service.getForecastings(weeks);
    }

    @RequestMapping(method = POST)
    public void saveHistoricData(@RequestBody ProductStockData data) {
        service.saveStockData(data);
    }
}
