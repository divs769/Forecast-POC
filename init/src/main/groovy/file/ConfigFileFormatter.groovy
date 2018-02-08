package file

class ConfigFileFormatter {

    public static final String SERVICE_NAME_KEY = "ServiceName="
    public static final String VERSION_KEY = "ServiceVersion="
    public static final String TEAM_ACCOUNT_NUMBER = "TeamAccountNumber="
    public static final String SHARED_ACCOUNT_NUMBER = "SharedAccountNumber="
    public static final String NUMBER_OF_ECS_TASKS_KEY = "NumberOfEcsTasks="

    private format(String serviceName, String initialVersion, String teamAccountNumber, String sharedAccountNumber, String numberOfECSTasks) {
        String serviceNameLine = SERVICE_NAME_KEY + serviceName
        String initialVersionLine = VERSION_KEY + initialVersion
        String teamAccountNumberLine = TEAM_ACCOUNT_NUMBER + teamAccountNumber
        String sharedAccountNumberLine = SHARED_ACCOUNT_NUMBER + sharedAccountNumber
        String numberOfECSTasksLine = NUMBER_OF_ECS_TASKS_KEY + numberOfECSTasks

        serviceNameLine + '\n' + initialVersionLine + '\n' + teamAccountNumberLine + '\n' + sharedAccountNumberLine + '\n' + numberOfECSTasksLine
    }
}