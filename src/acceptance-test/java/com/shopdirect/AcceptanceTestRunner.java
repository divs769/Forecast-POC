package com.shopdirect;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/acceptance-test/resources", plugin = {"pretty", "html:build/cucumber"})
public class AcceptanceTestRunner {
}