package com.applitools.eyes.selenium.managers;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
//import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.applitools.eyes.selenium.settings.Settings;

public class WebDriverManager {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    final Settings settings = Settings.readSettings();

    private WebDriver driver;

    /**
     * Returns the WebDriver for this Site.
     * 
     * @return  WebDriver
     */
    public WebDriver driver() {
        if (driver == null) {
            startBrowser();
        }
        return driver;
    }

    /*
     * Starts the web driver session and an initial browser window.
     * 
     * Run this before starting your tests.
     */
    public void startBrowser() {
        log.info("Thread ID [{}] creating web browser driver", getThreadId());

        if (settings.runOnApplitoolsExecutionCloud) {
            // Open the browser remotely in the Execution Cloud.
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setBrowserName("chrome");
            RemoteWebDriver remoteDriver = new RemoteWebDriver(applitoolsExecutionCloudURL(), caps);
            log.info("Eyes tests will execute on the Applitools Self-Healing Execution Cloud! [{}]", applitoolsExecutionCloudURL());
            this.driver = remoteDriver;
        }
        else {
            // Open the browser with a local ChromeDriver instance.
            ChromeOptions opts = new ChromeOptions();
            if (settings.headless) opts.addArguments("--headless=new");
            this.driver = new ChromeDriver(opts);
            log.info("Eyes tests will execute on your local Chrome browser...");
        }

        /* For larger projects, use explicit waits for better control.
         * https://www.selenium.dev/documentation/webdriver/waits/
         * 
         * The following call works for Selenium 4:
         */
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(settings.implicitWaitSeconds));

        /* If you are using Selenium 3, use the following call instead:
         */
        driver.manage().timeouts().implicitlyWait(settings.implicitWaitSeconds, TimeUnit.SECONDS);
    }
    
    // Return the Applitools Self-Healing Execution Cloud URL or throw a RuntimeException
    private URL applitoolsExecutionCloudURL() {
        try {
            //return new URL(Eyes.getExecutionCloudURL());
            return new URL("https://www.fanniemae.com");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to parse Applitools Execution Cloud URL!", e);
        }
    }
    
    /*
     * Opens a new browser window in the web driver session.
     */
    public void openWindow() {
        //driver.switchTo().newWindow(WindowType.WINDOW);
    }

    /*
     * Opens a new tab in the current browser window.
     */
    public void openTab() {
        //driver.switchTo().newWindow(WindowType.TAB);
    }

    /*
     * Closes the open window or tab, but does not end the session or quit the browser if
     * there are any other windows or tabs open.
     * 
     * Selenium treats the current browser window and tab as a single thing.  If the
     * current tab is the only one open in the browser window, this method closes the
     * browser window.  If there are more tabs open in the window, it only closes the
     * current tab.
     * 
     * This method quit the browser if there are no other windows or tabs open.
     */
    public void closeWindow() {
        log.info("Thread ID [{}] closing web browser window", getThreadId());
        driver.close();
    }
    
    /*
     * Closes all open windows and tabs, quits the browser, and ends the session.
     * 
     * Run this after finishing your tests.
     */
    public void quitBrowser() {
        log.info("Thread ID [{}] quitting web browser", getThreadId());
        driver.quit();
    }
    
    
    /*
     * Deletes all browser cookies.
     */
    public void deleteAllCookies() {
        log.info("Thread ID [{}] deleting all browser cookies", getThreadId());
        driver.manage().deleteAllCookies();
    }
    
    public Window window() {
        return driver.manage().window();
    }

    /*
     * Makes the browser window full-screen.
     */
    public void fullscreen() {
        window().fullscreen();
    }

    /*
     * Maximizes the browser window.
     */
    public void maximize() {
        window().maximize();
    }

    /*
     * Resizes the browser window to the passed Dimension object's dimensions.
     */
    public void resize(Dimension size) {
        window().setSize(size);
    }

    /*
     * Resizes the browser window to the passed width and height.
     */
    public void resize(int width, int height) {
        Dimension size = new Dimension(width, height);
        resize(size);
    }


}
