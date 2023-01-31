package com.applitools.eyes.selenium.cucumber.heroku.internet.steps;

import static com.applitools.eyes.selenium.introspection.Introspect.*;
import static org.testng.Assert.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.DropdownPage;
import com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi.HerokuInternetSite;
import com.applitools.eyes.selenium.managers.EyesManager;
import com.applitools.eyes.selenium.pageapi.PageTest;

public class DropdownSteps extends PageTest {
    private static final Logger log = LoggerFactory.getLogger(thisClass());
    
    private DropdownPage dropdownPage;
    //private EyesManager eyesManager;
    
    public DropdownSteps(HerokuInternetSite site, EyesManager eyesManager) {
        super(site);
        this.dropdownPage = new DropdownPage(site);
        //this.eyesManager = eyesManager;
    }
    
    @Given("I am on the Dropdown page")
    public void loadDropdownPage() {
        log.info("Thread ID [{}] loading Dropdown page", getThreadId());
        site.load(dropdownPage);
    }
    
    @When("I select the {string} dropdown item")
    public void selectDropdownItem(String itemText) {
        log.info("Thread ID [{}] selecting dropdown item'{}'", getThreadId(), itemText);
        dropdownPage.selectFromDropDown(itemText);
    }
    
    @Then("only one item should be selected")
    public void checkOnlyOneItemIsSelected() {
        log.info("Thread ID [{}] checking that only one dropdown item is selected", getThreadId());
        List<String> selectedItems = dropdownPage.getSelectedOptions();
        assertFalse(selectedItems.isEmpty(), "there is no dropdown item selected!");
        assertEquals(selectedItems.size(), 1, "more than one dropdown item is selected! " + selectedItems.toString());
    }
    
    @And("the selected item text should be {string}")
    public void checkSelectedItemText(String expectedText) {
        log.info("Thread ID [{}] checking for selected item text '{}'", getThreadId(), expectedText);
        String selectedText = dropdownPage.getSelectedOptions().stream().reduce("", String::concat);
        assertEquals(selectedText, expectedText, "the selected item text is not correct!");
    }
    
    @And("the selected item value should be {string}")
    public void checkSelectedItemValue(String expectedValue) {
        log.info("Thread ID [{}] checking for selected item value '{}'", getThreadId(), expectedValue);
        String selectedValue = dropdownPage.getSelectedValues().stream().findFirst().get();
        assertEquals(selectedValue, expectedValue, "the selected item value is not correct!");
    }
    
    /*@Then("check the {string} step with eyes")
    public void checkWithEyes(String step) {
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
        eyesManager.eyes().check(Target.window().withName(step));
        log.info("Thread ID [{}] checking the page for step '{}'", getThreadId(), step);
    }*/
    
}
