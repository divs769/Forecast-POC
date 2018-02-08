package stack

import com.amazonaws.services.cloudformation.model.Parameter
import utilities.AsyncRunner
import utilities.HttpCaller

import static org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat

class ECSStackTest {

    public static final int TIMES_TO_CALL = 5
    StackFactory stackFactory

    @Before
    void setUp() throws Exception {
        stackFactory = new StackFactory()
    }

    @Test
    void createStack() throws Exception {

        ServiceStack ecsStack = stackFactory.getServiceStack()
        List<Parameter> parameters = Arrays.asList(

                new Parameter().withParameterKey("ServiceName").withParameterValue("mark-service"),
                new Parameter().withParameterKey("ExternalLBSecurityGroup").withParameterValue("sg-7f19c604"),
                new Parameter().withParameterKey("InternalLBSecurityGroup").withParameterValue("sg-211cc35a"),
                new Parameter().withParameterKey("PrivateSubnets").withParameterValue("subnet-2a08a171,subnet-f91f739e,subnet-94c3b7dd"),
                new Parameter().withParameterKey("PublicSubnets").withParameterValue("subnet-3b09a060,subnet-b91975de,subnet-36ceba7f"),
                new Parameter().withParameterKey("VPCName").withParameterValue("vpc-51235536"),
                new Parameter().withParameterKey("ExposedPort").withParameterValue("80"),
                new Parameter().withParameterKey("InternetFacing").withParameterValue("true"),
                new Parameter().withParameterKey("ClusterName").withParameterValue("microservices"),
                new Parameter().withParameterKey("ContainerPort").withParameterValue("8080"),
                new Parameter().withParameterKey("DockerImage").withParameterValue("669711333016.dkr.ecr.eu-west-1.amazonaws.com/microservice-template:latest"),
                new Parameter().withParameterKey("ServiceRole").withParameterValue("ecsServiceRole"),
                new Parameter().withParameterKey("NumberOfTasks").withParameterValue("2")
        )

        def stackName = "TestServiceRegistrationStack"

        String loadBalancedUrl = ecsStack.create(stackName, parameters)

        int response = new AsyncRunner().run(caller, loadBalancedUrl, TIMES_TO_CALL, 10, 200)

        assertThat(response, is(200))

        ecsStack.delete(stackName)
    }

    def caller = { loadBalancedUrl ->
        new HttpCaller().callEndpoint("http://" + loadBalancedUrl + "/health")
    }
}