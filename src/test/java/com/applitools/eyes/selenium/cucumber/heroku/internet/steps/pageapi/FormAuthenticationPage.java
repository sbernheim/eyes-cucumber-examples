package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import org.openqa.selenium.By;

public class FormAuthenticationPage extends BasePage {
    
    private static final String RELATIVE_URL = "/login";
    public static final String TITLE_TEXT = "Login Page";
    
    By title = By.cssSelector("#content h2");
    By usernameField = By.id("username");
    By passwordField = By.id("password");
    By loginButton = By.cssSelector("#login button");
    
    public FormAuthenticationPage() {
        super(RELATIVE_URL);
    }
    
    public FormAuthenticationPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public String getTitle() {
        return find(title).getText();
    }
    
    public FormAuthenticationPage setUsername(String username) {
        find(usernameField).sendKeys(username);
        return this;
    }

    public FormAuthenticationPage setPassword(String password) {
        find(passwordField).sendKeys(password);
        return this;
    }

    public FormAuthenticationPage clickLoginButton() {
        find(loginButton).click();
        return this;
    }
    
    public boolean loginSuccessful() {
        return hasSuccessAlert();
    }
}
