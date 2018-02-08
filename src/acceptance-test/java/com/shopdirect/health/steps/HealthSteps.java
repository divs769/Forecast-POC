package com.shopdirect.health.steps;

import com.shopdirect.acceptancetest.CucumberStepsDefinition;
import com.shopdirect.health.resource.HealthResourceCaller;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HealthSteps extends CucumberStepsDefinition{

    @Autowired
    private HealthResourceCaller healthResourceCaller;

    @When("^I visit the health resource$")
    public void i_visit_the_health_resource() throws Throwable {
        healthResourceCaller.get();
    }

    @Then("^I get a 'OK' response$")
    public void i_get_a_OK_response() throws Throwable {
        HttpStatus actualStatus = healthResourceCaller.getHttpStatus();
        assertThat(actualStatus, is(HttpStatus.OK));
    }

    @Then("^the health status is 'UP'$")
    public void the_health_status_is_UP() throws Throwable {
        String healthBody = (String) healthResourceCaller.getBody();
        assertThat(healthBody.contains("UP"), is(true));
    }
}
