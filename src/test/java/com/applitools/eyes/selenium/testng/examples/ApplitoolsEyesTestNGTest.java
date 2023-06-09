package com.applitools.eyes.selenium.testng.examples;

import java.lang.reflect.Method;
import java.time.Duration;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

public class ApplitoolsEyesTestNGTest {
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
    private static final String batchPrefix = "Eyes Demo: ";
    private static String appName = "Acme Bank";
    private static String batchName = batchPrefix + appName;
    private int browserHeight = 768;
    private int browserWidth = 1024;

    // Test-specific objects
    private WebDriver driver;
    private Eyes eyes;
    
    // Eyes Test meta-data values
    private String eyesTestName = "undef";
    private String testngTestName = "";
    private String testSuite = "";

    @BeforeClass
    public void beforeClass() {
        log.info("Before: Class for {}", Introspect.thisClass());
        
        // Read the Applitools API key from an environment variable.
        // To find your Applitools API key:
        // https://applitools.com/tutorials/getting-started/setting-up-your-environment.html
        applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");

        eyesServerUrl = System.getenv("APPLITOOLS_SERVER_URL");

        // Read the headless mode setting from an environment variable.
        // Use headless mode for Continuous Integration (CI) execution.
        // Use headed mode for local development.
        headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "true"));

        // An environment variable that disables all the Eyes API calls.
        eyesIsDisabled = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_IS_DISABLED", "false"));

        // An environment variable that disables all the Eyes API calls.
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
        batch.addProperty("Scope", "Suite");
        batch.addProperty("Hooks", "true");
        batch.addProperty("Runner", "Classic");

        // Create a configuration for Applitools Eyes.
        log.info("Before: Class for {} - APPLITOOLS creating config", Introspect.thisClass());
        config = new Configuration();
        
        if (Strings.isNotNullAndNotEmpty(eyesServerUrl)) {
            log.warn("\n\n\t--------> APPLITOOLS_SERVER_URL '{}' <-------- {}\n", eyesServerUrl);
            config.setServerUrl(eyesServerUrl);
        }

        // Set the Applitools API key so test results are uploaded to your account.
        // If you don't explicitly set the API key with this call,
        // then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
        log.info("Before: Class for {} - APPLITOOLS setting API key", Introspect.thisClass());
        config.setApiKey(applitoolsApiKey);
        log.debug("Before: Class for {} - APPLITOOLS API key set '{}'", Introspect.thisClass(), applitoolsApiKey);

        // Set the config batch.
        config.setBatch(batch);
    }

    @BeforeMethod
    public void beforeMethod(Method testMethod, Object[] params) {
        log.info("Before: Method for {}", testMethod.getName());
        
        String testName = testngTestName.isBlank() ? testMethod.getName() : testngTestName;
        eyesTestName = params[2].equals("applibot") ? testName : String.format("%s#%s", testName, params[2]);

        // This method sets up each test with its own ChromeDriver and Applitools Eyes objects.

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
        eyes.setConfiguration(config);
        
        // Add property key/value pairs to filter and group test results in the Dashboard UI.
        eyes.addProperty("Function", "Login");
        eyes.addProperty("Letter", "A");
        eyes.addProperty("Number", "1");
        eyes.addProperty("Boolean", "true");
        
        // An environment variable that disables all the Eyes API calls.
        if (eyesIsDisabled) {
            log.warn("\n\n\t--------> ALL APPLITOOLS EYES API CALLS ARE DISABLED!!! <-------- {}\n", eyesIsDisabled);
        }
        eyes.setIsDisabled(eyesIsDisabled);
        
        // Open Eyes to start visual testing.
        // It is a recommended practice to set all four inputs:
        log.info("Before: Method for {} - EYES opening eyes", eyesTestName);
        eyes.open(
                driver,       // WebDriver object to "watch"
                appName,      // The name of the app under test
                eyesTestName, // The name of the test case
                              // The viewport size for the local browser
                new RectangleSize(browserWidth, browserHeight));
 
        log.info("Before: Method for {} - EYES opened", eyesTestName);
     }

    @Test( priority = 10, dataProvider = "loginPairs" )
    public void loginPageTest(String pageURL, String pageName, String username, String password) {
        // This test covers login for the Applitools demo site, which is a dummy banking app.
        // The interactions use typical Selenium WebDriver calls,
        // but the verifications use one-line snapshot calls with Applitools Eyes.
        // If the page ever changes, then Applitools will detect the changes and highlight them in the dashboard.
        // Traditional assertions that scrape the page for text values are not needed here.

        log.info("Running test '{}' getting {}", eyesTestName, pageURL);
        
        // Load the login page.
        driver.get(pageURL);

        // Verify the full login page loaded correctly.
        eyes.check(Target.window().fully().withName(pageName));

        // Perform login.
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("log-in")).click();

        // Verify the full main page loaded correctly.
        // This snapshot uses LAYOUT match level to avoid differences in closing time text.
        eyes.check(Target.window().fully().withName("Main page"));
    }
    
    @DataProvider
    public Object[][] loginPairs() {
        // Switch to the V2 URL to force some diffs (set FORCE_DIFFERENCES env var to "true")
        String pageURL = forceDiffs ? 
                "https://demo.applitools.com/index_v2.html" :
                "https://demo.applitools.com";
        String pageName = "Login page";
        return new Object[][] {
            new Object[] {
                    pageURL, pageName, "applibot", "I<3VisualTests"
            }, new Object[] {
                    pageURL, pageName, "nullpasswd", ""
            }, new Object[] {
                    pageURL, pageName, "randomuser", "123456"
            }
        };
    }
    
    @AfterMethod
    public void afterMethod(Method testMethod) {
        log.info("After:  Method for {}", testMethod.getName());

        // Quit the WebDriver instance.
        driver.quit();

        // Close Eyes to tell the server it should display the results.
        eyes.closeAsync();

        // Warning: `eyes.closeAsync()` will NOT wait for visual checkpoints to complete.
        // You will need to check the Applitools dashboard for visual results per checkpoint.
        // Note that "unresolved" and "failed" visual checkpoints will not cause the TestNG test to fail.

        // If you want the TestNG test to wait synchronously for all checkpoints to complete, then use `eyes.close()`.
        // If any checkpoints are unresolved or failed, then `eyes.close()` will make the TestNG test fail.
    }

    @AfterClass
    public void afterClass() {
         log.info("After:  Class for {}", Introspect.thisClass());

        // Close the batch and report visual differences to the console.
        // Note that it forces TestNG to wait synchronously for all visual checkpoints to complete.
        try {
            TestResultsSummary allTestResults = runner.getAllTestResults();
            log.info("RESULTS: {}", allTestResults);
        } catch (DiffsFoundException ex) {
            
        }
    }

    @BeforeTest
    public void beforeTest(ITestContext ctx) {
        testngTestName = ctx.getName();
        log.info("Before: Test for {}", testngTestName);
    }

    @AfterTest
    public void afterTest(ITestContext ctx) {
        log.info("After:  Test for {}", testngTestName);
    }

    @BeforeSuite
    public void beforeSuite(ITestContext ctx) {
        testSuite = ctx.getSuite().getName();
        log.info("Before: Suite for {}", testSuite);
        if (!testSuite.isBlank()) {
            appName = testSuite.isBlank() ? appName : testSuite;
            batchName = batchPrefix + appName;
        }
    }

    @AfterSuite
    public void afterSuite() {
        log.info("After:  Suite for {}", testSuite);
    }

}
