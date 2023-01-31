package com.applitools.eyes.selenium.cucumber.hooks;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.managers.WebDriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class WebHooks {
    private static final Logger log = LoggerFactory.getLogger(WebHooks.class.getName());
    
    private final WebDriverManager driverManager;;
    
    public WebHooks(WebDriverManager driverManager) {
       this.driverManager = driverManager; 
    }

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
    @Before(order=90,value="@web")
    public void beforeWebScenarios() {
        log.info("Thread ID [{}] Running before method from {}", getThreadId(), WebHooks.class.getName());
        driverManager.startBrowser();
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
    @After(order=90,value="@web")
    public void afterWebScenario() {
        log.info("Thread ID [{}] Running after method from {}", getThreadId(), WebHooks.class.getName());
        driverManager.quitBrowser();
    }

}
