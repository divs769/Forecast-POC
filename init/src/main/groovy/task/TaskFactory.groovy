package task

import file.AWSIdentityRetriever
import file.ServiceStackConfigFileWriter
import file.ConfigFileWriter
import file.ConfigFileFormatter
import file.FileAndFolderRenamer
import file.GrafanaDashboardRenamer
import file.ReadMeRenamer
import file.JenkinsConfigRenamer
import git.GitAddAll
import git.GitCommit
import git.GitPush
import stack.ServiceStackInitialiser
import ui.UserInputHandler
import ui.UserInputRetriever
import versioning.VersionInitialiser
import jenkins.RegisterCI
import file.ServiceStackConfigFileUpdater
import git.GitResolver
import naming.NameFormatter
import stack.StackFactory
import utilities.HttpCaller
import vpc.VPCConfigReader
import stack.ParameterMapper
import file.FileConfigReader
import stack.ECRParameterBuilder
import file.JenkinsConfigReader
import git.GitUpdater
import file.PathSetter
import naming.ServiceNameRetriever

class TaskFactory
{
    private String serviceName
    private ServiceStackConfigFileUpdater serviceStackConfigFileUpdater
    private ServiceStackInitialiser serviceStackInitialiser
    private GitResolver gitResolver
    private UserInputRetriever userInputRetriever
    private ConfigFileFormatter configFileFormatter
    private NameFormatter nameFormatter
    private StackFactory stackFactory
    private HttpCaller httpCaller
    private VPCConfigReader vpcConfigReader
    private ParameterMapper parameterMapper
    private FileConfigReader fileConfigReader
    private Map<String, FileConfigReader> envFileConfigReaders
    private ECRParameterBuilder ecrParameterBuilder
    private JenkinsConfigReader jenkinsConfigReader
    private GitUpdater gitUpdater
    private PathSetter pathSetter
    private ServiceNameRetriever serviceNameRetriever

    TaskFactory(serviceName, serviceStackConfigFileUpdater, serviceStackInitialiser, gitResolver, userInputRetriever, configFileFormatter, nameFormatter, stackFactory, httpCaller, vpcConfigReader, parameterMapper,
                fileConfigReader, envFileConfigReaders, ecrParameterBuilder, jenkinsConfigReader, gitUpdater, pathSetter, serviceNameRetriever){
        this.serviceName = serviceName
        this.serviceStackConfigFileUpdater = serviceStackConfigFileUpdater
        this.serviceStackInitialiser = serviceStackInitialiser
        this.gitResolver = gitResolver
        this.userInputRetriever = userInputRetriever
        this.configFileFormatter = configFileFormatter
        this.nameFormatter = nameFormatter
        this.stackFactory = stackFactory
        this.httpCaller = httpCaller
        this.vpcConfigReader = vpcConfigReader
        this.parameterMapper = parameterMapper
        this.fileConfigReader = fileConfigReader
        this.envFileConfigReaders = envFileConfigReaders
        this.ecrParameterBuilder = ecrParameterBuilder
        this.jenkinsConfigReader = jenkinsConfigReader
        this.gitUpdater = gitUpdater
        this.pathSetter = pathSetter
        this.serviceNameRetriever = serviceNameRetriever
    }


    def getServiceStackConfigFileWriter() {
        new ServiceStackConfigFileWriter(pathSetter.getPath("projectRoot"), pathSetter.getPath("serviceStackConfigFilePath"), serviceStackConfigFileUpdater.update
                (serviceNameRetriever.getServiceName(serviceName), userInputRetriever.getUserInput("sharedAccountNumber"), userInputRetriever.getUserInput("numberOfTasks")))
    }

    def getConfigFileWriter() {
        new ConfigFileWriter(pathSetter.getPath("projectRoot"), pathSetter.getPath("serviceConfigFilePath"), configFileFormatter, serviceNameRetriever.getServiceName(serviceName), userInputRetriever.getUserInput("initialVersion"),
                userInputRetriever.getUserInput("teamAccountNumber"), userInputRetriever.getUserInput("sharedAccountNumber"),
                userInputRetriever.getUserInput("numberOfTasks"))
    }

    def getFileAndFolderRenamer() {
        new FileAndFolderRenamer(pathSetter.getPath("projectRoot"), pathSetter.getPath("templateFolders"), nameFormatter.getPackageName(serviceNameRetriever.getServiceName(serviceName)))
    }

    def getGrafanaDashboardRenamer() {
        new GrafanaDashboardRenamer(pathSetter.getPath("projectRoot"), pathSetter.getPath("dashboardFilePath"), serviceNameRetriever.getServiceName(serviceName), nameFormatter.getDisplayableServiceName(serviceNameRetriever.getServiceName(serviceName)))
    }

    def getReadMeRenamer() {
        new ReadMeRenamer(pathSetter.getPath("projectRoot"),pathSetter.getPath("readMeFilePath"), nameFormatter.getDisplayableServiceName(serviceNameRetriever.getServiceName(serviceName)))
    }

    def getJenkinsConfigRenamer() {
        new JenkinsConfigRenamer(pathSetter.getPath("projectRoot"),pathSetter.getPath("jenkinsConfigFilePath"), gitResolver.getRepoUrl(), nameFormatter.getDisplayableServiceName(serviceNameRetriever.getServiceName(serviceName)))
    }

    def getServiceStackInitialiser() {
        new ServiceStackInitialiser(serviceNameRetriever.getServiceName(serviceName), stackFactory, httpCaller, vpcConfigReader, parameterMapper, fileConfigReader, envFileConfigReaders,
                ecrParameterBuilder)
    }

    def getVersionInitialiser() {
        new VersionInitialiser(serviceNameRetriever.getServiceName(serviceName), userInputRetriever.getUserInput("teamAccountNumber"), pathSetter.getPath("s3VersionBucketPath"))
    }

    def getRegisterCI() {
        new RegisterCI(pathSetter.getPath("projectRoot"), jenkinsConfigReader, gitUpdater, userInputRetriever.getUserInput("jenkinsPublicIP") + pathSetter.getPath("jenkinsPort"), userInputRetriever.getUserInput("jenkinsUsername"),
                userInputRetriever.getUserInput("jenkinsApiCredentials"), serviceNameRetriever.getServiceName(serviceName), pathSetter.getPath("gitCommitMessage"))
    }


}