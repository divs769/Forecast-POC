package com.shopdirect.forecastpoc.infrastructure.dao;

import com.shopdirect.forecastpoc.infrastructure.service.StockForecastingService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class InMemoryProductAndCategoriesDaoTest {
    private Map<String, Map<String, List<String>>> hierarchy;
    private ProductsAndCategoriesDao dao;

    @Before
    public void setUp() throws Exception {
        hierarchy = new HashMap<>();
        Map<String, List<String>> shirts = new HashMap<>();
        shirts.put("Basic shirt", Arrays.asList("8M417", "8M418", "8M419"));
        shirts.put("Long sleeve shirt", Arrays.asList("8M420", "8M421", "8M422"));
        hierarchy.put("shirts", shirts);
        Map<String, List<String>> jeans = new HashMap<>();
        jeans.put("Basic jeans", Arrays.asList("9M417", "9M418"));
        jeans.put("Ripped jeans", Arrays.asList("9M420", "9M421"));
        hierarchy.put("jeans", jeans);
        dao = new InMemoryProductAndCategoriesDao(hierarchy);
    }

    @Test
    public void testLineNumbersByEachCategory() {
        testLineNumbersByCategory("shirts");
        testLineNumbersByCategory("jeans");
    }

    @Test
    public void testLineNumbersByEachProduct() {
        testLineNumberByProduct("shirts", "Basic shirt");
        testLineNumberByProduct("shirts", "Long sleeve shirt");
        testLineNumberByProduct("jeans", "Basic jeans");
        testLineNumberByProduct("jeans", "Ripped jeans");
    }

    private void testLineNumberByProduct(String category, String product){
        List<String> lineNumbers = dao.getLineNumbersByProduct(product).collect(Collectors.toList());
        List<String> expectedLineNumbers = hierarchy.get(category).get(product);
        lineNumbersAssertions(expectedLineNumbers, lineNumbers);
    }

    private void testLineNumbersByCategory(String category) {
        List<String> lineNumbers = dao.getLineNumbersByCategory(category).collect(Collectors.toList());

        List<String> expectedLineNumbers = new ArrayList<>();
        for(List<String> lines : hierarchy.get(category).values()){
            expectedLineNumbers.addAll(lines);
        }
        lineNumbersAssertions(expectedLineNumbers, lineNumbers);
    }

    private void lineNumbersAssertions(List<String> expectedLineNumbers, List<String> lineNumbers) {
        assertEquals(expectedLineNumbers.size() ,lineNumbers.size());
        for(int i = 0; i < lineNumbers.size(); i++){
            assertEquals(expectedLineNumbers.get(i), lineNumbers.get(i));
        }
    }
}