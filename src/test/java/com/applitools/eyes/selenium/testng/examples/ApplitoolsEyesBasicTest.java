package com.applitools.eyes.selenium.testng.examples;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.google.common.base.Strings;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class ApplitoolsEyesBasicTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    // Test control inputs to read once and share for all tests
    private static String applitoolsApiKey;
    private static String eyesServerUrl;
    private static boolean headless;
    private static boolean eyesIsDisabled;
    private static boolean forceDiffs;
    private static boolean logTestResults;

    // Applitools Eyes Context objects - shared for all tests
    private static BatchInfo batch;
    private static Configuration config;
    private static EyesRunner runner;
    
    // Eyes Batch meta-data values
    private static final String batchName = "Eyes Demo: Fannie Mae";

    // Test-specific objects
    private WebDriver driver;
    private Eyes eyes;
    
    // Eyes Test meta-data values
    private static final String appName = "Fannie Mae Web";
    private String testName = "Fannie Mae Web Search";
    private int browserHeight = 768;
    private int browserWidth = 1024;

    @Test
    public void basicWebSiteTest() {
        log.info("Start basic UI test");

        // This test covers login for the Applitools demo site, which is a dummy banking app.
        // The interactions use typical Selenium WebDriver calls,
        // but the verifications use one-line snapshot calls with Applitools Eyes.
        // If the page ever changes, then Applitools will detect the changes and highlight them in the dashboard.
        // Traditional assertions that scrape the page for text values are not needed here.

        // Read the Applitools API key from an environment variable.
        // To find your Applitools API key:
        // https://applitools.com/tutorials/getting-started/setting-up-your-environment.html
        //
        // NOTE: this is not strictly required when using the eyes-selenium-java5 SDK.
        applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");
        
        //eyesServerUrl = System.getenv("APPLITOOLS_SERVER_URL");

        // Read the headless mode setting from an environment variable.
        // Use headless mode for Continuous Integration (CI) execution.
        // Use headed mode for local development.
        headless = Boolean.parseBoolean(System.getenv().getOrDefault("WEBDRIVER_HEADLESS", "true"));
        
        //eyesIsDisabled = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_IS_DISABLED", "false"));

        // Switch to the V2 URL to force some diffs (set FORCE_DIFFERENCES env var to "true")
        forceDiffs = Boolean.parseBoolean(System.getenv().getOrDefault("FORCE_DIFFERENCES", "false"));

        // Create the runner 
        runner = new ClassicRunner();

        // Create a new batch for tests.
        // A batch is the collection of visual checkpoints for a test suite.
        // Batches are displayed in the dashboard, so use meaningful names.
        batch = new BatchInfo(batchName);
        
        // Add Property key/value pairs to group and filter batch results in the Dashboard UI.
        batch.addProperty("Environment", "LOCAL");
        batch.addProperty("Language", "Java");
        batch.addProperty("SDK", "Selenium Java5");
        batch.addProperty("Framework", "TestNG");
        batch.addProperty("Scope", "Basic");
        batch.addProperty("Hooks", "false");
        batch.addProperty("Runner", "Classic");

        // Create a configuration for Applitools Eyes.
        //System.out.printf("Before: Class for %s - APPLITOOLS creating config\n", this.getClass().getSimpleName());
        config = new Configuration();

        if (!Strings.isNullOrEmpty(eyesServerUrl)) {
            log.warn("\n\n\t--------> APPLITOOLS_SERVER_URL '{}' <-------- {}\n", eyesServerUrl);
            config.setServerUrl(eyesServerUrl);
        }

        // Set the Applitools API key so test results are uploaded to your account.
        // If you don't explicitly set the API key with this call,
        // then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
        config.setApiKey(applitoolsApiKey);

        // The BatchInfo is a component of the Configuration
        config.setBatch(batch);
        //config.setDisableBrowserFetching(true);

        // Checks whether we will run this test on a local browser or on the Execution Cloud
        boolean ecx = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_EXECUTION_CLOUD", "false"));

        // Open the browser with a WebDriver instance - either ChromeDriver for local or RemoteWebDriver
        // Even if this test will render checkpoints for different setups in the Ultrafast Grid,
        // it still needs to run the test one time in a browser to capture snapshots.
        //if (ecx) {
            // Open the browser remotely in the Execution Cloud.
            /*DesiredCapabilities caps = new DesiredCapabilities();
            caps.setBrowserName("chrome");
            log.info("Eyes tests will execute on the Applitools Self-Healing Execution Cloud! [{}]", Eyes.getExecutionCloudURL());
            try {
                this.driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), caps);
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to the Applitools Self-Healing Execution Cloud at " + Eyes.getExecutionCloudURL(), e);
            }*/
        //}
        //else {
            // Open the browser with a local ChromeDriver instance.
            ChromeOptions opts = new ChromeOptions();
            if (headless) opts.addArguments("--headless=new");
            this.driver = new ChromeDriver(opts);
            log.info("Eyes tests will execute on your local Chrome browser...");
        //}
        
        // Set the browser window size - height, width
        driver.manage().window().setSize(new Dimension(browserHeight, browserHeight));

        // Set an implicit wait of 10 seconds.
        // For larger projects, use explicit waits for better control.
        // https://www.selenium.dev/documentation/webdriver/waits/
        // The following call works for Selenium 4:
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // If you are using Selenium 3, use the following call instead:
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Create the Applitools Eyes object connected to the Runner and set its configuration.
        eyes = new Eyes(runner);

        // The Configuration includes all the default settings for this Eyes test
        eyes.setConfiguration(config);
        
        // Add property key/value pairs to filter and group test results in the Dashboard UI.
        eyes.addProperty("Function", "Login");
        eyes.addProperty("Letter", "A");
        eyes.addProperty("Number", "1");
        eyes.addProperty("Boolean", "true");
        
        // Disable all Eyes API calls if the environment variable was set
        // An environment variable that disables all the Eyes API calls.
        if (eyesIsDisabled) {
            log.warn("\n\n\t--------> ALL APPLITOOLS EYES API CALLS ARE DISABLED!!! <-------- {}\n", eyesIsDisabled);
        }
        eyes.setIsDisabled(eyesIsDisabled);
        
        // Open Eyes to start visual testing.
        // It is a recommended practice to set all four inputs:
        eyes.open(
                driver,   // WebDriver object to "watch"
                appName,  // The name of the app under test
                testName, // The name of the test case
                          // The viewport size for the local browser - width , height
                new RectangleSize(browserWidth, browserHeight));

        try {
            ApplitoolsWebSiteTest.runSingleTest(driver, eyes, forceDiffs);
        } catch (Exception e) {
            log.error("Ending Eyes test due to Exception : {}", e);
            throw e;
        } finally {
            // Close Eyes
            eyes.closeAsync();

            // Quit the WebDriver instance.
            driver.quit();

        }
        
        try {
            // Gets results of all the tests AND implicitly closes the Batch
            TestResultsSummary allTestResults = runner.getAllTestResults(false); // pass true to throw DiffsFoundException when diffs are found!

            logTestResults = Boolean.parseBoolean(System.getenv().getOrDefault("LOG_DETAILED_TEST_RESULTS", "false"));
            if (logTestResults) {
                ApplitoolsWebSiteTest.logTestResults(allTestResults);
            } else {
                log.info("RESULTS: {}", allTestResults);
            }
        } catch (DiffsFoundException ex) {
            log.error("Applitools Eyes tests found differences! {}", ex);
        }

        log.info("End basic example test");
            
    }

}
