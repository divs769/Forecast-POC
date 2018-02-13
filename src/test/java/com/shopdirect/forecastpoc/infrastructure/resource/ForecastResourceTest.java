package com.shopdirect.forecastpoc.infrastructure.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(ForecastResource.class)
public class ForecastResourceTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnForecastResultWhenGetCalled() throws Exception {
        MvcResult result = mvc.perform(get("/1")).andExpect(status().isOk()).andReturn();

    }
}