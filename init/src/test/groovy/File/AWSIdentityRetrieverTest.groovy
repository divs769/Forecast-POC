package file

import spock.lang.Specification

class AWSIdentityRetrieverTest extends Specification {

    def "Determine whether I can retrieve my AWS account ID"() {

        given: "I need to retrieve the ID number of the AWS account that I currently reside"

        def expectedAccountNumber = "669711333016"

        when: "I call the getAccountID method within the GetAWSAccountID class"

        AWSIdentityRetriever getAwsAccountId = new AWSIdentityRetriever()
        def returned = getAwsAccountId.getAccountID()

        then: "Returned is my account number"

        returned == expectedAccountNumber

    }

}
