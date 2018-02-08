package stack

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.cloudformation.model.StackStatus

class StackMonitor {

    private AmazonCloudFormation amazonCloudFormation

    StackMonitor(amazonCloudFormation) {
        this.amazonCloudFormation = amazonCloudFormation
    }

    String waitForCompletion(String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest()
        wait.setStackName(stackName)
        Boolean completed = false
        String stackStatus = "Unknown"
        String stackReason = ""

        System.out.print("Waiting")

        while (!completed) {
            List<Stack> stacks = amazonCloudFormation.describeStacks(wait).getStacks()
            if (stacks.isEmpty()) {
                completed = true
                stackStatus = "NO_SUCH_STACK"
                stackReason = "Stack has been deleted"
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus() == StackStatus.CREATE_COMPLETE.toString() ||
                            stack.getStackStatus() == StackStatus.CREATE_FAILED.toString() ||
                            stack.getStackStatus() == StackStatus.ROLLBACK_FAILED.toString() ||
                            stack.getStackStatus() == StackStatus.DELETE_FAILED.toString() ||
                            stack.getStackStatus() == StackStatus.ROLLBACK_COMPLETE.toString()
                    ) {
                        completed = true
                        stackStatus = stack.getStackStatus()
                        stackReason = stack.getStackStatusReason()
                    }
                }
            }

            // Show we are waiting
            System.out.print(".")

            // Not done yet so sleep for 10 seconds.
            if (!completed) Thread.sleep(10000)
        }

        // Show we are done
        System.out.print("done\n")

        return stackStatus + " (" + stackReason + ")"
    }
}
