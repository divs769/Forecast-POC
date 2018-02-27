package com.shopdirect.forecastpoc.infrastructure.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class InMemoryProductAndCategoriesDao implements ProductsAndCategoriesDao {
    private Map<String, Map<String, List<String>>> hierarchy;

    public InMemoryProductAndCategoriesDao(Map<String, Map<String, List<String>>> hierachy) {
        this.hierarchy = hierachy;
    }

    @Autowired
    public InMemoryProductAndCategoriesDao(){
        hierarchy = new HashMap<>();
        Map<String, List<String>> shirts = new HashMap<>();
        shirts.put("Basic shirt", Arrays.asList("8M417", "8M418", "8M419"));
        shirts.put("Long sleeve shirt", Arrays.asList("8M420", "8M421", "8M422"));
        hierarchy.put("shirts", shirts);
        Map<String, List<String>> jeans = new HashMap<>();
        jeans.put("Basic jeans", Arrays.asList("9M417", "9M418"));
        jeans.put("Ripped jeans", Arrays.asList("9M420", "9M421"));
        hierarchy.put("jeans", jeans);
    }

    @Override
    public Stream<String> getLineNumbersByCategory(String category) {
        return hierarchy.get(category).values().stream().flatMap(list -> list.stream());
    }

    @Override
    public Stream<String> getLineNumbersByProduct(String product) {
        return hierarchy.values().stream()
                .filter(mapProducts -> mapProducts.containsKey(product))
                .flatMap(map -> map.get(product).stream());
    }
}
