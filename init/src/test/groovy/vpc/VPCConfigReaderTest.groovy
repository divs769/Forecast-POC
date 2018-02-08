package vpc

import file.AWSIdentityRetriever
import file.S3Reader
import spock.lang.Specification

class VPCConfigReaderTest extends Specification {

    public static final String ENVIRONMENT = "dev"
    private AWSIdentityRetriever awsIdentityRetriever
    private VPCConfigReader vpcConfigReader
    private S3Reader s3Reader
    private VPCConfigParser vpcConfigParser
    private Map<String, String> vpcConfigMap = new HashMap<>()

    void setup() {

        awsIdentityRetriever = Stub(AWSIdentityRetriever)
        s3Reader = Stub(S3Reader)
        vpcConfigParser = Stub(VPCConfigParser)
        vpcConfigReader = new VPCConfigReader(s3Reader, vpcConfigParser, awsIdentityRetriever)

        awsIdentityRetriever.getAccountID(ENVIRONMENT) >> 1234
    }

    def "Get vpc config from s3"() {

        given: "S3 has some VPC config values"

        def vpcConfigString = "configString"
        s3Reader.read(String.format(VPCConfigReader.CONFIG_BUCKET_FORMAT, 1234), VPCConfigReader.CONFIG_FILE_NAME, ENVIRONMENT) >> vpcConfigString

        and: "Config string is parsed into a map"
        vpcConfigParser.parse(vpcConfigString) >> vpcConfigMap

        when: "I request the values"

        Map<String, String> vpcConfig = vpcConfigReader.getConfig(ENVIRONMENT)

        then: "The VPC config values should be returned as a map"

        vpcConfig == vpcConfigMap
    }
}
