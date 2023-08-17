# Form Authentication Page Cucumber Scenario Outlines

@eyes @web
Feature: Fannie Mae Web Site Searches

  In order to validate web site search results
  I want to search for different words
  On the Fannie Mae web site
  
  @eyes
  Scenario Outline: Fannie Mae Web Search for "<searchTerm>"
    Given I am on the Fannie Mae main page
    And I accept all cookies
    And check the full "Main page" page with eyes
    When I enter the search term "<searchTerm>"
    And I submit the search
    Then I should be on the Search Results page
    And eyes should see search results
    
    Examples:
      | searchTerm        |
      | mortgage rates    |
      | disaster relief   |
      | economic forecast |
      