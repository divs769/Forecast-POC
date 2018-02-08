package stack

import com.amazonaws.services.cloudformation.AmazonCloudFormation

class ServiceStack extends AbstractStack {

    public static final String OUTPUT_KEY = "LoadBalancerDNS"

    ServiceStack(AmazonCloudFormation amazonCloudFormation, String templateUrl) {
        super(amazonCloudFormation, templateUrl, OUTPUT_KEY)
    }
}