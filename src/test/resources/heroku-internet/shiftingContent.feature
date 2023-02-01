# Shifting Content Pages Cucumber Scenario Outlines

@shifting @web
Feature: Heroku Internet Shifting Content Pages

  In order to demonstrate shifting and changing content
  I want to check the Heroku Internet Shifting Content pages
  
  @eyes @shiftingtext
  Scenario: List items with dynamic text
    Given I am on the Shifting List Content page
    Then check the "Dynamic List Text" step with eyes
    
  @eyes @shiftingimage
  Scenario: Shifting image
    Given I am on the Shifting Image page
    Then check the "Shifting Image" step with eyes
    
  @eyes @shiftingmenus
  Scenario Outline: Hover mouse over shifting menu item <item>
    Given I am on the Shifting Menu Elements page
    And check the "Before" step with eyes
    When I hover over menu item <item>
    Then there should be 5 menu items
    And check the "After" step with eyes
    
    Examples:
    | item |
    | 1    |
    | 2    |
    | 3    |
    | 4    |
    | 5    |