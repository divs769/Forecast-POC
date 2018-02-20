Feature: Get forecastings

    Background:
        Given the database has been initialised and is running
        And multiple stock values have been inserted

    Scenario: The request has a positive number of weeks
        Given a payload with 4 weeks and "8M417" as line number number
        When the get endpoint is called
        Then the response is success
        And the result contains the historic data
        And the result contains one prediction for each model
        And the result contains the forecasted values for the best model first
        And the result contains the correct predictions for the naive model
        And the result contains the correct predictions for the average model

    Scenario: The response returns only values for the used line number
        Given the database has rows with "8M418" as line number
        And a payload with 4 weeks and "8M418" as line number number
        When the get endpoint is called
        Then the response is success
        And the result contains the historic data
        And the result contains only predictions with "8M418" as line number

    Scenario: The request has no item with the given line number
        Given a payload with 4 weeks and "8M416" as line number number
        When the get endpoint is called
        Then the response is success
        And the historic data and forecastings are empty