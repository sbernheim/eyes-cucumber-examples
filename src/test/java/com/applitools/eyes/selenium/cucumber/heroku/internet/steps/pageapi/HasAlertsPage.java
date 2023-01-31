package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

public class HasAlertsPage extends BasePage {
    
    private static final String RELATIVE_URL = "/";
    
    public HasAlertsPage() {
        super(RELATIVE_URL);
    }
    
    public HasAlertsPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
}
