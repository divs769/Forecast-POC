package vpc

import spock.lang.Specification

class VPCConfigParserTest extends Specification {

    public static final String CONFIG_KEY_PUBLIC_SUBNET_TWO = "PublicSubnetTwo"
    public static final String CONFIG_VALUE_PUBLIC_SUBNET_TWO = "subnet-b91975de"
    public static final String CONFIG_KEY_VPC = "Vpc"
    public static final String CONFIG_VALUE_VPC = "vpc-51235536"

    def "Parses config string into map"() {

        given: "A config string with two values"

        String configString = CONFIG_KEY_PUBLIC_SUBNET_TWO + "=" + CONFIG_VALUE_PUBLIC_SUBNET_TWO + "\n" +
                CONFIG_KEY_VPC + "=" + CONFIG_VALUE_VPC + "\n"

        when: "The string is parsed"
        Map<String, String> configMap = new VPCConfigParser().parse(configString)

        then: "A map is created containing the two values"

        configMap.size() == 2
        configMap.get(CONFIG_KEY_PUBLIC_SUBNET_TWO) == CONFIG_VALUE_PUBLIC_SUBNET_TWO
        configMap.get(CONFIG_KEY_VPC) == CONFIG_VALUE_VPC
    }
}