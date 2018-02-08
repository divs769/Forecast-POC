package stack

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import file.AWSIdentityRetriever
import file.FileConfigReader
import file.S3Reader
import spock.lang.Specification
import utilities.HttpCaller
import vpc.VPCConfigParser
import vpc.VPCConfigReader

import com.amazonaws.services.cloudformation.model.DeleteStackRequest
import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder

class ServiceStackInitialiserTest extends Specification {

    def "ServiceStackInitialiser is carried out successfully"(){

        given: "I have a task to run"

        String serviceName = "servicestackinitunittest"

        VPCConfigReader vpcConfigReader = new VPCConfigReader(new S3Reader(), new VPCConfigParser(), new AWSIdentityRetriever())
        FileConfigReader fileConfigReader = new FileConfigReader(getProjectRoot() + '/init/src/test/resources/service-stack-config-test.txt')
        FileConfigReader devFileConfigReader = new FileConfigReader(getProjectRoot() + '/init/src/test/resources/dev-service-stack-config-test.txt')

        def envFileConfigReaders = [:]
        envFileConfigReaders.put("dev", devFileConfigReader)

        ServiceStackInitialiser serviceStackInitialiser = new ServiceStackInitialiser(serviceName, new StackFactory()
                ,new HttpCaller(), vpcConfigReader, new ParameterMapper(),fileConfigReader, envFileConfigReaders, new ECRParameterBuilder(new AWSIdentityRetriever()))

        when: "I run the task"

        serviceStackInitialiser.run()

        then: "ECS and ECR stacks are initialised"

        checkStacksExist(serviceName) == true
        checkStacksExist(serviceName + "-docker-repository") == true

        and: "Delete the stacks"

        deleteStacks(serviceName)
        deleteStacks(serviceName + "-docker-repository")
    }

    def deleteStacks(String serviceName){
        AmazonCloudFormation stackBuilder = AmazonCloudFormationClientBuilder.standard().withCredentials(new ProfileCredentialsProvider('dev')).withRegion(Regions.EU_WEST_1).build()
        DeleteStackRequest deleteRequest = new DeleteStackRequest()
        deleteRequest.setStackName(serviceName)
        println "Deleting the stack called " + deleteRequest.getStackName()
        stackBuilder.deleteStack(deleteRequest)
    }

    def checkStacksExist(String serviceName){
        AmazonCloudFormation stackBuilder = AmazonCloudFormationClientBuilder.standard().withCredentials(new ProfileCredentialsProvider('dev')).withRegion(Regions.EU_WEST_1).build()
        stackBuilder.describeStacks().toString().contains(serviceName)
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

}