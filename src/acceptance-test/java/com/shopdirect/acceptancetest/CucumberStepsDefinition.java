package com.shopdirect.acceptancetest;

import com.shopdirect.acceptancetest.configuration.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(
        classes = TestConfiguration.class)
@SpringBootTest
public class CucumberStepsDefinition {
}
