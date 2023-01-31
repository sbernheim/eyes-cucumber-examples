@hovers @web
Feature: Heroku Internet Hovers Page

  In order to demonstrate mouse hover functions
  I want to hover over an item
  To display text and a link
  On the Heroku Internet site Hovers page
  
  @eyes
  Scenario Outline: Hover over item <index> to display information about "<user>"
    Given I am on the Hovers page
    When I move my mouse over item <index>
    Then the item <index> caption should be displayed
    And the caption title should be "<title>"
    And the caption profile link text should be "<link_text>"
    And the caption profile link URL should be "<link_url>"
    And check the "Hovering" step with eyes
    
    Examples:
      | index | user  | title       | link_text    | link_url |
      | 1     | user1 | name: user1 | View profile | /users/1 |
      | 2     | user2 | name: user2 | View profile | /users/2 |
      | 3     | user3 | name: user3 | View profile | /users/3 |
      
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
