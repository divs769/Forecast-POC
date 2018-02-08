package jenkins

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import git.GitUpdater
import git.GitAddAll
import git.GitCommit
import git.GitPush
import spock.lang.Specification
import spock.lang.Ignore
import file.*

class RegisterCITest extends Specification {

    @Ignore('To be ignored in CI pipeline')

    def "RegisterCI task is carried out successfully"(){

        given: "I have a task to run"

        def jenkinsBoxPublicIP = 'jenkins.ignite.lsdg.com'
        def port = ":8080"
        def jenkinsURL = jenkinsBoxPublicIP + port
        def username = 'igniteadmin'
        def apiCredentials = 'e2a71e4688d2300e1def21aeaa1721eb'
        def jobName = 'RegisterCIUnitTest'
        def commitMessage = 'RegisterCITest'

        RegisterCI registerCI = new RegisterCI(getProjectRoot(),new JenkinsConfigReader(), new GitUpdater
                (new GitAddAll(getProjectRoot()), new GitCommit(getProjectRoot()), new GitPush(getProjectRoot())),jenkinsURL,username,apiCredentials,jobName, commitMessage)

        when: "I run the task"

        boolean returnedValue = registerCI.run()

        then: "RegisterCI is successfully carried out"

        returnedValue == true
        checkJobExists(username, apiCredentials, jenkinsURL, jobName) == true

        and: "Delete the job from Jenkins"

        delete(username, apiCredentials, jenkinsURL, jobName)

    }

    def checkJobExists(username, apiCredentials, jenkinsURL, jobName){
        RestTemplate rest = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        def encoding = Base64.getEncoder().encodeToString((username +":"+ apiCredentials).getBytes())
        headers.add("Authorization", "Basic " + encoding)
        HttpEntity<String> entity = new HttpEntity<String>(null, headers)
        String checkJob = "http://$jenkinsURL/job/$jobName/"
        try{rest.exchange(checkJob, HttpMethod.GET, entity, String.class)
            true
        }catch(Exception e){
            false
        }
    }

    def delete(username, apiCredentials, jenkinsURL, jobName){

        RestTemplate rest = new RestTemplate()
        HttpHeaders headers = new HttpHeaders()
        def encoding = Base64.getEncoder().encodeToString((username +":"+ apiCredentials).getBytes())
        headers.add("Authorization", "Basic " + encoding)
        HttpEntity<String> entity = new HttpEntity<String>(null, headers)
        String deleteURL = "http://$jenkinsURL/job/$jobName/doDelete"
        rest.exchange(deleteURL, HttpMethod.POST, entity, String.class)
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

}