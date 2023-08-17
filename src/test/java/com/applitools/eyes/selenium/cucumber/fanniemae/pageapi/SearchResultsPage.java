package com.applitools.eyes.selenium.cucumber.fanniemae.pageapi;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResultsPage extends BasePage {
    
    private static final String RELATIVE_URL = "/search";
    public static final String TITLE_TEXT = "Search Results";

    By title = By.cssSelector("h1.coveo-title");
    By resultsSummary = By.cssSelector("div.coveo-summary-section");
    By searchResultsList = By.cssSelector("div.CoveoResultList");
    By searchResults =  By.cssSelector("div.CoveoResult");
    
    public SearchResultsPage() {
        super(RELATIVE_URL);
    }
    
    public SearchResultsPage(FannieMaeSite site) {
        super(RELATIVE_URL, site);
    }
    
    public String getTitle() {
        return find(title).getText();
    }
    
    public By resultsSummary() {
        return resultsSummary;
    }
    
    public By results() {
        return searchResults;
    }
    
    public WebElement resultsList() {
        return find(searchResultsList);
    }
    
    public void waitForPage() {
        WebDriverWait wait = waiter(60);
        wait.until(ExpectedConditions.titleContains("Search"));
    }
    
    public boolean waitForSearchResults() {
        WebDriverWait wait = waiter();
        try {
            wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(searchResultsList, searchResults));
        } catch(TimeoutException e) {
            return false;
        }
        return true;
    }
    
}
