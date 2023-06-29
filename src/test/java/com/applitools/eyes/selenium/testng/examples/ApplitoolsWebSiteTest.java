package com.applitools.eyes.selenium.testng.examples;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;

public class ApplitoolsWebSiteTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    private static final String pageURL = "https://www.capitalone.com/";
    
    public static void runTest(WebDriver driver, Eyes eyes, boolean forceDiffs) {
        // Load the login page.
        driver.get(pageURL);
        
        // Getting the text of the footer is a convenient way to make sure the full page loaded.
        String footerText = driver.findElement(By.cssSelector("div.site-footer")).getText();
        log.trace("Found Footer: {}", footerText);
            
        if (forceDiffs) {
            // TODO: Inject some JavaScript to create arbitrary diffs on the page!
        }
        
        // Verify the full login page loaded correctly.
        if (eyes != null) eyes.check(Target.window().fully().withName("Main Page"));
        
        String linkCSSSelector = "div#siteHeaderContainer";
        By linkLocator = By.cssSelector(linkCSSSelector);
        
        SeleniumCheckSettings checkSettings = Target.window().fully();

        // Perform an action.
        driver.findElement(linkLocator).findElement(By.partialLinkText("Credit Cards")).click();

        // Getting the text of the footer is a convenient way to make sure the full page loaded.
        footerText = driver.findElement(By.cssSelector("div.site-footer")).getText();
        log.trace("Found Footer: {}", footerText);
        
        // Find all the data-date elements and add Layout regions to cover them!
        log.info("Finding shared article tiles");
        List<WebElement> tiles = driver.findElements(By.cssSelector("div.grv-row:nth-child(2) div.grv-card"));
        log.info("Found {} tiles", tiles.size());
        for (int x = 1; x <= tiles.size(); x++) {
            String cardSelector = String.format("div.grv-row:nth-child(2) ul li:nth-child(%d) div.grv-card", x);
            log.info("Adding coded layout region for '{}'", cardSelector);
            By cardLocator = By.cssSelector(cardSelector);
            WebElement e = driver.findElement(cardLocator);
            log.info("Found grv-card element {} : {}", x, e);
            if (e != null && e.isEnabled()) checkSettings = checkSettings.layout(cardLocator);
        }

        log.info("Checking 'Credit Cards'");
        if (eyes != null) eyes.check(checkSettings.withName("Credit Cards"));
            
    }

}
