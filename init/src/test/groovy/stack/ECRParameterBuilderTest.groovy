package stack

import com.amazonaws.services.cloudformation.model.Parameter
import file.AWSIdentityRetriever
import spock.lang.Specification

class ECRParameterBuilderTest extends Specification {

    public static final String SERVICE_NAME = "service"
    AWSIdentityRetriever awsIdentityRetriever
    ECRParameterBuilder ecrParameterBuilder

    void setup() {
        awsIdentityRetriever = Stub(AWSIdentityRetriever)
        ecrParameterBuilder = new ECRParameterBuilder(awsIdentityRetriever)
    }

    def "Creates ECR parameters"() {

        given: "a list of environments to deploy and a service name"

        List<String> environments = Arrays.asList("dev", "uat", "nft", "prd")

        awsIdentityRetriever.getAccountID("uat") >> "1234"
        awsIdentityRetriever.getAccountID("nft") >> "3456"
        awsIdentityRetriever.getAccountID("prd") >> "5678"


        when: "parameters are built"

        List<Parameter> parameters = ecrParameterBuilder.create(SERVICE_NAME, environments)

        then: "the parameters have authorised accounts"

        getParameterValue(parameters, "AuthorisedAccounts") == "1234,3456,5678"

        and: "the parameters have service name"

        getParameterValue(parameters, "RepositoryName") == SERVICE_NAME
    }

    private String getParameterValue(List<Parameter> parameters, parameterKey) {

        for (Parameter parameter : parameters) {
            if (parameter.getParameterKey() == parameterKey)
                return parameter.getParameterValue()
        }
    }
}
