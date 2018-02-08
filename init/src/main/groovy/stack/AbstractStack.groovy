package stack

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.CreateStackRequest
import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.Output
import com.amazonaws.services.cloudformation.model.Stack

abstract class AbstractStack {

    private AmazonCloudFormation amazonCloudFormation
    private String templateUrl
    private StackMonitor stackMonitor
    private String outputKey

    AbstractStack(AmazonCloudFormation amazonCloudFormation, String templateUrl, String outputKey) {
        this.stackMonitor = new StackMonitor(amazonCloudFormation)
        this.amazonCloudFormation = amazonCloudFormation
        this.templateUrl = templateUrl
        this.outputKey = outputKey
    }

    String create(stackName, parameters) {

        CreateStackRequest createRequest = new CreateStackRequest()
                .withStackName(stackName)
                .withParameters(parameters)
                .withTemplateURL(templateUrl)

        System.out.println("Creating a stack called " + createRequest.getStackName() + ".")
        amazonCloudFormation.createStack(createRequest)
        System.out.println("Stack creation completed, the stack " + stackName + " completed with " + stackMonitor.waitForCompletion(stackName))
        getOutput(stackName)
    }

    def delete(String stackName) {

        DeleteStackRequest deleteRequest = new DeleteStackRequest().withStackName(stackName)
        println "Deleting the stack called " + deleteRequest.getStackName() + "."
        amazonCloudFormation.deleteStack(deleteRequest)

   }

    String getOutput(String stackName) {

        if(outputKey != null) {

            DescribeStacksRequest describeStacksRequest = new DescribeStacksRequest()
            describeStacksRequest.setStackName(stackName)

            List<Stack> stacks = amazonCloudFormation.describeStacks(describeStacksRequest).getStacks()

            for (Stack stack : stacks) {
                for (Output output : stack.getOutputs()) {
                    if(output.getOutputKey() == outputKey) {
                        return output.getOutputValue()
                    }
                }
            }
        }
    }
}
