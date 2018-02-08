package file

import com.amazonaws.services.cloudformation.model.Parameter
import stack.ParameterMapper

class ServiceStackConfigFileUpdater {

    public static final String DEFAULT_SHARED_ACCOUNT_NUMBER = "669711333016"

    private ParameterMapper parameterMapper
    private FileConfigReader fileConfigReader

    ServiceStackConfigFileUpdater(parameterMapper, fileConfigReader){
        this.parameterMapper = parameterMapper
        this.fileConfigReader = fileConfigReader
    }

    def update(String serviceName, String sharedAccountNumber, String numberOfTasks)
    {
        def lines = fileConfigReader.getConfig()
        def parameters = parameterMapper.mapFileConfig(lines, true)

        for (Parameter parameter : parameters) {
            if (parameter.getParameterKey() == "ServiceName") {
                parameter.setParameterValue(serviceName)
            }
            if (parameter.getParameterKey() == "DockerImage") {
                String newDockerImage = parameter.getParameterValue().replace(DEFAULT_SHARED_ACCOUNT_NUMBER, sharedAccountNumber)
                parameter.setParameterValue(newDockerImage)
            }
            if (parameter.getParameterKey() == "NumberOfTasks") {
                parameter.setParameterValue(numberOfTasks)
            }
        }

        return parameters
    }

}