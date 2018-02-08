import file.AWSIdentityRetriever
import file.FileConfigReader
import file.ConfigFileFormatter
import file.S3Reader
import file.JenkinsConfigReader
import git.GitResolver
import git.GitUpdater
import git.GitAddAll
import git.GitCommit
import git.GitPush
import naming.NameFormatter
import stack.ECRParameterBuilder
import stack.ParameterMapper
import file.ServiceStackConfigFileUpdater
import stack.ServiceStackInitialiser
import stack.StackFactory
import ui.UserInputHandler
import ui.UserInputRetriever
import utilities.HttpCaller
import vpc.VPCConfigParser
import vpc.VPCConfigReader
import task.TaskFactory
import naming.ServiceNameRetriever
import file.PathSetter
import task.TaskRunner
import task.Task

class Initialiser {

    private UserInputHandler userInputHandler
    private TaskFactory taskFactory

    Initialiser(userInputHandler, taskFactory) {

        this.userInputHandler = userInputHandler
        this.taskFactory = taskFactory
    }

    static void main(String[] args) {

        String serviceName

        if(args.length > 0)
            serviceName = args[0]

        Initialiser initialiser = getInitialiser(serviceName)
        initialiser.initialise()
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def initialise() {

        def getServiceStackConfigFileWriter = taskFactory.getServiceStackConfigFileWriter()
        def getConfigFileWriter = taskFactory.getConfigFileWriter()
        def fileAndFolderRenamer = taskFactory.getFileAndFolderRenamer()
        def grafanaDashboardRenamer = taskFactory.getGrafanaDashboardRenamer()
        def readMeRenamer = taskFactory.getReadMeRenamer()
        def jenkinsConfigRenamer = taskFactory.getJenkinsConfigRenamer()
        def serviceStackInitialiser = taskFactory.getServiceStackInitialiser()
        def versionInitialiser = taskFactory.getVersionInitialiser()
        def registerCI = taskFactory.getRegisterCI()

        List<Task> listOfTasks = [getServiceStackConfigFileWriter, getConfigFileWriter, fileAndFolderRenamer,
                                  grafanaDashboardRenamer, readMeRenamer, jenkinsConfigRenamer,
                                  serviceStackInitialiser, versionInitialiser, registerCI]

        TaskRunner taskRunner = new TaskRunner(listOfTasks)
        taskRunner.run()

    }

    static Initialiser getInitialiser(String serviceName) {

        UserInputHandler userInputHandler = new UserInputHandler()
        AWSIdentityRetriever awsIdentityRetriever = new AWSIdentityRetriever()

        VPCConfigReader vpcConfigReader = new VPCConfigReader(new S3Reader(), new VPCConfigParser(), awsIdentityRetriever)
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

        ServiceStackInitialiser serviceStackInitialiser = new ServiceStackInitialiser(new UserInputHandler(),
                new StackFactory(), new HttpCaller(), vpcConfigReader, new ParameterMapper(), fileConfigReader, envFileConfigReaders, new ECRParameterBuilder(awsIdentityRetriever))


        TaskFactory taskFactory = new TaskFactory(serviceName, new ServiceStackConfigFileUpdater(new ParameterMapper(), fileConfigReader),
                serviceStackInitialiser,new GitResolver(),new UserInputRetriever(new UserInputHandler()), new ConfigFileFormatter(),
                new NameFormatter(), new StackFactory(), new HttpCaller(), vpcConfigReader, new ParameterMapper(), fileConfigReader, envFileConfigReaders,
                new ECRParameterBuilder(new AWSIdentityRetriever()),new JenkinsConfigReader(), new GitUpdater(new GitAddAll(projectRoot),
                new GitCommit(projectRoot), new GitPush(projectRoot)), new PathSetter(), new ServiceNameRetriever(new GitResolver(),serviceStackInitialiser)
                )

        new Initialiser(userInputHandler, taskFactory)

    }

}