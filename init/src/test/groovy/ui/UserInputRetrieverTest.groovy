package ui

import spock.lang.Specification

class UserInputRetrieverTest extends Specification {

    UserInputHandler userInputHandler
    UserInputRetriever userInputRetriever

    def setup() {

        userInputHandler = Mock(UserInputHandler)
        userInputRetriever = new UserInputRetriever(userInputHandler)
    }

    def "Return user input values"(){

        given:"I have a key"

        and:"The user will enter the value for initialVersion"

        userInputHandler.promptForInput(prompt) >> userInput

        when:"I call the method"

        String returned = userInputRetriever.getUserInput(key)

        then: "I am returned the value I entered"

        returned == userInput

        where: "There are other key/value combinations"
        key                  | prompt                                    |  userInput
        "initialVersion"     | UserInputRetriever.INITIAL_VERSION_PROMPT |  "2.0.0"
        "teamAccountNumber"  | UserInputRetriever.TEAM_ACCOUNT_NUMBER    |  "669711333016"
        "jenkinsUsername"    | UserInputRetriever.JENKINS_USERNAME       |  "testJenkinsUsername"
    }

    def "Return custom values set in previous test"(){

        given:"I have a key"

        and:"The user will enter the value for initialVersion"

        userInputHandler.promptForInput(UserInputRetriever.JENKINS_USERNAME) >> "unitTest"

        when:"Custom values have already been set"

        String returned = userInputRetriever.getUserInput(key)

        then:"I am returned the custom values entered at during the previous test"

        returned == expected

        where:
        key                 |   expected
        "jenkinsUsername"   |   "unitTest"

    }

    def "Return default when no user input"(){

        given:"I have a key"

        and:"The user failed to enter a value"

        userInputHandler.promptForInput(prompt) >> ""

        when:"I call the method"

        def returned = userInputRetriever.getUserInput(key)

        then:"I am returned the default value"

        returned == expected

        where: "There are the following default values"
        key                     | prompt                                        | expected
        "teamAccountNumber"     | UserInputRetriever.TEAM_ACCOUNT_NUMBER        | "669711333016"
        "numberOfTasks"         | UserInputRetriever.NUMBER_OF_ECS_TASKS_PROMPT | "2"

    }

    def "Only ask user for input once for same key"(){

        given:"I have a key"

        def key = "jenkinsPublicIP"

        def userInput = "test.jenkins.publicIP"

        and:"The user will enter the value for initialVersion"

        when:"I call the method"

        String returned = userInputRetriever.getUserInput(key)
        String returned2 = userInputRetriever.getUserInput(key)

        then: "I am returned the value I entered"

        returned == userInput
        returned2 == userInput
        1 * userInputHandler.promptForInput(UserInputRetriever.JENKINS_PUBLIC_IP) >> userInput

    }

    def "When default field left blank, only ask for user input once when "(){

        given:"The key for a default field"

        def key = "sharedAccountNumber"

        and:"The user leaves the field blank"

        when:"I call the method"

        userInputRetriever.getUserInput(key)
        userInputRetriever.getUserInput(key)

        then:"The user is only prompted once"

        1 * userInputHandler.promptForInput(UserInputRetriever.SHARED_ACCOUNT_NUMBER) >> ""


    }

}