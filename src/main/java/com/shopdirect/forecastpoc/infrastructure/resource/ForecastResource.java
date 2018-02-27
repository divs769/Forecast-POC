package com.shopdirect.forecastpoc.infrastructure.resource;

import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductHierarchy;
import com.shopdirect.forecastpoc.infrastructure.model.LineStockData;
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

    @RequestMapping(method = GET, path = "/{weeks}/{hierarchyType}/{hierarchyValue}/{startDate}")
    public ForecastingResult getForecastResult(@PathVariable int weeks,
                                               @PathVariable String hierarchyType,
                                               @PathVariable String hierarchyValue,
                                               @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return service.getForecastings(weeks, ProductHierarchy.getProductHierarchy(hierarchyType), hierarchyValue, startDate);
    }

    @RequestMapping(method = GET, path = "/{weeks}/{hierarchyType}/{hierarchyValue}")
    public ForecastingResult getForecastResult(@PathVariable int weeks, @PathVariable String hierarchyType,
                                               @PathVariable String hierarchyValue) {
        return service.getForecastings(weeks, ProductHierarchy.getProductHierarchy(hierarchyType), hierarchyValue);
    }

    @RequestMapping(method = POST)
    public void saveHistoricData(@RequestBody LineStockData data) {
        service.saveStockData(data);
    }
}
