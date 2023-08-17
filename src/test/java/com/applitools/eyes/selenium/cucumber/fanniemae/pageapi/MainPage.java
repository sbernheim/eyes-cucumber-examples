package com.applitools.eyes.selenium.cucumber.fanniemae.pageapi;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainPage extends BasePage {
    
    private static final String RELATIVE_URL = "/";
    public static final String TITLE_TEXT = "Main page";
    
    By cookieButton = By.cssSelector("button#onetrust-accept-btn-handler");
    By searchButton = By.cssSelector("button.fm-toggle-search");
    By searchField = By.cssSelector("#searchbox > div > div > div.magic-box-input > input");
    
    public MainPage() {
        super(RELATIVE_URL);
    }
    
    public MainPage(FannieMaeSite site) {
        super(RELATIVE_URL, site);
    }
    
    public MainPage acceptCookies() {
        WebDriverWait acceptCookieWait = waiter();
        try {
            acceptCookieWait.until(ExpectedConditions.elementToBeClickable(cookieButton));
            find(cookieButton).click();
        } catch (TimeoutException|ElementClickInterceptedException e ) {
            // Do nothing
        }
 
        return this;
    }
    
    public MainPage submitSearch() {
        find(searchField).sendKeys(Keys.ENTER);
        return this;
    }

    public MainPage enterSearchTerm(String searchTerm) {
        find(searchField).sendKeys(searchTerm);
        return this;
    }

    public MainPage clickSearchButton() {
        find(searchButton).click();
        return this;
    }
}
