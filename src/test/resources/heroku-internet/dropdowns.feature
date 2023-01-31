@dropdowns @web
Feature: Heroku Internet Dropdown Menus Page

  In order to demonstrate dropdown list selection
  I want to select dropdown list items
  On the Heroku Internet site Dropdown page
  
  @eyes
  Scenario Outline: Select the "<label>" dropdown menu option
    Given I am on the Dropdown page
    When I select the "<label>" dropdown item
    Then only one item should be selected
    And the selected item text should be "<label>"
    And the selected item value should be "<value>"
    And check the "Selected" step with eyes
    
    Examples:
      | label    | value |
      | Option 1 | 1     |
      | Option 2 | 2     |
      
#  @eyes
#  Scenario: Web Eyes Scenario
#    Given an example precondition
#    
#  @tag1
#  Scenario: Title of your scenario
#    Given I want to write a step with precondition
#    And some other precondition
#    When I complete action
#    And some other action
#    And yet another action
#    Then I validate the outcomes
#    And check more outcomes
#
#  @tag2
#  Scenario Outline: Title of your scenario outline
#    Given I want to write a step with <name>
#    When I check for the <value> in step
#    Then I verify the <status> in step
#
#    Examples: 
#      | name  | value | status  |
#      | name1 |     5 | success |
#      | name2 |     7 | Fail    |
