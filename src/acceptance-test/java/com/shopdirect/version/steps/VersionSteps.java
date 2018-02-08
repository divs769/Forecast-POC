package com.shopdirect.version.steps;

import com.shopdirect.acceptancetest.CucumberStepsDefinition;
import com.shopdirect.version.resource.VersionResourceCaller;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class VersionSteps extends CucumberStepsDefinition {

    public static final String VERSION_REGEX = "^[0-9]+[.][0-9]+[.][0-9]+$";
    @Autowired
    VersionResourceCaller versionResourceCaller;

    @When("^the Version endpoint is called$")
    public void the_Version_endpoint_is_called() throws Throwable {
            versionResourceCaller.get();

    }

    @Then("^the application's current Version is returned$")
    public void the_application_s_current_Version_is_returned() throws Throwable {

        String version = versionResourceCaller.getLastVersion();
        assertThat(version, is(not(nullValue())));
        assertThat(Pattern.matches(VERSION_REGEX, version), is(true));
    }
}
