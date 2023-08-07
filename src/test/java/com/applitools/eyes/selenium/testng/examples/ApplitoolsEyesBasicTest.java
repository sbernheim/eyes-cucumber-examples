package com.applitools.eyes.selenium.testng.examples;

import java.time.Duration;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.util.Strings;

public class ApplitoolsEyesBasicTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    // Test control inputs to read once and share for all tests
    private static String applitoolsApiKey;
    private static String eyesServerUrl;
    private static boolean headless;
    private static boolean eyesIsDisabled;
    private static boolean forceDiffs;

    // Applitools Eyes Context objects - shared for all tests
    private static BatchInfo batch;
    private static Configuration config;
    private static EyesRunner runner;
    
    // Eyes Batch meta-data values
    private static final String batchName = "Repro: Quest Diagnostics Grey Bars";

    // Test-specific objects
    private WebDriver driver;
    private Eyes eyes;
    
    // Eyes Test meta-data values
    private static final String appName = "Lab Report";
    private String testName = "Lab Report Analyte Card";
    //private int browserHeight = 768;
    //private int browserWidth = 1024;
    private int browserHeight = 600;
    private int browserWidth = 1200;

    // Values used by the test
    private String pageURL1 = "https://demo.applitools.com";
    private String pageURL2 = "https://demo.applitools.com/index_v2.html";
    private String pageURL = "http://localhost:8080/Lab%20Result.html";
    private String username = "applibot";
    private String password = "I<3VisualTests";
        
    @Test
    public void loginPageTest() {
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
        //pageURL = forceDiffs ? pageURL2 : pageURL1;

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
        batch.addProperty("Runner", "No");

        // Create a configuration for Applitools Eyes.
        //System.out.printf("Before: Class for %s - APPLITOOLS creating config\n", this.getClass().getSimpleName());
        config = new Configuration();
        config.setStitchMode(StitchMode.SCROLL);

        if (Strings.isNotNullAndNotEmpty(eyesServerUrl)) {
            //log.warn("\n\n\t--------> APPLITOOLS_SERVER_URL '{}' <-------- {}\n", eyesServerUrl);
            //config.setServerUrl(eyesServerUrl);
        }

        // Set the Applitools API key so test results are uploaded to your account.
        // If you don't explicitly set the API key with this call,
        // then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
        config.setApiKey(applitoolsApiKey);

        // The BatchInfo is a component of the Configuration
        config.setBatch(batch);
        //config.setDisableBrowserFetching(true);

        // Open the browser with the ChromeDriver instance.
        // Even though this test will run visual checkpoints on different browsers in the Ultrafast Grid,
        // it still needs to run the test one time locally to capture snapshots.
        ChromeOptions opts = new ChromeOptions();
        if (headless) opts.addArguments("--headless=new");
        driver = new ChromeDriver(opts);
        
        // Set the browser window size - height, width
        driver.manage().window().setSize(new Dimension(browserHeight, browserHeight));

        // Set an implicit wait of 10 seconds.
        // For larger projects, use explicit waits for better control.
        // https://www.selenium.dev/documentation/webdriver/waits/
        // The following call works for Selenium 4:
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // If you are using Selenium 3, use the following call instead:
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

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

        // Load the login page.
        driver.get(pageURL);

        // Verify the full login page loaded correctly.
        eyes.check(Target.window().fully().withName("Main Lab Report"));

        // Perform login.
        /*driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("log-in")).click();*/

        // Verify the full main page loaded correctly.
        WebElement scrollable = driver.findElement(By.cssSelector("#cit-column-report-detail-profile-0"));

        JavascriptExecutor jsexec = (JavascriptExecutor) driver;
        jsexec.executeScript("document.querySelector(\"#cit-scroll-to-top-control\").style.visibility = \"hidden\"");

        eyes.check(Target.region(By.cssSelector("#cit-column-report-detail-profile-0")).fully().stitchMode(StitchMode.CSS).withName("Analyte Cards CSS"));

        eyes.check(Target.region(By.cssSelector("#cit-column-report-detail-profile-0")).fully().stitchMode(StitchMode.SCROLL).withName("Analyte Cards Scroll"));
        
        eyes.check(Target.region(By.cssSelector("#singleanalyte-HEALTHQUOTIENTHQSUCCESS > div.qd-results-panel__content.col-md-12")).scrollRootElement(By.cssSelector("#cit-column-report-detail-profile-0")).fully().withName("Health Quotient Score Card"));

        // Close Eyes
        eyes.closeAsync();

        // Quit the WebDriver instance.
        driver.quit();

        // Gets results of all the tests AND implicitly closes the Batch
        TestResultsSummary allTestResults = runner.getAllTestResults();

        log.info("End basic example test");
        log.info("RESULTS: {}", allTestResults);
    }

}
