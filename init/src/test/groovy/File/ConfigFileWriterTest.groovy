package File

import spock.lang.Specification
import file.ConfigFileWriter
import file.ConfigFileFormatter

class ConfigFileWriterTest extends Specification {

    def "ConfigFileWriter task carried out successfully"(){

        given: "I have a task to run"

        ConfigFileWriter configFileWriter = new ConfigFileWriter(getProjectRoot(),'/init/src/test/resources/service-config-test.txt', new ConfigFileFormatter(),
                'ConfigFileWriterUnitTest', '1.0.0', '123456789012', '123456789012', '3' )

        when: "I run the task"

        boolean returnValue = configFileWriter.run()

        then: "Parameters are written to file and a success message is returned"

        testConfigFileWriter() == true
        returnValue == true

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    boolean testConfigFileWriter(){
        def file = new File(getProjectRoot() + '/init/src/test/resources/service-config-test.txt')
        String returned = file.readLines().toString() == "[ServiceName=UnitTest, ServiceVersion=1.0.0, TeamAccountNumber=123456789012, SharedAccountNumber=123456789012, NumberOfEcsTasks=2]"
        file.delete()
        returned
    }

}