package com.applitools.eyes.selenium.cucumber.heroku.internet.steps.pageapi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage extends BasePage {
    
    private static final String RELATIVE_URL = "/dropdown";
    
    By dropdown = By.id("dropdown");
    
    public DropdownPage() {
        super(RELATIVE_URL);
    }
    
    public DropdownPage(HerokuInternetSite site) {
        super(RELATIVE_URL, site);
    }
    
    public void selectFromDropDown(String option) {
        Optional<Select> dropdownElement = findSelect(dropdown);
        dropdownElement.ifPresent(e -> e.selectByVisibleText(option));
    }
    
    public List<String> getSelectedOptions() {
        Optional<Select> dropdownElement = findSelect(dropdown);
        return dropdownElement.map(e -> e.getAllSelectedOptions())
                .orElse(Collections.emptyList()).stream()
                .map(e -> e.getText()).collect(Collectors.toList());
    }
    
    public List<String> getSelectedValues() {
        Optional<Select> dropdownElement = findSelect(dropdown);
        return dropdownElement.map(e -> e.getAllSelectedOptions())
                .orElse(Collections.emptyList()).stream()
                .map(e -> e.getAttribute("value")).collect(Collectors.toList());
    }

}
