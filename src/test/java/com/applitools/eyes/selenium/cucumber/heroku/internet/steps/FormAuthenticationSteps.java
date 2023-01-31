package com.applitools.eyes.selenium.cucumber.heroku.internet.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import static org.testng.Assert.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.FormAuthenticationPage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HerokuInternetSite;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.SecureAreaPage;
import com.applitools.eyes.selenium.pageapi.PageTest;

public class FormAuthenticationSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(FormAuthenticationSteps.class.getName());
    
    private FormAuthenticationPage formAuthPage;
    private SecureAreaPage secureAreaPage;
    
    public FormAuthenticationSteps(HerokuInternetSite site) {
        super(site);
        this.formAuthPage = new FormAuthenticationPage(site);
        this.secureAreaPage = new SecureAreaPage(site);
    }
    
    @Given("I am on the Form Authentication page")
    public void loadFormAuthenticationPage() {
        log.info("Thread ID [{}] loading Form Authentication page", getThreadId());
        site.load(formAuthPage);
    }
    
    @Given("I have logged in as user {string} with password {string}")
    public void loginToSecureArea(String username, String password) {
        log.info("Thread ID [{}] logging in to secure area with username '{}' and password '{}'", getThreadId(), username, password);
        site.load(formAuthPage);
        checkFormAuthenticationPageIsLoaded();
        formAuthPage.setUsername(username);
        formAuthPage.setPassword(password);
        formAuthPage.clickLoginButton();
        assertTrue(secureAreaPage.hasSuccessAlert(), "Login failed! alert message: " +  secureAreaPage.alertText());
    }
    
    @And("I am on the Secure Area page")
    public void checkSecureAreaPageIsLoaded() {
        log.info("Thread ID [{}] checking that the secure area page is loaded", getThreadId());
        assertTrue(site.hasLoaded(secureAreaPage), "The browser has not loaded the Secure Area page!");
        assertEquals(secureAreaPage.getTitle(), SecureAreaPage.TITLE_TEXT, "Incorrect page title!");
    }
    
    @And("I should be on the Form Authentication page")
    public void checkFormAuthenticationPageIsLoaded() {
        log.info("Thread ID [{}] checking that the form authentication page is loaded", getThreadId());
        assertTrue(site.hasLoaded(formAuthPage), "The browser has not loaded the Form Authentication page!");
        assertEquals(formAuthPage.getTitle(), FormAuthenticationPage.TITLE_TEXT, "Incorrect page title!");
    }
    
    @When("I enter the username {string}")
    public void enterUsername(String username) {
        log.info("Thread ID [{}] entering username '{}'", getThreadId(), username);
        formAuthPage.setUsername(username);
    }
    
    @And("I enter the password {string}")
    public void enterPassword(String password) {
        log.info("Thread ID [{}] entering password '{}'", getThreadId(), password);
        formAuthPage.setPassword(password);
    }
    
    @And("I click the login button")
    public void clickLogin() {
        log.info("Thread ID [{}] clicking the login button", getThreadId());
        formAuthPage.clickLoginButton();
    }
    
    @And("I click the logout button")
    public void clickLogout() {
        log.info("Thread ID [{}] clicking the logout button", getThreadId());
        secureAreaPage.clickLogoutButton();
    }
    
}
