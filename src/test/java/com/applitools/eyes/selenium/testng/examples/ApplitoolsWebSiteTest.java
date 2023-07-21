package com.applitools.eyes.selenium.testng.examples;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

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
    
    private static final String pageURL = "https://www.pwc.com/";
    
    public static void runTest(WebDriver driver, Eyes eyes, boolean forceDiffs) {
        // Load the login page.
        driver.get(pageURL);
        log.info("Loading Site: {}", pageURL);
        
        String footerSelector = "div.glb-footer";
        // Get the text of the footer.
        String footerText = driver.findElement(By.cssSelector(footerSelector)).getText();
        log.trace("Found Footer: {}", footerText);
            
        if (forceDiffs) {
            // TODO: Inject some JavaScript to create arbitrary diffs on the page!
        }
        
        // Verify the full login page loaded correctly.
        if (eyes != null) {
            eyes.check(Target.window()
                    .fully()
                    .stitchOverlap(500)
                    .layoutBreakpoints(true)
                    .lazyLoad()
                    .layout(By.cssSelector("div.multi-featured-item__block"), By.cssSelector("div[ng-controller^=ContentListController]"))
                    .withName("Main Page"));
        }
        
        // Find all the main links in the footer
        String footerLinkBlockSelector = "div.glb-footer__block";
        WebElement footerLinkBlock = driver.findElement(By.cssSelector(footerLinkBlockSelector));
        String footerLinkSelector = "a.glb-footer__link";
        List<WebElement> footerLinkElements = footerLinkBlock.findElements(By.cssSelector(footerLinkSelector));
        //List<String> footerLinks = footerLinkElements.stream().map(WebElement::getText).toList();
        //footerLinkElements.toArray(new WebElement[footerLinkElements.size()]);
        List<String> footerLinks = new ArrayList<String>(footerLinkElements.size());
        for (WebElement footerLinkElement : footerLinkElements) {
            log.info("Found footer link '{}'", footerLinkElement.getText());
            footerLinks.add(footerLinkElement.getText());
        }
        
        SeleniumCheckSettings checkSettings = Target.window().fully()
                .stitchOverlap(500)
                .layoutBreakpoints(true)
                .lazyLoad()
                .layout(By.cssSelector("div[ng-controller^=ContentListController]"), By.cssSelector("div.feature-content__row"));
        
        // Start with the set of handles for all windows/tabs currently open in the browser
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            log.info("Found open window '{}'", windowHandle);
        }

        for (String footerLink : footerLinks) {
            // Find the next footer link and check that the text matches (a functional assertion for non-eyes tests)
            String foundLinkText = driver.findElement(By.cssSelector(footerLinkBlockSelector)).findElement(By.partialLinkText(footerLink)).getText();
            Assert.assertEquals(footerLink, foundLinkText, "footer link text does not match!");
            log.info("Found link for '{}'", foundLinkText);

            WebElement linkElement = driver.findElement(By.cssSelector(footerLinkBlockSelector)).findElement(By.partialLinkText(footerLink));
            String linkTarget = linkElement.getAttribute("target");
            log.info("Clicking link for '{}'", footerLink);
            driver.findElement(By.cssSelector(footerLinkBlockSelector)).findElement(By.partialLinkText(footerLink)).click();
            if (!(linkTarget == null || linkTarget.isBlank())) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                wait.until(ExpectedConditions.numberOfWindowsToBe(windowHandles.size() + 1));
                Set<String> newWindowHandles = driver.getWindowHandles();
                newWindowHandles.stream()
                        .filter(h -> { return !windowHandles.contains(h); })
                        .findFirst()
                        .ifPresent(h -> {
                            log.info("Switching to new window '{}'", h);
                            windowHandles.add(h);
                            driver.switchTo().window(h);
                        });
            }

            // Get the text of the footer.
            footerText = driver.findElement(By.cssSelector("div.glb-footer")).getText();
            log.trace("Found Footer: {}", footerText);

            log.info("Checking page for '{}'", footerLink);
            if (eyes != null) eyes.check(checkSettings.withName(footerLink));
        }
        
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
