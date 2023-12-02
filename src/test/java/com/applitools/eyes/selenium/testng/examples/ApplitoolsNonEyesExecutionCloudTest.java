package com.applitools.eyes.selenium.testng.examples;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.introspection.Introspect;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.util.Strings;

public class ApplitoolsNonEyesExecutionCloudTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    // Test control inputs to read once and share for all tests
    //private static String applitoolsApiKey;
    private static String eyesServerUrl;
    private static boolean headless;
    private static boolean eyesIsDisabled;
    private static boolean forceDiffs;
    private static boolean logTestResults;
    private static boolean ecx;

    // Applitools Eyes Context objects - shared for all tests
    private static BatchInfo batch;
    private static Configuration config;
    private static EyesRunner runner;
    
    // Test-specific objects
    private WebDriver driver;
    private Eyes eyes;
    
    @BeforeSuite
    public void createBatchContext(ITestContext ctx) {
        log.info("Before: Suite for suite '{}'", ctx.getSuite().getName());
        
        // Checks whether we will run this test on a local browser or on the Execution Cloud
        ecx = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_EXECUTION_CLOUD", "false"));

        // Read the Applitools API key from an environment variable.
        // To find your Applitools API key:
        // https://applitools.com/tutorials/getting-started/setting-up-your-environment.html
        //
        // NOTE: this is not strictly required when using the eyes-selenium-java5 SDK.
        //applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");
        
        eyesServerUrl = System.getenv("APPLITOOLS_SERVER_URL");

        // Read the headless mode setting from an environment variable.
        // Use headless mode for Continuous Integration (CI) execution.
        // Use headed mode for local development.
        headless = Boolean.parseBoolean(System.getenv().getOrDefault("WEBDRIVER_HEADLESS", "true"));
        
        eyesIsDisabled = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_IS_DISABLED", "false"));

        // Switch to the V2 URL to force some diffs (set FORCE_DIFFERENCES env var to "true")
        forceDiffs = Boolean.parseBoolean(System.getenv().getOrDefault("FORCE_DIFFERENCES", "false"));

        // Create the runner 
        runner = new ClassicRunner();

        // Create a new batch for tests.
        // A batch is the collection of visual checkpoints for a test suite.
        // Batches are displayed in the dashboard, so use meaningful names.
        batch = new BatchInfo(ApplitoolsWebSiteTest.batchName);
        
        // Add Property key/value pairs to group and filter batch results in the Dashboard UI.
        batch.addProperty("Environment", "LOCAL");
        batch.addProperty("Language", "Java");
        batch.addProperty("SDK", "Selenium Java5");
        batch.addProperty("Framework", "TestNG");
        batch.addProperty("Scope", "Basic");
        batch.addProperty("Hooks", "true");
        batch.addProperty("Runner", "Classic");

        // Create a configuration for Applitools Eyes.
        //System.out.printf("Before: Class for %s - APPLITOOLS creating config\n", this.getClass().getSimpleName());
        config = new Configuration();

        if (Strings.isNotNullAndNotEmpty(eyesServerUrl)) {
            log.warn("\n\n\t--------> APPLITOOLS_SERVER_URL '{}' <-------- {}\n", eyesServerUrl);
            //config.setServerUrl(eyesServerUrl);
        }

        // Set the Applitools API key so test results are uploaded to your account.
        // If you don't explicitly set the API key with this call,
        // then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
        //config.setApiKey(applitoolsApiKey);

        // The BatchInfo is a component of the Configuration
        config.setBatch(batch);
        //config.setDisableBrowserFetching(true);

        log.info("End BeforeSuite");
    }
    
    @BeforeMethod
    public void createTestContext(ITestContext ctx, Method testMethod) {
        // Open the browser with a WebDriver instance - either ChromeDriver for local or RemoteWebDriver
        // Even if this test will render checkpoints for different setups in the Ultrafast Grid,
        // it still needs to run the test one time in a browser to capture snapshots.
        if (ecx) {
            log.info("Tests will execute on the Applitools Self-Healing Execution Cloud! [{}]", Eyes.getExecutionCloudURL());
            // Open the browser remotely in the Execution Cloud.
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setBrowserName("chrome");
            try {
                this.driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), caps);
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to the Applitools Self-Healing Execution Cloud at " + Eyes.getExecutionCloudURL(), e);
            }
        }
        else {
            log.info("Tests will execute on your local Chrome browser...");
            // Open the browser with a local ChromeDriver instance.
            ChromeOptions opts = new ChromeOptions();
            if (headless) opts.addArguments("--headless=new");
            this.driver = new ChromeDriver(opts);
        }
        
        // Set the browser window size - height, width
        driver.manage().window().setSize(new Dimension(ApplitoolsWebSiteTest.browserHeight, ApplitoolsWebSiteTest.browserWidth));

        // Set an implicit wait of 10 seconds.
        // For larger projects, use explicit waits for better control.
        // https://www.selenium.dev/documentation/webdriver/waits/
        // The following call works for Selenium 4:
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // If you are using Selenium 3, use the following call instead:
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        if (ecx) jsExec.executeScript("applitools:startTest", 
                Map.ofEntries(
                        en("testName", ApplitoolsWebSiteTest.defaultTestName + " (Non-Eyes)"), 
                        en("appName", ApplitoolsWebSiteTest.appName), 
                        en("batch", 
                                Map.ofEntries(
                                        en("name", batch.getName()), 
                                        en("id", batch.getId()),
                                        en("properties", // Batch custom properties (working)
                                                List.of(
                                                        Map.ofEntries(
                                                                en("name", "Environment"),
                                                                en("value", "LOCAL")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "Language"),
                                                                en("value", "Java")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "SDK"),
                                                                en("value", "Selenium Java5")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "Framework"),
                                                                en("value", "TestNG")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "Scope"),
                                                                en("value", "Basic")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "Hooks"),
                                                                en("value", "true")
                                                        ),
                                                        Map.ofEntries(
                                                                en("name", "Runner"),
                                                                en("value", "Classic")
                                                        )
                                                )
                                        )
                                )
                        ),
                        en("properties", // Test custom properties (not working)
                                List.of(
                                        Map.ofEntries(
                                                en("name", "Function"),
                                                en("value", "Login")
                                        ),
                                        Map.ofEntries(
                                                en("name", "Letter"),
                                                en("value", "B")
                                        ),
                                        Map.ofEntries(
                                                en("name", "Number"),
                                                en("value", "2")
                                        ),
                                        Map.ofEntries(
                                                en("name", "Boolean"),
                                                en("value", "false")
                                        )
                                )
                        )
                )
         );

    }

    @Test
    public void executionCloudNonEyesTest() {
        log.info("Start non-eyes test");
        ApplitoolsWebSiteTest.runSingleTest(driver, null, forceDiffs);
    }
    
    @DataProvider
    public Object[][] loginPairs() {
        // Switch to the V2 URL to force some diffs (set FORCE_DIFFERENCES env var to "true")
        return new Object[][] {
            new Object[] {
                    ApplitoolsWebSiteTest.defaultUsername, ApplitoolsWebSiteTest.defaultPassword
            /*}, new Object[] {
                    "nullpasswd", ""
            }, new Object[] {
                    "randomuser", "123456" */
            }
        };
    }
    
    @AfterMethod
    public void closeTestContext() {

        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        if (ecx) jsExec.executeScript("applitools:endTest", Map.ofEntries(en("status", "Passed")));

        // Quit the WebDriver instance.
        driver.quit();
    }
    
    @AfterSuite
    public void closeBatchContext() {
        try {
            // Gets results of all the tests AND implicitly closes the Batch
            TestResultsSummary allTestResults = runner.getAllTestResults(false);
            
            logTestResults = Boolean.parseBoolean(System.getenv().getOrDefault("LOG_DETAILED_TEST_RESULTS", "false"));
            if (logTestResults) {
                ApplitoolsWebSiteTest.logTestResults(allTestResults);
            } else {
                log.info("RESULTS: {}", allTestResults);
            }
        } catch (DiffsFoundException ex) {
            log.error("Applitools Eyes tests found differences! {}", ex);
        }
    }
    
    public TestEntry en(String key, Object value) {
        return new TestEntry(key, value);
    }

    public class TestEntry implements Entry<String, Object> {
        
        private String k;
        private Object v;

        public TestEntry(String key, Object value) {
            k = key;
            v = value;
        }

        @Override
        public String getKey() {
            return k;
        }

        @Override
        public Object getValue() {
            return v;
        }

        @Override
        public Object setValue(Object value) {
            v = value;
            return v;
        }
    }

}
