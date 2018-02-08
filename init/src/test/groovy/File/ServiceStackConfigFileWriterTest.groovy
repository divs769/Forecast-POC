package File

import com.amazonaws.services.cloudformation.model.Parameter
import file.ServiceStackConfigFileWriter
import spock.lang.Specification

class ServiceStackConfigFileWriterTest extends Specification {

    def "ServiceStackConfigFileWriter task carried out successfully"(){

        given: "I have a task to run"

        ServiceStackConfigFileWriter serviceStackConfigFileWriter = new ServiceStackConfigFileWriter(getProjectRoot()
                ,'/init/src/test/resources/service-stack-config-test.txt', Arrays.asList(new Parameter()
                .withParameterKey('test').withParameterValue('parameters')))

        when: "I run the task"

        boolean returnValue = serviceStackConfigFileWriter.run()

        then: "Parameters written to file, and success message returned"

        testServiceStackConfigFileWriter() == true
        returnValue == true

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    boolean testServiceStackConfigFileWriter(){
        def file = new File(getProjectRoot() + '/init/src/test/resources/service-stack-config-test.txt')
        String returned = file.readLines().toString() == "[test=parameters]"
        file.delete()
        returned
    }

}