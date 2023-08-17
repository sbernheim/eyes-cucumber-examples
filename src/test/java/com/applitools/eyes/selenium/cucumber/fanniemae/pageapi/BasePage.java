package com.applitools.eyes.selenium.cucumber.fanniemae.pageapi;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.applitools.eyes.selenium.pageapi.Page;

public class BasePage extends Page {
    
    By footerSelector = By.cssSelector("div#footer-new");
    
    protected FannieMaeSite site;
    
    public BasePage(String relativeUrl) {
        super(relativeUrl);
    }
    
    public BasePage(String relativeUrl, FannieMaeSite site) {
        super(relativeUrl, site);
        this.site = site;
    }

    public void waitForVisibleFooter() {
        WebDriverWait wait = waiter(90);
        wait.until(ExpectedConditions.visibilityOfElementLocated(footerSelector));
    }
    
}
