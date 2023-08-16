package com.applitools.eyes.selenium.cucumber.heroku.internet.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import static org.testng.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HasAlertsPage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HerokuInternetSite;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.PageTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class CommonHerokuSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    protected final HerokuInternetSite site;
    private final EyesManager eyesManager;
    private final HasAlertsPage page;

    public CommonHerokuSteps(HerokuInternetSite site, HasAlertsPage page, EyesManager eyesManager) {
        super(site);
        this.site = site;
        this.eyesManager = eyesManager;
        this.page = page;
    }
    
    @Then("I should see a success alert")
    public void checkSuccessAlert() {
        assertTrue(page.hasSuccessAlert(), "The page did not display a success alert!");
    }

    @And("the success alert should say {string}")
    public void checkSuccessAlertText(String expectedAlertText) {
        String alertText = page.alertText();
        assertTrue(alertText.startsWith(expectedAlertText), "Success alert text is incorrect! ''" + alertText + "'");
        log.info("Thread [{}] found success alert message: {}", getThreadId(), alertText);
    }

    @Then("I should see an error alert")
    public void checkErrorAlert() {
        assertTrue(page.hasErrorAlert(), "The page did not display an error alert!");
    }
    
    @And("the error alert should say {string}")
    public void error_alert_text(String expectedAlertText) {
        String alertText = page.alertText();
        assertTrue(alertText.startsWith(expectedAlertText), "Alert text is incorrect! ''" + alertText+ "'");
        log.info("Thread [{}] found error alert message: {}", getThreadId(), alertText);
    }

    @Then("check the {string} step with eyes")
    public void checkWithEyes(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }

    @Then("check the {string} page for all layout breakpoints")
    public void checkWithEyesLayoutBreakpoints(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().layoutBreakpoints(true).withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }

}
