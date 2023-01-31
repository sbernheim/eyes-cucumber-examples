package com.applitools.eyes.selenium.pageapi;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.managers.WebDriverManager;
import com.applitools.eyes.selenium.settings.Settings;

public abstract class Site {
    private static final Logger log = LoggerFactory.getLogger(Site.class.getName());

    public static final Settings settings = Settings.readSettings();

    WebDriverManager driverManager;
    private final URL url;
    
    public Site(WebDriverManager driverManager, String baseUrl) {
        this(driverManager, toURL(baseUrl));
    }
    
    public Site(WebDriverManager driverManager, URL url) {
        this.driverManager = driverManager;
        this.url = url;
    }
    
    /**
     * Returns the WebDriver for this Site.
     * 
     * @return  WebDriver
     */
    public WebDriver driver() {
        return driverManager.driver();
    }
    
    /**
     * Returns a java.net.URL object for the base URL of this Site.
     * 
     * @return java.net.URL
     */
    public URL getURL() {
        return url;
    }
    
    /**
     * Returns the base URL of this Site as a String.
     * 
     * @return String
     */
    public String getBaseUrl() {
        return url.toExternalForm();
    }
    
    /*
     * Deletes all browser cookies.
     */
	public void deleteAllCookies() {
        log.info("deleting all browser cookies");
		driver().manage().deleteAllCookies();
	}

    /*
     * Makes the browser window full-screen.
     */
    public void fullscreen() {
        driver().manage().window().fullscreen();
    }

    /*
     * Maximizes the browser window.
     */
    public void maximize() {
        driver().manage().window().maximize();
    }

    /*
     * Resizes the browser window to the passed Dimension object's dimensions.
     */
    public void resize(Dimension size) {
        driver().manage().window().setSize(size);
    }

    /*
     * Resizes the browser window to the passed width and height.
     */
    public void resize(int width, int height) {
        Dimension size = new Dimension(width, height);
        resize(size);
    }

    /*
     * Converts a String URL to a URL object by wrapping the java.net.URL constructor.
     * 
     * If the passed URL is not valid, this method converts the MalformedURLException to a
     * RuntimeException for convenience in automated tests.
     */
    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("failed to parse URL '%s'", url), e);
        }
    }

    /*
     * Converts the passed relative-to-base URL for this Site to a full URL object within
     * the Site.
     * 
     * For example, if this site's base URL is "https://foo.com, and this method is passed
     * the String "/bar", it would return a java.net.URL object for "https://foo.com/bar".
     */
    public URL pageUrl(String page) {
        return toURL(pageUrlString(page));
    }
    
    /*
     * Converts the passed relative-to-base URL for this Site to a full URL String within
     * the Site.
     * 
     * For example, if this site's base URL is "https://foo.com, and this method is passed
     * the String "/bar", it would return the String "https://foo.com/bar".
     */
    public String pageUrlString(String page) {
        return getBaseUrl() + page;
    }
    
    /*
     * Load the full URL for the passed page object in the current browser window or tab.
     */
    public <P extends Page> P load(P page) {
        String urlStr = pageUrlString(page.getRelativeUrl());
        log.info("Thread ID [{}] Loading Page URL: {}", getThreadId(), urlStr);
        driver().get(urlStr);
        return page;
    }
    
    /*
     * Returns true if the full URL for the passed page object is loaded in the current 
     * browser window or tab.  Otherwise, returns false.
     */
    public <P extends Page> boolean hasLoaded(P page) {
        String urlStr = pageUrlString(page.getRelativeUrl());
        log.info("Thread ID [{}] Checking expected URL '{}'", getThreadId(), urlStr);
        String currentUrl = driver().getCurrentUrl();
        log.info("Thread ID [{}] Currently loaded URL  '{}' match[{}]", getThreadId(), currentUrl, urlStr.equals(currentUrl));
        return urlStr.equals(currentUrl);
    }

    /*
     * Takes a screenshot of the current page and returns it as a File.
     */
    protected File takeScreenshot() throws WebDriverException, IOException {
        TakesScreenshot picDriver = (TakesScreenshot) driver();
        File savedPic = new File(System.getenv("HOME") + File.separator + "Screenshot-" + Instant.now().getEpochSecond() + ".png");
        FileUtils.writeByteArrayToFile(savedPic, picDriver.getScreenshotAs(OutputType.BYTES));
        log.info("Thread ID [{}] Screenshot saved to {}", getThreadId(), savedPic.getAbsolutePath());
        return savedPic;
    }

}