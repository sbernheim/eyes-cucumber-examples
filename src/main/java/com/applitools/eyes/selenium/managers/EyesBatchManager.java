package com.applitools.eyes.selenium.managers;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.applitools.eyes.selenium.settings.Settings;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;


public class EyesBatchManager {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    private static final Settings settings = Settings.readSettings();

    private static BatchInfo batchInfo;
    private static Configuration config;
    private static EyesRunner runner;

    private static RectangleSize viewportSize = new RectangleSize(1024, 768);
    private static String batchName = "Eyes Test Application Batch";
    private static String appName = "Test Application";

    public EyesBatchManager() {
        // Anything?
    }
    
    public String batchName() {
        return batchName;
    }
    
    public void setBatchName(String batchName) {
        log.info("Thread ID [{}]--> EYES BATCH setting batch name {}", getThreadId(), batchName);
        EyesBatchManager.batchName = batchName;
    }
    
    public String appName() {
        return appName;
    }
    
    public void setAppName(String applicationName) {
        log.info("Thread ID [{}]--> EYES BATCH setting application name {}", getThreadId(), applicationName);
        EyesBatchManager.appName = applicationName;
    }
    
    public RectangleSize viewportSize() {
        return viewportSize;
    }
    
    public void setViewportSize(int width, int height) {
        log.info("Thread ID [{}]--> EYES BATCH setting viewport size [width {}, height {}]", getThreadId(), width, height);
        viewportSize = new RectangleSize(width, height); // The viewport size for the local browser
    }
    
    public BatchInfo batchInfo() {
        if (batchInfo == null) createBatchInfo();
        return batchInfo;
    }
    
    public String batchId() {
        return batchInfo().getId();
    }
    
    public EyesRunner runner() {
        if (runner == null) createRunner();
        return runner;
    }
    
    public Configuration config() {
        if (config == null) createConfiguration();
        return config;
    }

    public BatchInfo createBatchInfo() {
        log.info("Thread ID [{}]--> EYES BATCH creating BatchInfo for '{}'", getThreadId(), batchName);

        // Create a new Batch for tests.
        // A Batch is the collection of Tests that can each capture multiple visual checkpoints.
        // Batches are displayed in the dashboard, so use meaningful names.
        batchInfo = new BatchInfo(batchName);

        // Use the Batch ID from the environment or system settings if applicable.
        if (settings.batchId.isNotBlank()) {
            batchInfo.setId(settings.batchId.get());
            log.info("Thread ID [{}]--> EYES BATCH using BatchID '{}'", getThreadId(), batchInfo.getId());
        }

        log.info("Thread ID [{}]--> EYES BATCH created BatchInfo with BatchID {}", getThreadId(), batchInfo.getId());
        return batchInfo;
    }
    
    /**
     * Create a runner for rendering checkpoints on the test execution machine.
     */
    public EyesRunner createClassicRunner() {
        log.info("Thread ID [{}]--> EYES BATCH creating ClassicRunner", getThreadId());
        runner = new ClassicRunner();
        return runner;
    }

    /**
     * Create a runner for rendering checkpoints on the Applitools UltraFast Grid.
     */
    public EyesRunner createVisualGridRunner() {
        log.info("Thread ID [{}]--> EYES BATCH creating VisualGridRunner", getThreadId());
        // Create a runner for rendering checkpoints on the Applitools UltraFast Grid.
        // Concurrency refers to the number of visual checkpoints Applitools will perform in parallel.
        // Warning: If you have a free account, then concurrency will be limited to 1.
        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(settings.concurrency));
        return runner;
    }
    
    public EyesRunner createRunner() {
        return settings.renderOnUltraFastGrid ?
                createVisualGridRunner() :
                    createClassicRunner();
    }
    
    public Configuration createClassicConfiguration() {
        log.info("Thread ID [{}]--> EYES BATCH creating Classic configuration for batch '{}'", getThreadId(), batchName);

        config = new Configuration();

        if (settings.branchName.isNotBlank()) {
            config.setBranchName(settings.branchName.get());
            log.info("Thread ID [{}]--> EYES BATCH using branch name '{}'", getThreadId(), config.getBranchName());
        }
        
        settings.accessibility().ifPresent(s -> {
            config.setAccessibilityValidation(s);
            log.info("Thread ID [{}]--> EYES BATCH setting Accessibility validation: {}", getThreadId(), s);
        });

        // Set the Applitools API key so test results are uploaded to your account.
        // If you don't explicitly set the API key with this call,
        // then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
        if (settings.applitoolsApiKey.isNotBlank()) {
            log.info("Thread ID [{}]--> Found Applitools API Key!", getThreadId());
            /**
             * This is no longer required for eyes-selenium-java5!
             * 
             * Use the following method if you're using eyes-selenium-java-3
             */
            //config.setApiKey(settings.applitoolsApiKey.get());
        }
        
        if (settings.serverUrl.isNotBlank()) {
            config.setServerUrl(settings.serverUrl.get());
        }

        if (settings.isDisabled) {
            config.setIsDisabled(true);
            log.info("Thread ID [{}]--> EYES BATCH \n\n\t--------> ALL APPLITOOLS EYES API CALLS ARE DISABLED!!! <-------- {}\n", getThreadId(), settings.isDisabled);
        }

        // Set the batch for the config.
        config.setBatch(batchInfo());
        
        return config;
    }
    
    public Configuration createVisualGridConfiguration() {
        log.info("Thread ID [{}]--> EYES BATCH creating VisualGrid configuration for batch '{}'", getThreadId(), batchName);
        
        config = createClassicConfiguration();

        log.info("Thread ID [{}]--> EYES BATCH adding browser and device configurations", getThreadId());

        // Add different desktop browsers with different viewports for cross-browser testing in the Ultrafast Grid.
        /*if (baselineEnvironment.isPresent()) {
            System.out.printf("BeforeSuite : %s using baseline environment %s\n", thisMethod, baselineEnvironment.get());
            config.addBrowser(1024, 768, BrowserType.CHROME, baselineEnvironment.get()); // This is the "desktop-browser" baseline
            //config.addBrowser(1920, 1080, BrowserType.CHROME, baselineEnvironment.get());
            //config.addBrowser(3840, 2160, BrowserType.FIREFOX, baselineEnvironment.get());
            //config.addBrowser(3008, 1692, BrowserType.SAFARI, baselineEnvironment.get());
            //config.addBrowser(2560, 1440, BrowserType.SAFARI_TWO_VERSIONS_BACK, baselineEnvironment.get());
            //config.addBrowser(1504, 846, BrowserType.EDGE_LEGACY, baselineEnvironment.get());
            //config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM, baselineEnvironment.get());
            //config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM_TWO_VERSION_BACK, baselineEnvironment.get());
            //config.addBrowser(1024, 768, BrowserType.IE_11, baselineEnvironment.get());
            //config.addBrowser(1024, 768, BrowserType.IE_10, baselineEnvironment.get());
        } else {*/
            config.addBrowser(1024, 768, BrowserType.CHROME); // This is the "desktop-browser" baseline
            //config.addBrowser(1920, 1080, BrowserType.CHROME);
            //config.addBrowser(3840, 2160, BrowserType.FIREFOX);
            //config.addBrowser(3008, 1692, BrowserType.SAFARI);
            //config.addBrowser(2560, 1440, BrowserType.SAFARI_TWO_VERSIONS_BACK);
            //config.addBrowser(1504, 846, BrowserType.EDGE_LEGACY);
            //config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM);
            //config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM_TWO_VERSION_BACK);
            //config.addBrowser(1024, 768, BrowserType.IE_11);
            //config.addBrowser(1024, 768, BrowserType.IE_10);
        //}

        // Add mobile emulation devices with different orientations for cross-browser testing in the Ultrafast Grid.
        /*if (mobileBaselineEnvironment.isPresent()) {
            System.out.printf("BeforeSuite : %s using mobile baseline environment %s\n", thisMethod, mobileBaselineEnvironment.get());
            config.addDeviceEmulation(DeviceName.Pixel_5, ScreenOrientation.PORTRAIT, mobileBaselineEnvironment.get()); // I think this is the "mobile-browser" baseline
            //config.addDeviceEmulation(DeviceName.iPad_Pro, ScreenOrientation.PORTRAIT, mobileBaselineEnvironment.get());
            //config.addDeviceEmulation(DeviceName.iPhone_11_Pro, ScreenOrientation.PORTRAIT, mobileBaselineEnvironment.get());
            //config.addDeviceEmulation(DeviceName.Pixel_4_XL, ScreenOrientation.LANDSCAPE, mobileBaselineEnvironment.get());
            //config.addDeviceEmulation(DeviceName.OnePlus_7T, ScreenOrientation.LANDSCAPE, mobileBaselineEnvironment.get());
            //config.addDeviceEmulation(DeviceName.Kindle_Fire_HDX, ScreenOrientation.PORTRAIT, mobileBaselineEnvironment.get());
        } else {*/
            config.addDeviceEmulation(DeviceName.Pixel_5, ScreenOrientation.PORTRAIT); // I think this is the "mobile-browser" baseline
            //config.addDeviceEmulation(DeviceName.iPad_Pro, ScreenOrientation.PORTRAIT);
            //config.addDeviceEmulation(DeviceName.iPhone_11_Pro, ScreenOrientation.PORTRAIT);
            //config.addDeviceEmulation(DeviceName.Pixel_4_XL, ScreenOrientation.LANDSCAPE);
            //config.addDeviceEmulation(DeviceName.OnePlus_7T, ScreenOrientation.LANDSCAPE);
            //config.addDeviceEmulation(DeviceName.Kindle_Fire_HDX, ScreenOrientation.PORTRAIT);
        //}

        return config;
    }
    
    public Configuration createConfiguration() {
        return settings.renderOnUltraFastGrid ?
                createVisualGridConfiguration() :
                    createClassicConfiguration();
    }
    
    public void openEyesBatch() {
        log.info("Thread ID [{}]--> EYES BATCH {} opening", getThreadId(), batchInfo().getId());

        // Create the Applitools Eyes test runner
        log.info("Thread ID [{}]--> EYES BATCH creating runner", getThreadId());
        createRunner();

        // Create a configuration for Applitools Eyes.
        log.info("Thread ID [{}]--> EYES BATCH creating config", getThreadId());
        Configuration config = createConfiguration();

        log.info("Thread ID [{}]--> EYES BATCH {} open", getThreadId(), config.getBatch().getId());
    }
    
    public TestResultsSummary closeEyesBatch() {
        log.info("Thread ID [{}]<-- EYES BATCH closing", getThreadId());

        // Close the batch and report visual differences to the console.
        // Note that it forces TestNG to wait synchronously for all visual checkpoints to complete.
        TestResultsSummary allTestResults = runner().getAllTestResults();
        log.info("Thread ID [{}]<-- EYES BATCH closed", getThreadId());

        // No need to explicitly close the Runner
        //runner().close(batchName);
        
        return allTestResults;
    }
    
    /**
     * An example of what to do with the TestResultsSummary returned when closing the Eyes Batch.
     * 
     * @param allTestResults the Eyes TestResultsSummary object for a recently closed Batch
     */
    public void logAllTestResults(TestResultsSummary allTestResults) {
        log.info("Thread ID [{}]<-- EYES BATCH printing test results summary for batch '{}': {}", getThreadId(), batchName, allTestResults);
        allTestResults.forEach((c) -> {
            Throwable e = c.getException();
            if (e != null) {
                log.error("Exception encountered for {} : {}", c.getBrowserInfo(), e);
            }
            log.info("Found Result Container: {}", c);
            if (c != null) {
                log.info("    Result Container URL        : {}", c.getTestResults().getUrl());
                // Both of the following calls return the same value as TestResults.getUrl() above
                //log.info("    Result Container Batch URL  : {}", c.getTestResults().getAppUrls().getBatch());
                //log.info("    Result Container Session URL: {}", c.getTestResults().getAppUrls().getSession());
            }
        });
    }

}