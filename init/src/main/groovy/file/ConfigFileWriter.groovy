package file

import task.Task

class ConfigFileWriter implements Task {

    private String projectRoot
    private String configFileLocation
    private ConfigFileFormatter configFileFormatter
    private String serviceName
    private String initialVersion
    private String teamAccountNumber
    private String sharedAccountNumber
    private String numberOfECSTasks

    ConfigFileWriter(projectRoot, configFileLocation, configFileFormatter, serviceName, initialVersion, teamAccountNumber, sharedAccountNumber, numberOfECSTasks) {
        this.projectRoot = projectRoot
        this.configFileLocation = configFileLocation
        this.configFileFormatter = configFileFormatter
        this.serviceName = serviceName
        this.initialVersion = initialVersion
        this.teamAccountNumber = teamAccountNumber
        this.sharedAccountNumber = sharedAccountNumber
        this.numberOfECSTasks = numberOfECSTasks
    }

    boolean run(){
        writeNameAndVersionToFile(serviceName, initialVersion,teamAccountNumber, sharedAccountNumber, numberOfECSTasks)
        println "ConfigFileWriter successful!\n"
        true
    }

    def writeNameAndVersionToFile(String serviceName, String initialVersion, String teamAccountNumber, String sharedAccountNumber,
                                  String numberOfECSTasks) {

        File sdgConfigFile = new File(projectRoot + configFileLocation)
        sdgConfigFile.write(configFileFormatter.format(serviceName, initialVersion, teamAccountNumber, sharedAccountNumber, numberOfECSTasks))
    }
}
