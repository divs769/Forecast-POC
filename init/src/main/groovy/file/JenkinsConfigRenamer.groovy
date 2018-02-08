package file

import task.Task

class JenkinsConfigRenamer implements Task {
    private String projectRoot

    public static final String ORIGINAL_GIT_URL = "git@bitbucket.org:ShopDirect/microservice-template.git"
    public static final String ORIGINAL_SERVICE_NAME_DISPLAYABLE = "Microservice Template"
    private String jenkinsConfigFilePath
    private String gitURL
    private String serviceDisplayableName

    JenkinsConfigRenamer(projectRoot, jenkinsConfigFilePath, gitURL, serviceDisplayableName){
        this.projectRoot = projectRoot
        this.jenkinsConfigFilePath = jenkinsConfigFilePath
        this.gitURL = gitURL
        this.serviceDisplayableName = serviceDisplayableName
    }

    boolean run(){
        rename(gitURL, serviceDisplayableName)
        println "JenkinsConfigRenamer successful!\n"
        true
    }

    def rename(String gitURL, String serviceNameDisplayable) {

        File file = new File(projectRoot + jenkinsConfigFilePath)
        String contents = file.getText('UTF-8')
        contents = contents.replace(ORIGINAL_GIT_URL, gitURL)
        contents = contents.replace(ORIGINAL_SERVICE_NAME_DISPLAYABLE, serviceNameDisplayable)
        file.setText(contents)
    }

}