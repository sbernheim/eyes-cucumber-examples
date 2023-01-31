# Form Authentication Page Cucumber Scenario Outlines

@formauth @web
Feature: Heroku Internet Form Authentication Page

  In order to demonstrate form authentication login flow
  I want to log in with valid and invalid usernames and passwords
  On the Heroku Internet Form Authentication page
  With an example of logging out after a successful login
  
  @eyes @fail
  Scenario Outline: Login failure with invalid username "<username>"
    Given I am on the Form Authentication page
    When I enter the username "<username>"
    And I enter the password "<password>"
    And check the "Before" step with eyes
    And I click the login button
    Then I should see an error alert
    And the error alert should say "Your username is invalid!"
    And check the "After" step with eyes
    
    Examples:
      | username | password             |
      | timsmith | SuperSecretPassword! |
      | joeblow  | SuperSecretPassword! |
      
  @eyes @fail
  Scenario Outline: Login failure with invalid password "<password>"
    Given I am on the Form Authentication page
    When I enter the username "<username>"
    And I enter the password "<password>"
    And check the "Before" step with eyes
    And I click the login button
    Then I should see an error alert
    And the error alert should say "Your password is invalid!"
    And check the "After" step with eyes
    
    Examples:
      | username | password             |
      | tomsmith | SuperInv@l1dPa55w0rd |
      | tomsmith | .                    |
      
  @eyes @success
  Scenario: Login success with valid username "tomsmith" and password "SuperSecretPassword!"
    Given I am on the Form Authentication page
    When I enter the username "tomsmith"
    And I enter the password "SuperSecretPassword!"
    And check the "Before" step with eyes
    And I click the login button
    Then I should see a success alert
    And the success alert should say "You logged into a secure area!"
    And check the "After" step with eyes
    
  @eyes @logout
  Scenario: Logging out after a successful login
    Given I have logged in as user "tomsmith" with password "SuperSecretPassword!"
    And I am on the Secure Area page
    And check the "Before" step with eyes
    When I click the logout button
    Then I should see a success alert
    And the success alert should say "You logged out of the secure area!"
    And I should be on the Form Authentication page
    And check the "After" step with eyes
