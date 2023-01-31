package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import com.applitools.eyes.selenium.managers.WebDriverManager;
import com.applitools.eyes.selenium.pageapi.Site;

public class HerokuInternetSite extends Site {
    
    public static final String baseUrl = "https://the-internet.herokuapp.com";

    public HerokuInternetSite(WebDriverManager driverManager) {
        super(driverManager, baseUrl);
    }
    
}
