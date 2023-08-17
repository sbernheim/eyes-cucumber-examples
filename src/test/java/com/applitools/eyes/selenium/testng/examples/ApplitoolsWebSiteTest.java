package com.applitools.eyes.selenium.testng.examples;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.applitools.eyes.visualgrid.model.EmulationBaseInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;

public class ApplitoolsWebSiteTest {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    //private static final String pageURLDiffs = "https://demo.applitools.com/index_v2.html";
    private static final String pageURL = "https://www.fanniemae.com";
    
    private static final String defaultSearchTerm = "mortgage rates";
    
    public static void runSingleTest(WebDriver driver, Eyes eyes, boolean forceDiffs) {
        runTest(driver, eyes, forceDiffs, defaultSearchTerm);
    }

    public static void runTest(WebDriver driver, Eyes eyes, boolean forceDiffs, String searchTerm) {
        // Load the login page.
        driver.get(pageURL);
        
        // Get the text of the footer.
        String footerText = driver.findElement(By.cssSelector("div#footer-new")).getText();
        log.trace("Found Footer: {}", footerText);
        
        // Accept cookies
        String cookieButtonSelector = "button#onetrust-accept-btn-handler";
        WebDriverWait acceptCookieWait = new WebDriverWait(driver, 30);
        try {
            acceptCookieWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cookieButtonSelector)));
            driver.findElement(By.cssSelector(cookieButtonSelector)).click();
        } catch (TimeoutException e) {
            log.info("Did not find the Accept Cookies button for this test run: CSS Selector '{}'", cookieButtonSelector);
        }
            
        // Verify the full login page loaded correctly.
        if (eyes != null) eyes.check(Target.window().fully().withName("Main page"));

        // Click on the search button
        // Enter search term
        // Perform search
        driver.findElement(By.cssSelector("button.fm-toggle-search")).click();;
        WebElement s = driver.findElement(By.cssSelector("#searchbox > div > div > div.magic-box-input > input"));
        s.sendKeys(searchTerm);
        s.sendKeys(Keys.ENTER);

        // Verify the full main page loaded correctly.
        if (eyes != null) eyes.check(Target.window().fully().withName("Search results"));
    }
    
    public static void logTestResults(TestResultsSummary allTestResults) {
        // You can also traverse the array of TestResultContainer objects in the TestResultsSummary for detailed
        // information about the results of each test launched by this Runner.
        String currentBatchId = "";
        for (TestResultContainer resultContainer : allTestResults.getAllResults()) {
            TestResults results = resultContainer.getTestResults(); // A single test result containing multiple step results

            String batchId = results.getBatchId();
            String batchName = results.getBatchName();
            String branchName = results.getBranchName(); // Will be "default" unless your test set a batch name
            SessionUrls testLinks = results.getAppUrls();

            if (currentBatchId.equals(batchId)) {
                currentBatchId = batchId;
                log.info("Batch '{}' for baseline branch '{}' [{}]", batchName, branchName, batchId);
                if (testLinks != null) {
                    log.info("link to batch : {}", testLinks.getBatch());
                }
            }

            String appName = results.getAppName();
            String testName = results.getName();
            String hostApp = results.getHostApp();
            String hostOS = results.getHostOS();
            RectangleSize hostSize = results.getHostDisplaySize();
            String device = "DESKTOP";
            Calendar startedAtCal = results.getStartedAt();
            String startedAt = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a z").format(startedAtCal.getTime());
            TestResultsStatus testStatus = results.getStatus(); // An enum with Passed, Unresolved, Failed and NotOpened values
            boolean isPassed = results.isPassed();
            boolean isNew = results.isNew();
            boolean isDifferent = results.isDifferent();
            boolean isAborted = results.isAborted();
            int durationInSeconds = results.getDuration();
            log.info("    {} : App '{}' Test '{}' on '{}' '{}' at [{}]", 
                    testStatus, appName, testName, hostOS, hostApp, hostSize);
            log.info("        started at '{}' runtime {} seconds", 
                    startedAt, durationInSeconds);

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

                log.info("        rendered on '{}' deviceSize [{}] viewportSize [{}] with '{}'", device, deviceSize, viewportSize, renderInfo);
                
                String baselineEnvName = browserInfo.getBaselineEnvName();
                if (baselineEnvName != null && !baselineEnvName.isBlank()) {
                    log.info("        compared to named environment '{}'", baselineEnvName);
                }
            }

            if (testLinks != null) {
                log.info("        link to test  : {}", testLinks.getSession());
            }
            
            if (isAborted) {
                log.warn("         NOTE: THIS TEST WAS ABORTED BEFORE COMPLETION!");
            } else if (isNew) {
                log.info("        This is a NEW test!");
            } else if (isPassed) {
                log.info("        This test passed!");
            } else if (isDifferent) {
                log.info("        This test found differences!");
            }

            int stepsCount = results.getSteps();
            int stepsMatched = results.getMatches();
            int stepsMatchedExactly = results.getExactMatches();
            int stepsMismatched = results.getMismatches();
            int stepsMissing = results.getMissing();
            log.info("        {} of {} steps matched ({} exactly), {} had differences, and {} were missing", 
                    stepsMatched, stepsCount, stepsMatchedExactly, stepsMismatched, stepsMissing);
            
            int stepNumber = 0;
            for (StepInfo step : results.getStepsInfo()) {
                String stepResult = step.getIsDifferent() ? "found differences" : "matched the baseline";
                if (!step.getHasBaselineImage()) stepResult = isNew ? "is a new step" : "did not have a baseline image";
                if (!step.getHasCurrentImage()) stepResult = "did not have a checkpoint image";
                log.info("        step {} of {} {}", ++stepNumber, stepsCount, stepResult);
                AppUrls stepLinks = step.getAppUrls();
                if (stepLinks != null) {
                    log.info("            link to view step : {}", stepLinks.getStep());
                    log.info("            link to edit step : {}", stepLinks.getStepEditor());
                }
            }

            Throwable testException = resultContainer.getException(); // Should be null if there was no Exception
            if (testException != null) log.info("        exception thrown:", testException);
        }
    }

}
