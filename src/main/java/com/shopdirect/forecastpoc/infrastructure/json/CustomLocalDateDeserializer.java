package com.shopdirect.forecastpoc.infrastructure.json;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.format.DateTimeFormatter;

public class CustomLocalDateDeserializer extends LocalDateDeserializer {
    public CustomLocalDateDeserializer() {
        super(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
