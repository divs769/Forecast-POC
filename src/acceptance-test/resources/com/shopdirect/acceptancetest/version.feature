Feature: Version endpoint is up
  Visiting the Version endpoint we are returned the current application Version.
  Scenario:
    When the Version endpoint is called
    Then the application's current Version is returned