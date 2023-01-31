package com.applitools.eyes.selenium.cucumber.hooks;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class ExampleHooks {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    /*
     * A Hook method to open the WebDriver before each Scenario.
     * 
     * The order attribute value must be lower than the Before hook that opens Eyes to
     * ensure that we always start the Selenium WebDriver before opening Eyes.
     * 
     * The value attribute is a tag expression.  This Hook will only run before a
     * Scenario with this tag, so you can have Scenarios in the same suite that do not
     * need to use a Selenium WebDriver.
     * 
     */
    @Before(order=90,value="@examples and @all")
    public void beforeAllWebScenarios(Scenario scenario) {
        log.info("Thread ID [{}] running before method {} for scenario {}", getThreadId(), thisMethod(), scenario.getName());
    }
    
    @Before(order=90,value="@examples and @some")
    public void beforeSomeWebScenarios(Scenario scenario) {
        log.info("Thread ID [{}] running before method {} for scenario {}", getThreadId(), thisMethod(), scenario.getName());
    }
    
    /*
     * A Hook method to close the WebDriver after each Scenario.
     * 
     * The order attribute value must be lower than the After that closes Eyes to
     * ensure that we always close the Eyes before the browser.
     * 
     * The value attribute is a tag expression.  This Hook will only run after a
     * Scenario with this tag, so you can have Scenarios in the same suite that do not
     * need to use a Selenium WebDriver.
     * 
     */
    @After(order=90,value="@examples and @all")
    public void afterAllWebScenarios(Scenario scenario) {
        log.info("Thread ID [{}] running after method {} for scenario {}", getThreadId(), thisMethod(), scenario.getName());
    }

    @After(order=90,value="@examples and @some")
    public void afterSomeWebScenarios(Scenario scenario) {
        log.info("Thread ID [{}] running after method {} for scenario {}", getThreadId(), thisMethod(), scenario.getName());
    }

}
