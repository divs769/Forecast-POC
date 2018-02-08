package file
import com.amazonaws.services.cloudformation.model.Parameter
import spock.lang.Specification

class ECSStackConfigFileWriterTest extends Specification {


    public static final String SERVICE_NAME_KEY = 'ServiceName'
    public static final String SERVICE_NAME_VALUE = 'microservice-template'
    public static final String CONTAINER_PORT_KEY = 'ContainerPort'
    public static final String CONTAINER_PORT_VALUE = '8080'

    def "Determine whether array list parameters are written to ecs stack config file"() {

        given: "we have a array list with parameters"

        String configFileLocation = "/init/src/test/resources/service-stack-config-test.txt"
        ServiceStackConfigFileWriter ecsStackConfigFileWriter = new ServiceStackConfigFileWriter(getProjectRoot(), configFileLocation)

        List<Parameter> listOfParameters = Arrays.asList(
                new Parameter().withParameterKey(SERVICE_NAME_KEY).withParameterValue(SERVICE_NAME_VALUE),
                new Parameter().withParameterKey(CONTAINER_PORT_KEY).withParameterValue(CONTAINER_PORT_VALUE)
        )

        when: "We pass the array list into the writeNameAndVersionToFile method"

        ecsStackConfigFileWriter.write(listOfParameters)

        then: "the ecs stack config file is updated correctly"

        readInLinesOfConfigFile() ==
                SERVICE_NAME_KEY + "=" + SERVICE_NAME_VALUE + "\n" +
                CONTAINER_PORT_KEY + "=" + CONTAINER_PORT_VALUE
    }

    String readInLinesOfConfigFile() {

        File file = new File(getProjectRoot() +'/init/src/test/resources/service-stack-config-test.txt')
        file.getText("UTF-8").trim()
    }

    String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }
}