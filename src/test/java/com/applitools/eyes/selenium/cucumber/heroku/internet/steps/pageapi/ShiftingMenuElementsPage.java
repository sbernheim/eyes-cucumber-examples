package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import org.openqa.selenium.By;

import com.applitools.eyes.selenium.pageapi.Page;

public class ShiftingMenuElementsPage extends Page {
    
    private static final String RELATIVE_URL = "/shifting_content/menu";
    
    By menuList = By.tagName("ul");
    
    public ShiftingMenuElementsPage() {
        super(RELATIVE_URL);
    }
    
    public ShiftingMenuElementsPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public int countMenuItems() {
        return countListItems(find(menuList));
    }

}
