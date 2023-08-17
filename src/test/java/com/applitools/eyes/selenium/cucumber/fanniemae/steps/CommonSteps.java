package com.applitools.eyes.selenium.cucumber.fanniemae.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.fanniemae.pageapi.FannieMaeSite;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.PageTest;

import io.cucumber.java.en.Then;

public class CommonSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    private final EyesManager eyesManager;

    public CommonSteps(FannieMaeSite site, EyesManager eyesManager) {
        super(site);
        this.eyesManager = eyesManager;
    }
    
    @Then("check the {string} page with eyes")
    public void checkWithEyes(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }

    @Then("check the full {string} page with eyes")
    public void checkWithEyesFully(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().fully().withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }

    @Then("check the {string} page for all layout breakpoints")
    public void checkWithEyesLayoutBreakpoints(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().layoutBreakpoints(true).withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }

}
