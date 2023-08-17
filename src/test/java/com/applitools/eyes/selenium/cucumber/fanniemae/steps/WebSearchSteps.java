package com.applitools.eyes.selenium.cucumber.fanniemae.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import static org.testng.Assert.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.fanniemae.pageapi.FannieMaeSite;
import com.applitools.eyes.selenium.cucumber.fanniemae.pageapi.MainPage;
import com.applitools.eyes.selenium.cucumber.fanniemae.pageapi.SearchResultsPage;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.PageTest;

public class WebSearchSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(WebSearchSteps.class.getName());
    
    private MainPage mainPage;
    private SearchResultsPage searchResultsPage;
    private final EyesManager eyesManager;
    
    public WebSearchSteps(FannieMaeSite site, EyesManager eyesManager) {
        super(site);
        this.mainPage = new MainPage(site);
        this.searchResultsPage = new SearchResultsPage(site);
        this.eyesManager = eyesManager;
    }
    
    @Given("I am on the Fannie Mae main page")
    public void loadMainPage() {
        log.info("Thread ID [{}] loading Fannie Mae main page", getThreadId());
        site.load(mainPage);
    }
    
    @And("I accept all cookies")
    public void acceptAllCookies() {
        log.info("Thread ID [{}] accepting all cookies", getThreadId());
        mainPage.acceptCookies();
    }
    
    @When("I enter the search term {string}")
    public void enterSearchTerm(String searchTerm) {
        log.info("Thread ID [{}] entering search term '{}'", getThreadId(), searchTerm);
        mainPage.clickSearchButton();
        mainPage.enterSearchTerm(searchTerm);
    }
    
    @And("I submit the search")
    public void submitSearch() {
        log.info("Thread ID [{}] submitting search", getThreadId());
        mainPage.submitSearch();
    }
    
    @Then("I should be on the Search Results page")
    public void checkSearchResultsPageIsLoaded() {
        log.info("Thread ID [{}] checking that the Search Results page is loaded", getThreadId());
        searchResultsPage.waitForPage();
        searchResultsPage.waitForVisibleFooter();
        assertEquals(searchResultsPage.getTitle(), SearchResultsPage.TITLE_TEXT, "Incorrect page title!");
    }
    
    @And("eyes should see search results")
    public void enterUsername() {
        log.info("Thread ID [{}] checking the search results page", getThreadId());
        searchResultsPage.waitForSearchResults();
        eyesManager.eyes().check(Target.window().fully().layout(searchResultsPage.resultsSummary(), searchResultsPage.results()));
    }
    
}
