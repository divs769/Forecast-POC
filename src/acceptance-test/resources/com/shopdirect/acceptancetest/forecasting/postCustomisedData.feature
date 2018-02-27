Feature: Save customised data

  Background:
    Given there is no customised model inserted for hierarchy "lineNumber" and value "8M418"

  Scenario: Customised data is posted
    Given a payload with customised data for hierarchy "lineNumber" and value "8M418"
    When the post customised data endpoint is called
    Then the response is success
    And new customisation is inserted
    And the id of the new customisation is returned