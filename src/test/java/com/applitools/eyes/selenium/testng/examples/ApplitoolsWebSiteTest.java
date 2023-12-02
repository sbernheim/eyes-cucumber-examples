package com.applitools.eyes.selenium.testng.examples;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.SessionUrls;
import com.applitools.eyes.StepInfo;
import com.applitools.eyes.TestResultContainer;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsStatus;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.StepInfo.AppUrls;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

public class ApplitoolsWebSiteTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    // Eyes Batch and Test meta-data values
    static final String batchPrefix = "Eyes Demo: ";
    static String appName = "Acme Bank";
    static String batchName = batchPrefix + appName;
    static String defaultTestName = "loginTest";
    static int browserHeight = 768;
    static int browserWidth = 1024;

    private static final String pageURLDiffs = "https://demo.applitools.com/index_v2.html";
    private static final String pageURL = "https://demo.applitools.com";
    
    static final String defaultUsername = "applibot";
    static final String defaultPassword =  "I<3VisualTests";
    
    public static void runSingleTest(WebDriver driver, Eyes eyes, boolean forceDiffs) {
        runTest(driver, eyes, forceDiffs, defaultUsername, defaultPassword);
    }

    public static void runTest(WebDriver driver, Eyes eyes, boolean forceDiffs, String username, String password) {
        // This test covers login for the Applitools demo site, which is a dummy banking app.
        // The interactions use typical Selenium WebDriver calls,
        // but the verifications use one-line snapshot calls with Applitools Eyes.
        // If the page ever changes, then Applitools will detect the changes and highlight them in the dashboard.
        // Traditional assertions that scrape the page for text values are not needed here.

        // Load the login page.
        driver.get(forceDiffs ? pageURL : pageURLDiffs);
        
        // Get the text of the footer.
        /*String footerText = driver.findElement(By.cssSelector("div.site-footer")).getText();
        log.trace("Found Footer: {}", footerText);*/
            
        // Verify the full login page loaded correctly.
        if (eyes != null) eyes.check(Target.window().fully().withName("Login page"));

        // Perform login.
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("log-in")).click();

        // Verify the full main page loaded correctly.
        // This snapshot uses LAYOUT match level to avoid differences in closing time text.
        if (eyes != null) eyes.check(Target.window().fully().withName("Main page"));
    }
    
    /*
     * Logs a full description of all TestResults in the passed TestResultsSummary object.
     * 
     * A TestResultsSummary object models all of the test results for all the tests run by the
     * an instance of the Runner class.  Usually all those tests are within the same batch, but a
     * single Runner instance can be used to run tests for more than one batch.
     * 
     * A TestResultsSummary object contains one or more TestResultContainer objects.
     * 
     * A TestResultContainer object models a test result with a potential error status and
     * meta-data about the browser or device that rendered the check images for the result.
     * 
     * A TestResults object models a single test result.
     * 
     * A TestResults object contains one or more StepInfo objects that model the step results.
     */
    public static void logTestResults(TestResultsSummary allTestResults) {
        // Traverse the array of TestResultContainer objects in the TestResultsSummary for detailed
        // information about the results of each test launched by this Runner.
        String currentBatchId = "";

        // There are two ways to get all the test results from the TestResultsSummary object!
        //
        // The iterator method returns a generic java.util.Iterator of TestResultContainer objects.
        //Iterator<TestResultContainer> it = allTestResults.iterator();
        // 
        // The getAllTestResults method returns an array of TestResultContainer objects.
        for (TestResultContainer resultContainer : allTestResults.getAllResults()) {
            String prefix = "    ";

            // A TestResults object models a single test result containing multiple step results.
            TestResults results = resultContainer.getTestResults(); 

            String batchId = results.getBatchId();
            String batchName = results.getBatchName();
            String branchName = results.getBranchName(); // Will be "default" unless your test set a batch name
            String device = "DESKTOP";

            if (!currentBatchId.equals(batchId)) {
                currentBatchId = batchId;
                log.info("Batch '{}' for baseline branch '{}' [{}]", batchName, branchName, batchId);

                SessionUrls testLinks = results.getAppUrls();
                if (testLinks != null) {
                    log.info("- batch link {}", testLinks.getBatch());
                }
            }
            
            logTestResultsInfo(results, prefix);

            RenderBrowserInfo browserInfo = resultContainer.getBrowserInfo(); // Info on how this test was rendered in the UFG
            if (browserInfo != null) {
                BrowserType browser = browserInfo.getBrowserType(); // An enum with values for all the Browsers supported by the UFG
                int height = browserInfo.getHeight();
                int width = browserInfo.getWidth();
                RectangleSize viewportSize = browserInfo.getViewportSize(); 
                RectangleSize deviceSize = browserInfo.getDeviceSize();
                String platform = browserInfo.getPlatform();
                String renderInfo = String.format("%s %s at [h %d x w %d]", 
                        platform, browser.getName(), height, width);

                EmulationBaseInfo chromeEmulationInfo = browserInfo.getEmulationInfo();
                IosDeviceInfo iosDeviceInfo = browserInfo.getIosDeviceInfo();
                if (chromeEmulationInfo != null) {
                    device = String.format("%s in %s mode emulated by Chrome", chromeEmulationInfo.getDeviceName(), chromeEmulationInfo.getScreenOrientation());
                } else if (iosDeviceInfo != null) {
                    device = String.format("%s in %s mode as an emulated iOS device", iosDeviceInfo.getDeviceName(), iosDeviceInfo.getScreenOrientation());
                }

                log.info("{}- rendered on '{}' deviceSize [{}] viewportSize [{}] with '{}'", prefix, device, deviceSize, viewportSize, renderInfo);
                
                String baselineEnvName = browserInfo.getBaselineEnvName();
                if (baselineEnvName != null && !baselineEnvName.isBlank()) {
                    log.info("{}- compared to named environment '{}'", prefix, baselineEnvName);
                }
            }

            logTestResultsSteps(results, prefix);

            Throwable testException = resultContainer.getException(); // Should be null if there was no Exception
            if (testException != null) log.warn("{}- Exception thrown:", prefix, testException);
        }
    }
    
    /*
     * Logs a full description of the passed TestResults object and its contents.
     * 
     * A TestResults object models a single test result.
     * 
     * A TestResults object contains one or more StepInfo objects that model the step results.
     */
    public static void logTestResults(TestResults results) {
        String prefix = "";
        logTestResultsInfo(results, prefix);
        logTestResultsSteps(results, prefix);
    }

    /*
     * Logs the status, test name, app name, and meta-data of the passed TestResults object
     */
    public static void logTestResultsInfo(TestResults results, String prefix) {
       String appName = results.getAppName();
       String testName = results.getName();
       String hostApp = results.getHostApp();
       String hostOS = results.getHostOS();
       RectangleSize hostSize = results.getHostDisplaySize();
       Calendar startedAtCal = results.getStartedAt();
       String startedAt = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a z").format(startedAtCal.getTime());
       TestResultsStatus testStatus = results.getStatus(); // An enum with Passed, Unresolved, Failed and NotOpened values
       int durationInSeconds = results.getDuration();

       log.info("{}{} : Test '{}' of App '{}'", 
               prefix, testStatus, appName, testName);
       log.info("{}- on '{}' '{}' at [{}]", 
               prefix, hostOS, hostApp, hostSize);
       log.info("{}- started at '{}' runtime {} seconds", 
               prefix, startedAt, durationInSeconds);

       if (results.isAborted()) log.warn("{}- THIS TEST WAS ABORTED BEFORE COMPLETION!", prefix);
       if (results.isNew()) log.info("{}- This is a NEW test!", prefix);
       if (results.isPassed()) log.info("{}- This test passed!", prefix);
       if (results.isDifferent()) log.info("{}- This test found differences!", prefix);

       SessionUrls testLinks = results.getAppUrls();
       if (testLinks != null) {
           log.info("{}- test link  {}", prefix, testLinks.getSession());
           if (prefix.isEmpty()) log.info("{}- batch link {}", testLinks.getBatch());
       }
    }
       
    /*
     * Logs the content of each StepInfo in the passed TestResults object
     */
    public static void logTestResultsSteps(TestResults results, String prefix) {
       int stepsCount = results.getSteps();
       boolean isNew = results.isNew();
       int stepsMatched = results.getMatches();
       int stepsMatchedExactly = results.getExactMatches();
       int stepsMismatched = results.getMismatches();
       int stepsMissing = results.getMissing();
       log.info("{}+ {} of {} steps matched ({} exactly), {} had differences, and {} were missing", 
               prefix, stepsMatched, stepsCount, stepsMatchedExactly, stepsMismatched, stepsMissing);
       
       int stepNumber = 0;
       for (StepInfo step : results.getStepsInfo()) {
           String stepName = step.getName();
           String stepResult = step.getIsDifferent() ? "found differences" : "matched the baseline";
           String stepStatus = step.getIsDifferent() ? "DIFFS" : "MATCH";
           if (!step.getHasBaselineImage()) {
               stepResult = isNew ? "is a new test step" : "did not have a baseline image";
               stepStatus = isNew ? "NEW T" : "NEW S";
           }
           if (!step.getHasCurrentImage()) {
               stepResult = "did not have a checkpoint image";
               stepStatus = "UNDEF";
           }
           log.info("{}----+ {} step '{}' ({} of {}) {}", prefix, stepStatus, stepName, ++stepNumber, stepsCount, stepResult);
           AppUrls stepLinks = step.getAppUrls();
           if (stepLinks != null) {
               log.info("{}    - view link : {}", stepLinks.getStep());
               log.info("{}    - edit link : {}", stepLinks.getStepEditor());
           }
       }
    }

}
