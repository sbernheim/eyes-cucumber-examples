package com.applitools.eyes.selenium.pageapi;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Page {
    private static final Logger log = LoggerFactory.getLogger(Page.class.getName());
    
    private WebDriver driver;
    private URL url;
    private String relativeUrl;

    public Page(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }
    
    public Page(String relativeUrl, Site site) {
        this(relativeUrl);
        this.driver = site.driver();
        this.url = site.pageUrl(relativeUrl);
    }
    
    public URL getURL() {
        return url;
    }
    
    public String getRelativeUrl() {
        return relativeUrl;
    };

    public Page clickLink(String linkText) {
        WebElement link = driver.findElement(By.linkText(linkText));
        link.click();
        return this;
    }
    
    public WebElement find(By locator) {
        return driver.findElement(locator);
    }
    
    public boolean has(By locator) {
        return driver.findElements(locator).size() > 0;
    }
    
    public List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }
    
    public Optional<WebElement> findOptional(By locator) {
        List<WebElement> found = driver.findElements(locator);
        if (found.size() > 0) {
            return Optional.of(found.get(0));
        }
        return Optional.empty();
    }
    
    public WebDriverWait waiter() {
        return waiter(30);
    }

    public WebDriverWait waiter(long timeoutInSeconds) {
        return new WebDriverWait(driver, timeoutInSeconds);
    }
    
    public Select toSelect(WebElement e) {
        return e == null ? null : new Select(e);
    }
    
    public Optional<Select> findSelect(By locator) {
        Optional<WebElement> element = findOptional(locator);
        return element.map(this::toSelect);
    }
    
    public Page click(By locator) {
        find(locator).click();
        return this;
    }
    
    public Actions actions() {
        return new Actions(driver);
    }
    
    public WebElement moveTo(WebElement element) {
        actions().moveToElement(element).perform();;
        return element;
    }
    
    public WebElement moveTo(By locator) {
        return moveTo(find(locator));
    }
    
    public WebElement moveTo(By locator, int index) {
        return moveTo(findAll(locator).get(index));
    }
    
    public Page logTitle() {
        log.info("Page Title: '{}' for {}", driver.getTitle(), url);
        return this;
    }
    
    public int countLinks() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        log.info("Found " + links.size() + " links!");
        return links.size();
    }
    
    public WebElement findUnorderedList() {
        return driver.findElement(By.tagName("ul"));
    }
    
    public int countListItems() {
        return countListItems(findUnorderedList());
    }
    
    public int countListItems(WebElement listElement) {
        List<WebElement> listItems = listElement.findElements(By.tagName("li"));
        log.info("Found " + listItems.size() + " list items!");
        return listItems.size();
    }

}