package jenkins

import org.springframework.http.*
import file.*
import org.springframework.web.client.RestTemplate
import git.GitUpdater
import task.Task

class RegisterCI implements Task{

    private String projectRoot
    private JenkinsConfigReader jenkinsConfigReader
    private GitUpdater gitUpdater
    private String jenkinsURL
    private String username
    private String apiCredentials
    private String jobName
    private String commitMessage

    RegisterCI(projectRoot, jenkinsConfigReader, gitUpdater, jenkinsURL, username, apiCredentials, jobName, commitMessage) {
        this.projectRoot = projectRoot
        this.jenkinsConfigReader = jenkinsConfigReader
        this.gitUpdater = gitUpdater
        this.jenkinsURL = jenkinsURL
        this.username = username
        this.apiCredentials = apiCredentials
        this.jobName = jobName
        this.commitMessage = commitMessage
    }

    boolean run(){
        register(jenkinsURL, username, apiCredentials, jobName, commitMessage)
        println "RegisterCI successful!\n"
        true
    }

    def register(String jenkinsURL, String username, String apiCredentials, String jobName, String commitMessage) {

        updateGit(commitMessage)

        def encoding = getEncoding(username, apiCredentials)
        def xmlConfigFile = readJenkinsXML()
        def headers = getHeaders(encoding)

        HttpEntity<String> entity = new HttpEntity<String>(xmlConfigFile, headers)
        String url = "http://$jenkinsURL//createItem?name=$jobName"
        callEndpoint(entity,url)
    }

    private updateGit(String message){
        gitUpdater.update(message)
    }

    private readJenkinsXML(){
        String jenkinsXMLPath = projectRoot + "/init/src/main/resources/JenkinsConfig.xml"
        jenkinsConfigReader.read(jenkinsXMLPath)
    }

    private getHeaders(String encoding){
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_XML)
        headers.add("Authorization", "Basic " + encoding)
        headers
    }

    private getEncoding(String username, String apiCredentials){
        Base64.getEncoder().encodeToString((username + ":" + apiCredentials).getBytes())
    }

    private callEndpoint(HttpEntity<String> entity, String url){
        println "Pushing code to Jenkins pipeline\n"
        RestTemplate rest = new RestTemplate()

        try{
            rest.exchange(url, HttpMethod.POST, entity, String.class)
        }catch(Exception e){
            println "Error carrying out registerCI : $e"
            System.exit(1)
        }

    }

}