package stack

import com.amazonaws.services.cloudformation.model.Parameter

class ParameterMapper {

    def mapVPCConfig(Map<String, String> vpcConfig) {

        List<Parameter> newParameters = Arrays.asList(
                new Parameter()
                        .withParameterKey('PrivateSubnets')
                        .withParameterValue(vpcConfig['PrivateSubnetOne'] + ',' + vpcConfig['PrivateSubnetTwo'] + ',' + vpcConfig['PrivateSubnetThree']),
                new Parameter()
                        .withParameterKey('PublicSubnets')
                        .withParameterValue(vpcConfig['PublicSubnetOne'] + ',' + vpcConfig['PublicSubnetTwo'] + ',' + vpcConfig['PublicSubnetThree']),
                new Parameter()
                        .withParameterKey('VPCName')
                        .withParameterValue(vpcConfig['Vpc'])
        )

        newParameters
    }

    List<Parameter> mapFileConfig(List<String> fileConfig, boolean verbatim=false) {

        List<Parameter> parameters = new ArrayList<>()

        fileConfig.each { line ->

            def split = line.split("=")

            String key = split[0]
            String value = split[1]

            if (key != "DeployedEnvironments" || verbatim) {
                parameters.add(new Parameter().withParameterKey(key).withParameterValue(value))
            }
        }

        parameters
    }

    List<String> mapEnvironmentsToDeploy(List<String> fileConfig) {

        for (String fleConfigLine : fileConfig) {
            def split = fleConfigLine.split("=")
            if ((split[0]) == "DeployedEnvironments") {
                List<String> environments = split[1].split(",")
                return environments
            }
        }
    }
}