package com.applitools.eyes.selenium.testng.examples;

import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
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
    private static boolean logTestResults;

    // Applitools Eyes Context objects - shared for all tests
    private static BatchInfo batch;
    private static Configuration config;
    private static EyesRunner runner;
    
    // Test-specific objects
    private WebDriver driver;
    private Eyes eyes;
    
    // Eyes Test meta-data values
    private String batchName = ApplitoolsWebSiteTest.batchName;
    private String appName = ApplitoolsWebSiteTest.appName;
    private String testngTestName = "";
    private String testngSuiteName = "";

    /* 
     * BeforeSuite methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @BeforeSuite
    public void beforeSuite(ITestContext ctx) {
        log.info("Before: Suite for suite '{}'", ctx.getSuite().getName());
        testngSuiteName = ctx.getSuite().getName();
        if (!testngSuiteName.isBlank() && !testngSuiteName.startsWith("Default suite")) {
            appName = testngSuiteName.isBlank() ? appName : testngSuiteName;
            batchName = ApplitoolsWebSiteTest.batchPrefix + appName;
        }
    }

    /* 
     * BeforeTest methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @BeforeTest
    public void beforeTest(ITestContext ctx) {
        log.info("Before: Test for test '{}' suite '{}'", ctx.getName(), ctx.getSuite().getName());
        testngTestName = ctx.getName();
    }

    /* 
     * BeforeClass methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @BeforeClass
    public void beforeClass(ITestContext ctx) {
        log.info("Before: Class for class {} for test '{}' suite '{}'", Introspect.thisClass(), ctx.getName(), ctx.getSuite().getName());
        
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

    /* 
     * BeforeMethod methods can declare an ITestContext and/or java.lang.reflect.Method parameters for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     * 
     * If the test method picks up parameter values from a DataProvider method, the parameter values are also supplied to the BeforeMethod.
     * You can use those parameter values to create the Eyes testName as in the example below.
     * 
     * see: https://testng.org/doc/documentation-main.html#parameters-dataproviders
     */
    @BeforeMethod
    public void beforeMethod(ITestContext ctx, Method testMethod, Object[] params) {
        log.info("Before: Method for method {} class {} for test '{}' suite '{}'", testMethod.getName(), testMethod.getDeclaringClass().getSimpleName(), ctx.getName(), ctx.getSuite().getName());
        log.info("Before: Method PARAMS {}", params);
        
        String testName = (testngTestName.isBlank() || testngTestName.startsWith("Default test")) ? testMethod.getName() : testngTestName;
        // You can use values supplied by a DataProvider in your test name.
        testName = params[1].equals("applibot") ? testName : String.format("%s#%s", testName, params[1]);

        // This method sets up each test with its own Selenium WebDriver and Applitools Eyes objects.

        // Checks whether we will run this test on a local browser or on the Execution Cloud
        boolean ecx = Boolean.parseBoolean(System.getenv().getOrDefault("APPLITOOLS_EXECUTION_CLOUD", "false"));

        // Open the browser with a WebDriver instance - either ChromeDriver for local or RemoteWebDriver
        // Even if this test will render checkpoints for different setups in the Ultrafast Grid,
        // it still needs to run the test one time in a browser to capture snapshots.
        if (ecx) {
            // Open the browser remotely in the Execution Cloud.
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setBrowserName("chrome");
            log.info("Eyes tests will execute on the Applitools Self-Healing Execution Cloud! [{}]", Eyes.getExecutionCloudURL());
            try {
                this.driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), caps);
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to the Applitools Self-Healing Execution Cloud at " + Eyes.getExecutionCloudURL(), e);
            }
        }
        else {
            // Open the browser with a local ChromeDriver instance.
            ChromeOptions opts = new ChromeOptions();
            if (headless) opts.addArguments("--headless=new");
            this.driver = new ChromeDriver(opts);
            log.info("Eyes tests will execute on your local Chrome browser...");
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
        log.info("Before: Method for {} - EYES opening eyes", testName);
        eyes.open(
                driver,       // WebDriver object to "watch"
                appName,      // The name of the app under test
                testName, // The name of the test case
                              // The viewport size for the local browser
                new RectangleSize(ApplitoolsWebSiteTest.browserWidth, ApplitoolsWebSiteTest.browserHeight));
 
        log.info("Before: Method for {} - EYES opened", testName);
    }

    /* 
     * Test methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     * 
     * This text method example picks up parameter values from the loginPairs DataProvider method.
     * 
     * see: https://testng.org/doc/documentation-main.html#parameters-dataproviders
     */
    @Test( priority = 10, dataProvider = "loginPairs" )
    public void testNgWebSiteTest(ITestContext ctx, String username, String password) {
        log.info("Test:   running method {} class {} for test '{}' suite '{}' PARAMS[username '{}' password '{}']", Introspect.thisMethod(), Introspect.thisClass(), ctx.getName(), username, password);
        ApplitoolsWebSiteTest.runTest(driver, eyes, forceDiffs, username, password);
    }
    
    /* 
     * DataProvider methods can declare ITestContext and/or java.lang.reflect.Method parameters for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @DataProvider
    public Object[][] loginPairs(ITestContext ctx, Method testMethod) {
        log.info("Before: DataProvider for method {} class {} for test '{}' suite '{}'", testMethod.getName(), testMethod.getDeclaringClass().getSimpleName(), ctx.getName(), ctx.getSuite().getName());
        return new Object[][] {
            new Object[] {
                    "applibot", "I<3VisualTests"
            /*}, new Object[] {
                    "nullpasswd", ""
            }, new Object[] {
                    "randomuser", "123456"*/
            }
        };
    }
    
    /* 
     * AfterMethod methods can declare an ITestResult and/or java.lang.reflect.Method parameters for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @AfterMethod
    public void afterMethod(ITestResult result, Method testMethod) {
        log.info("After:  Method for method {} class {}", testMethod.getName(), testMethod.getDeclaringClass().getSimpleName());
        log.info("After:  Method for method {} class {} for test '{}' suite '{}'", result.getMethod().getMethodName(), result.getTestClass().getName(), result.getTestContext().getName(), result.getTestContext().getSuite().getName());

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

    /* 
     * AfterClass methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @AfterClass
    public void afterClass(ITestContext ctx) {
        log.info("After:  Class for class {} for test '{}' suite '{}'", Introspect.thisClass(), ctx.getName(), ctx.getSuite().getName());

        // Call runner.getAllTestResults() to close the batch and report visual differences to the console.
        // Note that this call forces TestNG to wait synchronously for all visual checkpoints to complete.
        try {
            // Pass true to Runner.getAllTestResults(boolean) to throw a DiffsFoundException if any test found diffs.
            TestResultsSummary allTestResults = runner.getAllTestResults(true);

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

    /* 
     * AfterTest methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @AfterTest
    public void afterTest(ITestContext ctx) {
        log.info("After:  Test for test '{}' suite '{}'", ctx.getName(), ctx.getSuite().getName());
    }

    /* 
     * AfterSuite methods can declare an ITestContext parameter for native injection.
     * 
     * see: https://testng.org/doc/documentation-main.html#native-dependency-injection
     */
    @AfterSuite
    public void afterSuite(ITestContext ctx) {
        log.info("After:  Suite for suite '{}'", ctx.getSuite().getName());
    }

}
