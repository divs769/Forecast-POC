package stack

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder

class StackFactory {

    public static final String SERVICE_KEY = "SERVICE"
    public static final String ECR_KEY = "ECR"

    private HashMap<String, String> templateMap = new HashMap<>()

    StackFactory() {
        initTemplateMap()
    }

    ServiceStack getServiceStack(environment) {
        new ServiceStack(getCloudformationClient(environment), getTemplateUrl(SERVICE_KEY))
    }

    ECRStack getECRStack(environment) {
        new ECRStack(getCloudformationClient(environment), getTemplateUrl(ECR_KEY))
    }

    def initTemplateMap() {
        templateMap.put(SERVICE_KEY, "https://s3-eu-west-1.amazonaws.com/cloudformation.ignite.lsdg.com/service-registration.yml")
        templateMap.put(ECR_KEY, "https://s3-eu-west-1.amazonaws.com/cloudformation.ignite.lsdg.com/ecr.yml")
    }

    def getTemplateUrl(key) {
        templateMap.get(key)
    }

    AmazonCloudFormation getCloudformationClient(environment) {

        return AmazonCloudFormationClientBuilder.standard().withRegion(Regions.EU_WEST_1)
                .withCredentials(new ProfileCredentialsProvider(environment))
                .build()
    }
}