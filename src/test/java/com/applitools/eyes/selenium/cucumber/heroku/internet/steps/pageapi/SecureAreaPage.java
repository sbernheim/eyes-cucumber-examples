package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import org.openqa.selenium.By;

public class SecureAreaPage extends BasePage {
    
    private static final String RELATIVE_URL = "/secure";
    public static final String SUCCESS_ALERT_TEXT = "You logged into a secure area!";
    public static final String TITLE_TEXT = "Secure Area";

    By title = By.cssSelector("#content h2");
    By logoutButton = By.cssSelector("#content .example .button");
    
    public SecureAreaPage() {
        super(RELATIVE_URL);
    }
    
    public SecureAreaPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public String getTitle() {
        return find(title).getText();
    }
    
    public SecureAreaPage clickLogoutButton() {
        find(logoutButton).click();
        return this;
    }

}
