package stack

import com.amazonaws.services.cloudformation.model.Parameter
import file.AWSIdentityRetriever

class ECRParameterBuilder {

    private AWSIdentityRetriever awsIdentityRetriever

    ECRParameterBuilder(AWSIdentityRetriever awsIdentityRetriever) {
        this.awsIdentityRetriever = awsIdentityRetriever
    }

    List<Parameter> create(String serviceName, List<String> environments) {

        List<String> accounts = new ArrayList<>()

        for (String environment : environments){

            if(environment != "dev") {
                def accountID = awsIdentityRetriever.getAccountID(environment)
                accounts.add(accountID)
            }
        }

        String accountsString = String.join(",", accounts)

        List<Parameter> parameters = Arrays.asList(
                new Parameter().withParameterKey('RepositoryName').withParameterValue(serviceName),
                new Parameter().withParameterKey('AuthorisedAccounts').withParameterValue(accountsString)
        )

        parameters
    }
}
