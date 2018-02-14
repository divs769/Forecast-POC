Feature: Get forecastings

    Background:
        Given the database has been initialised and is running
        And multiple stock values have been inserted

    Scenario: The request has a positive number of weeks
        Given the endpoint is called with a number of weeks greater than 0 passed as input
        Then the response is success
        And the result contains the historic data
        And the result contains one prediction for each model
        And the result contains the forecasted values for the best model first
        And the result contains the correct predictions for the naive model
        And the result contains the correct predictions for the average model