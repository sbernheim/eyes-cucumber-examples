package com.applitools.eyes.selenium.cucumber.fanniemae.pageapi;

import com.applitools.eyes.selenium.managers.WebDriverManager;
import com.applitools.eyes.selenium.pageapi.Site;

public class FannieMaeSite extends Site {
    
    public static final String baseUrl = "https://www.fanniemae.com";

    public FannieMaeSite(WebDriverManager driverManager) {
        super(driverManager, baseUrl);
    }
    
}
