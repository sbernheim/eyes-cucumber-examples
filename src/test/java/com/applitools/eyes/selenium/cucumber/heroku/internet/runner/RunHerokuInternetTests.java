package com.applitools.eyes.selenium.cucumber.heroku.internet.runner;

import static com.applitools.eyes.selenium.introspection.Introspect.*;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.managers.EyesBatchManager;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a Cucumber TestNG Runner class.  The Maven Surefire or Failsafe plugin runs
 * this class as a TestNG test, and this test will then run your Cucumber Scenarios.
 * 
 * In order for the Maven Surefile build plugin to recognize this class as a test, its
 * file name must match one of the automatically included wildcard patterns. For this
 * example, the test class file name matches the "**\/*Tests.java" pattern.
 * See: https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
 * 
 * In order for the Maven Failsafe plugin to recognize this class as a test, the class 
 * file name must instead match one of its automatically includes wildcard patterns. To
 * run this test with Failsafe, change the class and file name to match the "**\/IT.java"
 * pattern.
 * See: https://maven.apache.org/surefire/maven-failsafe-plugin/examples/inclusion-exclusion.html#inclusions
 * 
 * @author sebastian
 *
 */
@CucumberOptions(
    features = {                // A comma-delimited list of paths to feature file dirs.
        "classpath:heroku-internet"    // Defaults to this package if left blank or blank string.
    },
    glue = {    // A comma-delimited list of packages containing step definitions.
        "com.applitools.eyes.selenium.cucumber.hooks",
        "com.applitools.eyes.selenium.cucumber.heroku.internet.steps"
    },
    tags = "@web", // A tag expression for the tests this runner will run.
    plugin = {  // A comma-delimited list of Cucumber plug-ins.
        //"html:target/cucumber.html",    // html, json and pretty are built in report formatters
        //"json:target/cucumber.json",
        "pretty" 
    }
)
public class RunHerokuInternetTests extends AbstractTestNGCucumberTests {
    private static final Logger log = LoggerFactory.getLogger(RunHerokuInternetTests.class.getName());
    
    private EyesBatchManager batchManager;
    
    public RunHerokuInternetTests() {
        this.batchManager = new EyesBatchManager();
    }
    
    @BeforeSuite
    public void openEyesBatch() {
        batchManager.setAppName("Heroku Internet Site");
        batchManager.setBatchName("Heroku Internet Site Test Batch");
        batchManager.createBatchInfo();
        log.info("Thread ID [{}] Eyes BatchID {} OPENING", getThreadId(), batchManager.batchId());
        batchManager.openEyesBatch();
        log.info("Thread ID [{}] Eyes BatchID {} OPEN", getThreadId(), batchManager.batchId());
    }
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        Object[][] scenarios = super.scenarios();
        log.info("Thread ID [{}] Eyes BatchID {} TestNGCucumberRunner will launch {} Cucumber Scenarios!", getThreadId(), batchManager.batchId(), scenarios.length);
        return scenarios;
    }
    
    @AfterSuite
    public void closeEyesBatch() {
        log.info("Thread ID [{}] Eyes BatchID {} CLOSING", getThreadId(), batchManager.batchId());
        TestResultsSummary results = batchManager.closeEyesBatch();
        log.info("Thread ID [{}] Eyes BatchID {} CLOSED", getThreadId(), batchManager.batchId());
        batchManager.logAllTestResults(results);
    }

}
