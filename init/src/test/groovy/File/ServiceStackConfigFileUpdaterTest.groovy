import file.FileConfigReader
import spock.lang.Specification
import com.amazonaws.services.cloudformation.model.Parameter
import stack.ParameterMapper
import file.ServiceStackConfigFileUpdater

class ServiceStackConfigFileUpdaterTest extends Specification {


    public static final String ORIGINAL_SERVICE_ROLE = 'ecsServiceRole'
    public static final String ORIGINAL_SERVICE_NAME = 'microservice-template'
    public static final String ORIGINAL_NUMBER_OF_TASKS = '2'
    public static final String ORIGINAL_DOCKER_IMAGE = '669711333016.dkr.ecr.eu-west-1.amazonaws.com/demo:latest'
    public static final String NEW_SERVICE_NAME = "my-service"
    public static final String NEW_NUMBER_OF_TASKS = '4'
    public static final String NEW_SHARED_ACCOUNT_NUMBER = '5678'

    ParameterMapper parameterMapper
    FileConfigReader fileConfigReader
    ServiceStackConfigFileUpdater serviceStackConfigFileUpdater

    void setup() {
        parameterMapper = Stub(ParameterMapper)
        fileConfigReader = Stub(FileConfigReader)
        serviceStackConfigFileUpdater = new ServiceStackConfigFileUpdater(parameterMapper, fileConfigReader)
    }

    def "Determine whether variable ecs stack config values are added to array list parameters"() {

        given: "we have four stack variable values"

        List<Parameter> parameters = Arrays.asList(
                new Parameter().withParameterKey('ServiceName').withParameterValue(ORIGINAL_SERVICE_NAME),
                new Parameter().withParameterKey('NumberOfTasks').withParameterValue(ORIGINAL_NUMBER_OF_TASKS),
                new Parameter().withParameterKey('DockerImage').withParameterValue(ORIGINAL_DOCKER_IMAGE),
                new Parameter().withParameterKey('ServiceRole').withParameterValue(ORIGINAL_SERVICE_ROLE)
        )

        def lines = new ArrayList()
        fileConfigReader.getConfig() >> lines
        parameterMapper.mapFileConfig(lines) >> parameters

        when: "we call updateListArray with these values"

        List<Parameter> newParameters = serviceStackConfigFileUpdater.update(NEW_SERVICE_NAME, NEW_SHARED_ACCOUNT_NUMBER, NEW_NUMBER_OF_TASKS)

        then: "variable stack values are updated"

        getParameterValue(newParameters, "ServiceName") == NEW_SERVICE_NAME
        getParameterValue(newParameters, "NumberOfTasks") == NEW_NUMBER_OF_TASKS
        getParameterValue(newParameters, "DockerImage") == NEW_SHARED_ACCOUNT_NUMBER + ".dkr.ecr.eu-west-1.amazonaws.com/demo:latest"

        and: "static stack values are not updated"

        getParameterValue(newParameters, "ServiceRole") == ORIGINAL_SERVICE_ROLE
    }

    private String getParameterValue(List<Parameter> parameters, parameterKey) {

        for (Parameter parameter : parameters) {
            if (parameter.getParameterKey() == parameterKey)
                return parameter.getParameterValue()
        }
    }

}

