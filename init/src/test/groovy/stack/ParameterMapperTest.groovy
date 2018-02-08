package stack

import com.amazonaws.services.cloudformation.model.Parameter
import spock.lang.Specification

class ParameterMapperTest extends Specification {

    def "Maps VPC config to parameters"() {

        given: "VPC config with values"

        Map<String, String> vpcConfig = new HashMap<>()
        vpcConfig.put("PrivateSubnetOne", "PrivateSubnetOneValue")
        vpcConfig.put("PrivateSubnetTwo", "PrivateSubnetTwoValue")
        vpcConfig.put("PrivateSubnetThree", "PrivateSubnetThreeValue")
        vpcConfig.put("PublicSubnetOne", "PublicSubnetOneValue")
        vpcConfig.put("PublicSubnetTwo", "PublicSubnetTwoValue")
        vpcConfig.put("PublicSubnetThree", "PublicSubnetThreeValue")
        vpcConfig.put("Vpc", "VpcValue")

        when: "I map the config values to parameters"

        List<Parameter> parameters = new ParameterMapper().mapVPCConfig(vpcConfig)

        then: "I have a list of parameters populated from the config values"

        "PrivateSubnetOneValue,PrivateSubnetTwoValue,PrivateSubnetThreeValue" == getParameterValue(parameters, "PrivateSubnets")
        "PublicSubnetOneValue,PublicSubnetTwoValue,PublicSubnetThreeValue" == getParameterValue(parameters, "PublicSubnets")
        "VpcValue" == getParameterValue(parameters, "VPCName")
    }

    def "Maps file config to parameters not to include deployed environments"() {

        given: "File config lines with values"

        List<String> fileConfig = new ArrayList<>(Arrays.asList("KeyOne=ValueOne", "KeyTwo=ValueTwo", "DeployedEnvironments=a,b,c"))

        when: "I map the config values to parameters"

        List<Parameter> parameters = new ParameterMapper().mapFileConfig(fileConfig)

        then: "I have a list of parameters populated from the config values"

        "ValueOne" == getParameterValue(parameters, "KeyOne")
        "ValueTwo" == getParameterValue(parameters, "KeyTwo")

        and: "I do not have deployed environments parameter"

        null == getParameterValue(parameters, "DeployedEnvironments")
    }

    def "Maps file config to parameters verbatim"() {

        given: "File config lines with values"

        List<String> fileConfig = new ArrayList<>(Arrays.asList("KeyOne=ValueOne", "KeyTwo=ValueTwo", "DeployedEnvironments=a,b,c"))

        when: "I map the config values to parameters"

        List<Parameter> parameters = new ParameterMapper().mapFileConfig(fileConfig, true)

        then: "I have a list of parameters populated from the config values"

        "ValueOne" == getParameterValue(parameters, "KeyOne")
        "ValueTwo" == getParameterValue(parameters, "KeyTwo")
        "a,b,c" == getParameterValue(parameters, "DeployedEnvironments")
    }

    def "Maps environments to deploy"() {

        given: "File config lines with environment a b and c to deploy"

        List<String> fileConfig = new ArrayList<>(Arrays.asList("KeyOne=ValueOne", "DeployedEnvironments=a,b,c", "KeyTwo=ValueTwo"))

        when: "I map the config values to environments to deploy"

        List<String> environmentsToDeploy = new ParameterMapper().mapEnvironmentsToDeploy(fileConfig)

        then: "I have a list of parameters populated from the config values"

        true == containsString(environmentsToDeploy, "a")
        true == containsString(environmentsToDeploy, "b")
        true == containsString(environmentsToDeploy, "c")
    }

    private String getParameterValue(List<Parameter> parameters, parameterKey) {

        for (Parameter parameter : parameters) {
            if (parameter.getParameterKey() == parameterKey)
                return parameter.getParameterValue()
        }
    }

    private boolean containsString(List<String> strings, expectedString) {

        for (String string : strings) {
            if (string == expectedString)
                return true
        }

        return false
    }
}

