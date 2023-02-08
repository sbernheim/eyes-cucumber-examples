package com.applitools.eyes.selenium.cucumber.runner;

import org.testng.annotations.DataProvider;


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
        "classpath:examples"    // Defaults to this package if left blank or blank string.
    },
    glue = {    // A comma-delimited list of packages containing step definitions.
        "com.applitools.eyes.selenium.cucumber.hooks",
        "com.applitools.eyes.selenium.cucumber.examples"
    },
    //tags = "@all", // A tag expression for the tests this runner will run.
    plugin = {  // A comma-delimited list of Cucumber plug-ins.
        //"html:target/cucumber.html",    // html, json and pretty are built in report formatters
        //"json:target/cucumber.json",
        "pretty" 
    }
)
public class RunExampleTests extends AbstractTestNGCucumberTests {
    private static final Logger log = LoggerFactory.getLogger(RunExampleTests.class.getName());
    
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        Object[][] scenarios = super.scenarios();
        log.info("TestNGCucumberRunner will launch {} Cucumber Scenarios!", scenarios.length);
        return scenarios;
    }

}
