package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

public class ShiftingImagePage extends BasePage {
    
    private static final String RELATIVE_URL = "/shifting_content/image";
    
    public ShiftingImagePage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }

}
