package naming

import stack.ServiceStackInitialiser
import git.GitResolver

class ServiceNameRetriever {

    GitResolver gitResolver
    ServiceStackInitialiser serviceStackInitialiser

    ServiceNameRetriever(gitResolver, serviceStackInitialiser){
        this.gitResolver = gitResolver
        this.serviceStackInitialiser = serviceStackInitialiser
    }

    def getServiceName(String serviceName){
        String repositoryName = gitResolver.getRepoName()
        serviceName = (serviceName == null) ? serviceStackInitialiser.setServiceNameToLowercase(repositoryName) : serviceName
        serviceName
    }

}