package file

import com.amazonaws.services.cloudformation.model.Parameter
import task.Task

class ServiceStackConfigFileWriter implements Task {

    private String projectRoot
    private String configFileLocation
    private List parameters

    ServiceStackConfigFileWriter(projectRoot, configFileLocation, parameters) {
        this.projectRoot = projectRoot
        this.configFileLocation = configFileLocation
        this.parameters = parameters
    }

    boolean run(){
        write(parameters)
        println "\nServiceStackConfigFileWriter successful!\n"
        true
    }

    def write(List<Parameter> parameters){
        File sdgConfigFile = new File(projectRoot + configFileLocation)

               StringBuilder str = new StringBuilder()
               for(parameter in parameters) {
                   str.append(parameter.getParameterKey() +"="+ parameter.getParameterValue() + '\n')
               }

        sdgConfigFile.write(str.toString())

        }
}
