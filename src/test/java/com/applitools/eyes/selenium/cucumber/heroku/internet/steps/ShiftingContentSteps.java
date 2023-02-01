package com.applitools.eyes.selenium.cucumber.heroku.internet.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import static org.testng.Assert.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.DropdownPage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HerokuInternetSite;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.ShiftingImagePage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.ShiftingListPage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.ShiftingMenuElementsPage;
import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.PageTest;

public class ShiftingContentSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    private ShiftingListPage listPage;
    private ShiftingImagePage imagePage;
    private ShiftingMenuElementsPage menuPage;
    
    public ShiftingContentSteps(HerokuInternetSite site) {
        super(site);
        this.listPage = new ShiftingListPage(site);
        this.imagePage = new ShiftingImagePage(site);
        this.menuPage = new ShiftingMenuElementsPage(site);
    }
    
    @Given("I am on the Shifting List Content page")
    public void loadShiftingListPage() {
        log.info("Thread ID [{}] loading Shifting List Content page", getThreadId());
        site.load(listPage);
    }
    
    @Given("I am on the Shifting Image page")
    public void loadShiftingImagePage() {
        log.info("Thread ID [{}] loading Shifting Image page", getThreadId());
        site.load(imagePage);
    }
    
    @Given("I am on the Shifting Menu Elements page")
    public void loadShiftingMenuElementPage() {
        log.info("Thread ID [{}] loading Shifting Menu Elements page", getThreadId());
        site.load(menuPage);
    }
    
    @Then("there should be {int} menu items")
    public void countMenuItems(int expectedCount) {
        log.info("Thread ID [{}] checking the Shifting Menu Elements page has {} menu items", getThreadId(), expectedCount);
        assertEquals(menuPage.countMenuItems(), expectedCount, "the Shifting Menu Elements page has the wrong number of menu items!");
    }
    
    @When("I hover over menu item {int}")
    public void hoverOverMenuItem(int index) {
        log.info("Thread ID [{}] hovering mouse over Shifting Menu Elements menu item {}", getThreadId(), index);
        menuPage.hoverOverMenuItem(index);
    }
    
}
