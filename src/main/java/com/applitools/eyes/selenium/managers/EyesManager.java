package com.applitools.eyes.selenium.managers;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.introspection.Introspect;
import com.applitools.eyes.selenium.settings.Settings;

public class EyesManager {
    private static final Logger log = LoggerFactory.getLogger(Introspect.thisClass());
    
    private final Settings settings = Settings.readSettings();
    
    private EyesBatchManager batchManager;
    private WebDriverManager driverManager;

    // An eyes object for each test
    private Eyes eyes;
    
    private String testName = "Eyes Test";

    public EyesManager(EyesBatchManager batchManager, WebDriverManager driverManager) {
        this.batchManager = batchManager;
        this.driverManager = driverManager;
    }

    public String testName() {
        return testName;
    }

    public void setTestName(String testName) {
        log.info("Thread ID [{}]--> EYES setting test name {}", getThreadId(), testName);
        this.testName = testName;
    }
    
    public Eyes eyes() {
        return eyes(testName);
    }
    
    public Eyes eyes(String testName) {
        if (eyes == null) {
            eyes = openEyes(testName);
        }
        return eyes;
    }

    public Eyes openEyes() {
        return openEyes(testName);
    }
    
    public Eyes openEyes(String testName) {
        // Create the Applitools Eyes object connected to the Runner and set its configuration.
        eyes = new Eyes(batchManager.runner());
        eyes.setConfiguration(batchManager.config());
        
        if (settings.baselineEnvironment.isNotNull()) {
            eyes.setBaselineEnvName(settings.baselineEnvironment.get());
        }
        
        if (settings.isDisabled) {
            eyes.setIsDisabled(true);
            log.debug("Thread ID [{}]--> EYES \n\n\t--------> ALL APPLITOOLS EYES API CALLS ARE DISABLED!!! <-------- {}\n", getThreadId(), settings.isDisabled);
        }
        
        // If you're using Eyes Selenium Java3, you can use one of the following calls to
        // get log messages from the Eyes SDK.
        //
        // This one will log messages to a specific file.
        //eyes.setLogHandler(new FileLogger("/dev/null", true, true));
        //
        // This one will log messages to the console via STDOUT.
        //eyes.setLogHandler(new StdoutLogHandler(true));
        //
        // If you're using Eyes Selenium Java5, the setLogHandler method calls don't do anything
        // and the Eyes Universal SDK client logs to a separate file in your temp directory.
        // In that case, you should find the path to those log files in your test's execution log,
        // or you can look for them yourself in $TMPDIR/applitools-logs/ or in a Windows context
        // in $env:TEMP/applitools-logs/
        
        
        // Open Eyes to start visual testing.
        // It is a recommended practice to set all four inputs:
        log.info("Thread ID [{}]--> EYES opening app named '{}' and test case '{}'", getThreadId(), batchManager.appName(), testName);
        eyes.open(
                driverManager.driver(),       // WebDriver object to "watch"
                batchManager.appName(),       // The name of the app under test
                testName,                     // The name of the test case
                batchManager.viewportSize()); // The viewport size for the local browser
        
        log.info("Thread ID [{}]--> EYES opened", getThreadId());

        return eyes;
    }
    
    public void closeEyes() {
        // Close Eyes to tell the server that this test is complete
        log.info("Thread ID [{}]<-- EYES closing", getThreadId());
        eyes.closeAsync();

        /* Warning: `eyes.closeAsync()` will NOT wait for visual checkpoints comparisons
         * to complete. You will need to check the Applitools dashboard for visual results
         * per checkpoint. Note that "unresolved" and "failed" visual checkpoints will not
         * cause the test to fail. If you want the test to wait synchronously for all
         * checkpoints to complete, then use `eyes.close()`.
         * 
         * If any checkpoints are unresolved or failed, then `eyes.close()` will make the
         * test fail.
         */
        //eyes.close();
        log.info("Thread ID [{}]<-- EYES closed", getThreadId());
    }


}
