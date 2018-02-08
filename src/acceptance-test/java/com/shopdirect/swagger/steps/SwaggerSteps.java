package com.shopdirect.swagger.steps;

import com.shopdirect.swagger.page.SwaggerPageCaller;
import com.shopdirect.acceptancetest.CucumberStepsDefinition;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SwaggerSteps extends CucumberStepsDefinition {

    @Autowired
    private SwaggerPageCaller swaggerPageCaller;

    @When("^I call the Swagger-UI API$")
    public void i_call_the_Swagger_UI_API() throws Throwable {
        swaggerPageCaller.get();
    }

    @Then("^I am returned a title of \"([^\"]*)\"$")
    public void i_am_returned_a_title_of(String expectedTitle) throws Throwable {
        String actualTitle = swaggerPageCaller.getTitle();
        assertThat(actualTitle.contains("<title>" + expectedTitle + "</title>"), is(true));
    }

}
