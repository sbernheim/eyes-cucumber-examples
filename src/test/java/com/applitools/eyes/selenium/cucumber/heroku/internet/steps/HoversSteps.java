package com.applitools.eyes.selenium.cucumber.heroku.internet.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HerokuInternetSite;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HoversPage;
import com.applitools.eyes.selenium.pageapi.PageTest;

public class HoversSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(HoversSteps.class.getName());
    
    private HoversPage hoversPage;
    private HoversPage.FigureCaption caption;
    
    public HoversSteps(HerokuInternetSite site) {
        super(site);
        this.hoversPage = new HoversPage(site);
    }
    
    @Given("I am on the Hovers page")
    public void loadHoversPage() {
        log.info("Thread ID [{}] loading Hovers page", getThreadId());
        site.load(hoversPage);
    }
    
    @When("I move my mouse over item {int}")
    public void mouseOverItem(int index) {
        log.info("Thread ID [{}] hovering mouse over item {}", getThreadId(), index);
        caption = hoversPage.hoverOverFigure(index);
    }
    
    @Then("the item {int} caption should be displayed")
    public void checkThatItemCaptionIsDisplayed(int index) {
        log.info("Thread ID [{}] checking that item {} caption is displayed", getThreadId(), index);
        assertTrue(caption.isDisplayed(), "the item " + index + " caption is not displayed!");
    }

    @And("the caption title should be {string}")
    public void checkCaptionTitleText(String expectedText) {
        log.info("Thread ID [{}] checking for caption title text '{}'", getThreadId(), expectedText);
        String titleText = caption.getTitle();
        assertEquals(titleText, expectedText, "the caption title text is not correct!");
    }
    
    
    @And("the caption profile link text should be {string}")
    public void checkCaptionProfileLinkText(String expectedText) {
        log.info("Thread ID [{}] checking for caption profile link text '{}'", getThreadId(), expectedText);
        String profileLinkText = caption.getProfileLinkText();
        assertEquals(profileLinkText, expectedText, "the caption profile link text is not correct!");
    }
    
    @And("the caption profile link URL should be {string}")
    public void checkCaptionProfileLinkUrl(String relativeUrl) {
        log.info("Thread ID [{}] checking for caption profile link URL '{}'", getThreadId(), relativeUrl);
        String expectedUrl = caption.getPageUrl(relativeUrl);
        String profileLinkUrl = caption.getProfileLinkURL();
        assertEquals(profileLinkUrl, expectedUrl, "the caption profile link URL is not correct!");
    }
    
}
