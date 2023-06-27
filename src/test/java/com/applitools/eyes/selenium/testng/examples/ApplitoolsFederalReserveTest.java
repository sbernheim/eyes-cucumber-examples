package com.applitools.eyes.selenium.testng.examples;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;

public class ApplitoolsFederalReserveTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    private static final String pageURL = "https://www.philadelphiafed.org/";
    
    public static void runTest(WebDriver driver, Eyes eyes, boolean forceDiffs) {
        // Load the login page.
        driver.get(pageURL);
        
        if (forceDiffs) {
            // TODO: Inject some JavaScript to create arbitrary diffs on the page!
        }
        
        // Verify the full login page loaded correctly.
        if (eyes != null) eyes.check(Target.window().fully().withName("Main Page"));

        // Perform an action.
        String topMenuCssSelector = "ul.nav-topic";
        By topMenuLocator = By.cssSelector(topMenuCssSelector);
        String menuItemsCssSelector = topMenuCssSelector + " > li > a:first-child";
        By menuItemsLocator = By.cssSelector(menuItemsCssSelector);
        String activeMenuItemCssSelector = topMenuCssSelector + " > li.active";
        String activeMenuItemLinksCssSelector = activeMenuItemCssSelector + " > a:first-child";
        By activeMenuItemLinksLocator = By.cssSelector(activeMenuItemLinksCssSelector);
        String activeSubMenuCssSelector = activeMenuItemCssSelector + "  > ul.nav-sub-topic";
        By subMenuLocator = By.cssSelector(activeSubMenuCssSelector);
        String subMenuItemLinksCssSelector = activeSubMenuCssSelector + " > li > a:first-child";
        By subMenuItemsLocator = By.cssSelector(subMenuItemLinksCssSelector);
        String activeSubMenuItemCssSelector = activeSubMenuCssSelector + " > li.active";
        By activeSubMenuItemLocator = By.cssSelector(activeSubMenuItemCssSelector);

        List<WebElement> menuItems = driver.findElements(menuItemsLocator);
        List<String> menuItemNames = menuItems.stream().map(i -> i.getText().trim()).toList();
        log.info("Found Menu Items: {}", menuItemNames);

        for (String menuItemName : menuItemNames) {
            log.info("Clicking Menu Item: {}", menuItemName);
            driver.findElement(topMenuLocator).findElement(By.partialLinkText(menuItemName)).click();

            // Getting the text of the footer is a convenient way to make sure the full page loaded.
            String footerText = driver.findElement(By.cssSelector("div.footer")).getText();
            log.trace("Found Footer: {}", footerText);
            
            // Check that selected menu item is now the active item
            String activeMenuItemName = driver.findElement(activeMenuItemLinksLocator).getText().trim();
            log.info("Found Active Menu Item: {}", activeMenuItemName);
            Assert.assertEquals(menuItemName, activeMenuItemName, "expected Menu Item is not marked as active!");
            
            SeleniumCheckSettings menuItemCheckSettings = (SeleniumCheckSettings) Target.window().fully().withName(menuItemName);

            // Find all the data-date elements and add Layout regions to cover them!
            log.info("Finding data-dates for Menu Item {}", menuItemName);
            List<WebElement> dataDates = driver.findElements(By.cssSelector("p.data-date"));
            log.info("Found {} data-dates for Menu Item {} : {}", dataDates.size(), menuItemName, dataDates);
            for (WebElement e : dataDates ) {
                menuItemCheckSettings = menuItemCheckSettings.layout(e);
            }

            log.info("Checking Menu Item Page: {}", menuItemName);
            if (eyes != null) eyes.check(menuItemCheckSettings);
            
            log.info("Finding Sub-Menu Items for {} Menu Item", menuItemName);
            List<WebElement> subMenuItems = driver.findElements(subMenuItemsLocator);
            List<String> subMenuItemNames = subMenuItems.stream().map(i -> i.getText().trim()).toList();
            log.info("Found {} Sub-Menu Items: {}", menuItemName, subMenuItemNames);
            
            for (String subMenuItemName : subMenuItemNames) {
                log.info("Clicking {} Sub-Menu Item: {}", menuItemName, subMenuItemName);
                driver.findElement(subMenuLocator).findElement(By.partialLinkText(subMenuItemName)).click();

                // Getting the text of the footer is a convenient way to make sure the full page loaded.
                footerText = driver.findElement(By.cssSelector("div.footer")).getText();
                log.trace("Found Footer: {}", footerText);
                
                // Check that selected sub-menu item is now the active item
                String activeSubMenuItemName = driver.findElement(activeSubMenuItemLocator).getText().trim();
                log.info("Found {} Active Sub-Menu Item: {}", menuItemName, activeSubMenuItemName);
                Assert.assertEquals(subMenuItemName, activeSubMenuItemName, "expected Sub-Menu Item is not marked as active!");

                String subMenuItemStepName = String.format("%s - %s", menuItemName, subMenuItemName);
                SeleniumCheckSettings subMenuItemCheckSettings = 
                        (SeleniumCheckSettings) Target.window().fully().withName(subMenuItemStepName);

                // Find all the data-date elements and add Layout regions to cover them!
                log.info("Finding data-dates for {} Sub-Menu Item {}", menuItemName, subMenuItemName);
                dataDates = driver.findElements(By.cssSelector("p.data-date"));
                log.info("Found {} data-dates for {} Sub-Menu Item {} : {}", dataDates.size(), menuItemName, subMenuItemName, dataDates);
                for (WebElement e : dataDates ) { 
                    subMenuItemCheckSettings = subMenuItemCheckSettings.layout(e); 
                }

                log.info("Checking {} Sub-Menu Item Page: {}", menuItemName, subMenuItemName);
                if (eyes != null) eyes.check(subMenuItemCheckSettings);
            }
        }
    }

}
