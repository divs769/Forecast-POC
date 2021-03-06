package com.shopdirect.forecastpoc.infrastructure.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopdirect.forecastpoc.infrastructure.model.ForecastingResult;
import com.shopdirect.forecastpoc.infrastructure.model.ProductHierarchy;
import com.shopdirect.forecastpoc.infrastructure.model.LineStockData;
import com.shopdirect.forecastpoc.infrastructure.service.StockForecastingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(ForecastResource.class)
public class ForecastResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StockForecastingService service;

    @Test
    public void shouldReturnForecastResultWhenGetCalledWithWeeks() throws Exception {
        mvc.perform(get("/forecast/1/lineNumber/8M417")).andExpect(status().isOk());

        verify(service).getForecastings(1, ProductHierarchy.LINE_NUMBER,"8M417");
    }

    @Test
    public void shouldReturnForecastResultWhenGetCalledWithWeeksAnd() throws Exception {
        LocalDate date = LocalDate.now();
        mvc.perform(get("/forecast/1/lineNumber/8M417/" + date.toString())).andExpect(status().isOk());

        verify(service).getForecastings(1, ProductHierarchy.LINE_NUMBER,"8M417", date);
    }

    @Test
    public void shouldSaveStockData() throws Exception {
        LineStockData data = new LineStockData(LocalDate.now(), 100L);
        mvc.perform(post("/forecast")
                .content(mapper.writeValueAsString(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).saveStockData(isA(LineStockData.class));
    }
}