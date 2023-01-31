package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import com.applitools.eyes.selenium.pageapi.Page;

public class ShiftingListPage extends Page {
    
    private static final String RELATIVE_URL = "/shifting_content/list";
    
    public ShiftingListPage() {
        super(RELATIVE_URL);
    }

    public ShiftingListPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }

}
