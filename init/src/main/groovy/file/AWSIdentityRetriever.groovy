package file

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest

class AWSIdentityRetriever {

    def getAccountID(String environment){

        AWSSecurityTokenService awsSecurityTokenServiceClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(environment)).build()

        GetCallerIdentityResult getCallerIdentityResult = awsSecurityTokenServiceClient.getCallerIdentity(new GetCallerIdentityRequest())
        String accountId = getCallerIdentityResult.getAccount()
        accountId

    }

}
