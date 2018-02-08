package naming

import spock.lang.Specification
import git.GitResolver
import stack.ServiceStackInitialiser

class ServiceNameRetrieverTest extends Specification {

    def"the correct service name is returned"(){

        given:"My service has a name"

        ServiceNameRetriever serviceNameRetriever = new ServiceNameRetriever(new GitResolver(), new ServiceStackInitialiser(null, null, null, null, null, null, null, null))
        String expected = "microservice-template"

        when:"I call the method"

        String returned = serviceNameRetriever.getServiceName(null)

        then:"The correct service name is returned"

        returned == expected

    }

}