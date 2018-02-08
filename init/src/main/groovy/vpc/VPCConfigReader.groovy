package vpc

import file.AWSIdentityRetriever
import file.S3Reader

class VPCConfigReader {

    public static final String CONFIG_FILE_NAME = 'SecureVPC'
    public static final String CONFIG_BUCKET_FORMAT = 'config.%s.sd.com'
    private S3Reader s3Reader
    private VPCConfigParser configParser
    AWSIdentityRetriever awsIdentityRetriever

    VPCConfigReader(S3Reader s3Reader, VPCConfigParser configParser, AWSIdentityRetriever awsIdentityRetriever) {

        this.s3Reader = s3Reader
        this.configParser = configParser
        this.awsIdentityRetriever = awsIdentityRetriever
    }

    Map<String, String> getConfig(String environment) {

        def accountID = awsIdentityRetriever.getAccountID(environment)
        def bucketName = String.format(CONFIG_BUCKET_FORMAT,accountID)

        def vpcConfigString = s3Reader.read(bucketName, CONFIG_FILE_NAME, environment)

        Map<String, String> vpcConfigMap = configParser.parse(vpcConfigString)
        vpcConfigMap
    }


}
