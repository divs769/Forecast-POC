package ui

class UserInputRetriever {

    public static final String INITIAL_VERSION_PROMPT =
            "\nPlease input a desired version number in the following format - MAJOR.MINOR.PATCH (e.g. 1.0.0):"

    public static final String TEAM_ACCOUNT_NUMBER =
            "\nPlease input your team account number (press enter to default to 669711333016):"

    public static final String SHARED_ACCOUNT_NUMBER =
            "\nPlease input your shared account number (press enter to default to 669711333016):"

    public static final String NUMBER_OF_ECS_TASKS_PROMPT =
            "\nPlease input the desired number of ECS tasks you would like to run for this service (minimum 2, press enter to default to 2):"

    public static final String JENKINS_PUBLIC_IP =
            "\nPlease input the public IP of the Jenkins box you would like to register with (press enter to default to jenkins.ignite.lsdg.com)"

    public static final String JENKINS_USERNAME =
            "\nPlease input your Jenkins username: "

    public static final String JENKINS_API_CREDENTIALS =
            "\nPlease input your Jenkins API key: "

    private promptMap =    ["initialVersion":INITIAL_VERSION_PROMPT,
                            "teamAccountNumber":TEAM_ACCOUNT_NUMBER,
                            "sharedAccountNumber":SHARED_ACCOUNT_NUMBER,
                            "numberOfTasks": NUMBER_OF_ECS_TASKS_PROMPT,
                            "jenkinsPublicIP": JENKINS_PUBLIC_IP,
                            "jenkinsUsername":JENKINS_USERNAME,
                            "jenkinsApiCredentials":JENKINS_API_CREDENTIALS]


    private defaultMap   = ["teamAccountNumber"  : "669711333016",
                            "sharedAccountNumber": "669711333016",
                            "numberOfTasks"      : "2",
                            "jenkinsPublicIP"    : "jenkins.ignite.lsdg.com"]

    private customMap = [:]


    private UserInputHandler userInputHandler

    UserInputRetriever(UserInputHandler userInputHandler) {
        this.userInputHandler = userInputHandler
    }

    def getUserInput(String key) {


        if (getCustomValue(key) != null) {
            return getCustomValue(key)
        }

        String response = userInputHandler.promptForInput(promptMap[key])

        if ((response == '') & (getDefaultValue(key) != null)) {
            setCustomValue(key, getDefaultValue(key))
            return getDefaultValue(key)

        } else {
            setCustomValue(key, response)
            return response
        }

    }

    private String getDefaultValue(String key) {
        return defaultMap.get(key)
    }

    private String setCustomValue(String key, String response) {
        customMap.put(key,response)
        return response
    }

    private String getCustomValue(String key) {
        return customMap.get(key)
    }

}