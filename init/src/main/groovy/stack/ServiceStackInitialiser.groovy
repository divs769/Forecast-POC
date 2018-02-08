package stack

import com.amazonaws.services.cloudformation.model.Parameter
import file.AWSIdentityRetriever
import file.FileConfigReader
import utilities.AsyncRunner
import utilities.HttpCaller
import file.S3Reader
import vpc.VPCConfigParser
import vpc.VPCConfigReader
import task.Task

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class ServiceStackInitialiser implements Task {

    private String serviceName
    private StackFactory stackFactory
    private HttpCaller httpCaller
    private VPCConfigReader vpcConfigReader
    private ParameterMapper parameterMapper
    private FileConfigReader fileConfigReader
    private Map<String, FileConfigReader> envFileConfigReaders
    private ECRParameterBuilder ecrParameterBuilder

    ServiceStackInitialiser(serviceName, stackFactory, httpCaller, vpcConfigReader, parameterMapper, fileConfigReader, envFileConfigReaders, ecrParameterBuilder) {
        this.serviceName = serviceName
        this.stackFactory = stackFactory
        this.httpCaller = httpCaller
        this.vpcConfigReader = vpcConfigReader
        this.parameterMapper = parameterMapper
        this.fileConfigReader = fileConfigReader
        this.envFileConfigReaders = envFileConfigReaders
        this.ecrParameterBuilder = ecrParameterBuilder
    }

    boolean run(){
        initialise(serviceName)
        println "\nServiceStackInitialiser successful!\n"
        true
    }

    void initialise(String serviceName) {

        def stackName = serviceName

        List<String> environmentsToDeploy = getEnvironmentsToDeploy()

        for (String environmentToDeploy : environmentsToDeploy)
        {
            List<Parameter> environmentalParameters = getParameters(environmentToDeploy)

            def loadBalancedUrl = stackFactory.getServiceStack(environmentToDeploy).create(stackName, environmentalParameters)

            int response = new AsyncRunner().run(caller, loadBalancedUrl, 5, 10, 200)
            assertThat(response, is(200))

            if(environmentToDeploy == "dev") {
                stackFactory.getECRStack(environmentToDeploy).create(stackName + "-docker-repository", ecrParameterBuilder.create(serviceName, environmentsToDeploy))
            }
        }
    }

    def caller = { loadBalancedUrl ->
        httpCaller.callEndpoint("http://" + loadBalancedUrl + "/health")
    }

    private ArrayList<Parameter> getParameters(environment) {

        Map<String, String> vpcConfig = vpcConfigReader.getConfig(environment)
        def parameters = parameterMapper.mapVPCConfig(vpcConfig)

        def lines = fileConfigReader.getConfig()
        parameters += parameterMapper.mapFileConfig(lines)

        def envLines = envFileConfigReaders.get(environment).getConfig()
        parameters += parameterMapper.mapFileConfig(envLines)

        parameters
    }

    List<String> getEnvironmentsToDeploy() {

        def lines = fileConfigReader.getConfig()
        return parameterMapper.mapEnvironmentsToDeploy(lines)
    }

    static void main(String[] args) {

        AWSIdentityRetriever awsIdentityRetriever = new AWSIdentityRetriever()

        VPCConfigReader vpcConfigReader = new VPCConfigReader(new S3Reader(), new VPCConfigParser(), new AWSIdentityRetriever())
        FileConfigReader fileConfigReader = new FileConfigReader(projectRoot + '/init/src/main/resources/service-stack-config.txt')
        FileConfigReader devFileConfigReader = new FileConfigReader(projectRoot + '/init/src/main/resources/dev-service-stack-config.txt')
        FileConfigReader uatFileConfigReader = new FileConfigReader(projectRoot + '/init/src/main/resources/uat-service-stack-config.txt')
        FileConfigReader nftFileConfigReader = new FileConfigReader(projectRoot + '/init/src/main/resources/nft-service-stack-config.txt')
        FileConfigReader prdFileConfigReader = new FileConfigReader(projectRoot + '/init/src/main/resources/prd-service-stack-config.txt')

        def envFileConfigReaders = [:]
        envFileConfigReaders.put("dev", devFileConfigReader)
        envFileConfigReaders.put("uat", uatFileConfigReader)
        envFileConfigReaders.put("nft", nftFileConfigReader)
        envFileConfigReaders.put("prd", prdFileConfigReader)

        ParameterMapper parameterMapper = new ParameterMapper()

        ServiceStackInitialiser serviceStackInitialiser = new ServiceStackInitialiser('serviceStackInitialiser',
                new StackFactory(), new HttpCaller(), vpcConfigReader, parameterMapper, fileConfigReader, envFileConfigReaders, new ECRParameterBuilder(awsIdentityRetriever))
        serviceStackInitialiser.initialise("microservice-template-test")

    }

    def setServiceNameToLowercase(serviceName){

        return serviceName.toLowerCase()

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }
}