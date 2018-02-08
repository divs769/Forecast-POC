Feature: Health check
  Should be able to access the Health endpoint and receive a response saying ok
  Scenario: Check service is running
    When I visit the health resource
    Then I get a 'OK' response
    And the health status is 'UP'