package file

class PathSetter {

    public static final String SERVICE_STACK_CONFIG_FILE_PATH = '/init/src/main/resources/service-stack-config.txt'
    public static final String SERVICE_CONFIG_FILE_PATH = '/src/main/resources/service-config'
    public static final String DASHBOARD_FILE_PATH = '/init/Dashboard.json'
    public static final String README_FILE_PATH = '/README.md'
    public static final String JENKINS_CONFIG_FILE_PATH = '/init/src/main/resources/JenkinsConfig.xml'
    public static final String S3_VERSION_BUCKET_PATH = 'service.%s.sd.com/version'
    public static final String JENKINS_PORT = ':8080'
    public static final String GIT_COMMIT_MESSAGE = 'ServiceInitialised'

    def map =  ["serviceStackConfigFilePath":SERVICE_STACK_CONFIG_FILE_PATH,
                "serviceConfigFilePath":SERVICE_CONFIG_FILE_PATH,
                "dashboardFilePath":DASHBOARD_FILE_PATH,
                "readMeFilePath":README_FILE_PATH,
                "jenkinsConfigFilePath":JENKINS_CONFIG_FILE_PATH,
                "jenkinsPort":JENKINS_PORT,
                "s3VersionBucketPath":S3_VERSION_BUCKET_PATH,
                "projectRoot":getProjectRoot(),
                "templateFolders":getTemplateFolders(),
                "gitCommitMessage":GIT_COMMIT_MESSAGE]

    def getPath(key){
        map.get(key)
    }

    def getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def getTemplateFolders(){
        String[] templateFolders = new String[3]
        templateFolders[0] = projectRoot + '/src/main/java/com/shopdirect/microservicetemplate'
        templateFolders[1] = projectRoot + '/src/test/java/com/shopdirect/microservicetemplate'
        templateFolders[2] = projectRoot + '/src/acceptance-test/java/com/shopdirect/microservicetemplate'
        templateFolders
    }

}