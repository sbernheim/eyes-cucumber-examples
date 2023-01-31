package com.applitools.eyes.selenium.cucumber.examples;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Steps {
    private static final Logger log = LoggerFactory.getLogger(Steps.class.getName());
    
    @BeforeStep
    public void beforeEachStep() {
        log.info("Thread ID [{}] Running before step method from {}", getThreadId(), Steps.class.getName());
    }
    
    @Given("Step from {string} in {string} feature file")
    public void exampleStep(String scenario, String file) {
        log.info("Thread ID [{}] scenario '{}' from feature file '{}'", getThreadId(), scenario, file);
    }
    
    @Given("an example precondition")
    public void examplePrecondition() {
        log.info("Thread ID [{}] example precondition", getThreadId());
    }
    
    @Given("a different precondition")
    public void differentExamplePrecondition() {
        log.info("Thread ID [{}] a different precondition", getThreadId());
    }
    
}
