package com.applitools.eyes.selenium.cucumber.hooks;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.Site;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class EyesHooks {
    private static final Logger log = LoggerFactory.getLogger(EyesHooks.class.getName());
    
    public EyesManager observer;
    
    public EyesHooks(EyesManager observer, Site site) {
        this.observer = observer;
    }
    
    /*
     * A Hook method to open an Eyes instance before each Scenario.
     * 
     * The order attribute value must be higher than the Before hook that opens the 
     * Selenium WebDriver to ensure it always runs before this.
     * 
     * The value attribute is a tag expression.  This Hook will only run before a
     * Scenario with this tag, so you can have Scenarios in the same suite that do not
     * upload checkpoint images to Applitools Eyes.
     * 
     */
    @Before(order=100,value="@eyes")
    public void beforeEyesScenarios(Scenario scenario) {
        log.info("Thread ID [{}] opening Eyes for scenario {}", getThreadId(), scenario.getName());
        // Set the testName OR pass it to openEyesBatch
        observer.openEyes(scenario.getName());
    }
    
    /*
     * A Hook method to close Eyes after each Scenario.
     * 
     * The order attribute value must be higher than the After hook that closes the 
     * Selenium WebDriver to ensure it always runs after this.
     * 
     * The value attribute is a tag expression.  This Hook will only run after a
     * Scenario with this tag, so you can have Scenarios in the same suite that do not
     * upload checkpoint images to Applitools Eyes.
     * 
     */
    @After(order=100,value="@eyes")
    public void afterEyesScenario(Scenario scenario) {
        log.info("Thread ID [{}] closing Eyes for scenario {}", getThreadId(), scenario.getName());
        observer.closeEyes();
    }
    
}
