package stack

import com.amazonaws.services.cloudformation.AmazonCloudFormation

class ECRStack extends AbstractStack {

    ECRStack(AmazonCloudFormation amazonCloudFormation, String templateUrl) {
        super(amazonCloudFormation, templateUrl, null)
    }
}