package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.applitools.eyes.selenium.pageapi.Page;

public class BasePage extends Page {
    
    By statusAlert = By.cssSelector("#flash-messages #flash");
    By successAlert = By.cssSelector("#flash-messages #flash.success");
    By errorAlert = By.cssSelector("#flash-messages #flash.error");
    
    protected HerokuInternetSite site;
    
    public BasePage(String relativeUrl) {
        super(relativeUrl);
    }
    
    public BasePage(String relativeUrl, HerokuInternetSite site) {
        super(relativeUrl, site);
        this.site = site;
    }
    
    public boolean hasAlert() {
        Optional<WebElement> alert = findOptional(statusAlert);
        return alert.isPresent() && alert.get().isDisplayed();
    }
    
    public String alertText() {
        Optional<WebElement> alert = findOptional(statusAlert);
        return alert.filter(WebElement::isDisplayed)    // Check that the alert element is displayed
                .map(WebElement::getText)               // Get the element text
                .map(s -> s.lines().findFirst().get())  // Get the first line of String text
                .get();                                 // the map function returns an Optional, so get the String value
    }
    
    public Optional<WebElement> successAlert() {
        return findOptional(successAlert);
    }
    
    public boolean hasSuccessAlert() {
        Optional<WebElement> alert = successAlert();
        return alert.isPresent() && alert.get().isDisplayed();
    }
    
    public Optional<WebElement> errorAlert() {
        return findOptional(errorAlert);
    }
    
    public boolean hasErrorAlert() {
        Optional<WebElement> alert = errorAlert();
        return alert.isPresent() && alert.get().isDisplayed();
    }
}
