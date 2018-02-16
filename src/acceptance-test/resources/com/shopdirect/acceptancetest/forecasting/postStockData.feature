Feature: Save historic stock data

  Background:
    Given the database has been initialised and is running

  Scenario: Stock data is posted
    Given a payload with stock data
    When the post endpoint is called
    Then the response is success
    And the database contains the new value