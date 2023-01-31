package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import org.openqa.selenium.By;

public class ShiftingContentPage extends BasePage {
    
    private static final String RELATIVE_URL = "/shifting_content";
    
    By menuElementLink = By.linkText("Example 1: Menu Element");
    By anImageLink = By.linkText("Example 2: An image");
    By listLink = By.linkText("Example 3: List");
    
    public ShiftingContentPage() {
        super(RELATIVE_URL);
    }
    
    public ShiftingContentPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public void clickMenuElementLink() {
        click(menuElementLink);
    }

    public void clickAnImageLink() {
        click(anImageLink);
    }

    public void clickListLink() {
        click(listLink);
    }

}
