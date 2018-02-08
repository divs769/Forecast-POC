Feature: Swagger-UI is up
  The user should be able to access the Swagger-UI and view existing endpoints.

  Scenario: Is Swagger-UI up and displaying endpoints?
  When I call the Swagger-UI API
  Then I am returned a title of "Swagger UI"