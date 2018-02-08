package git

import spock.lang.Specification
import spock.lang.Ignore

class GitAddAllTest extends Specification {

    @Ignore('To be ignored in CI pipeline')
    def "Stage changes in Git"(){

        def workingDirectory = new File(getProjectRoot())

        GitAddAll gitAddAll = new GitAddAll(getProjectRoot())

        given: "That I've made changes to code in the Microservice template"

        when: "I run the add method"

        gitAddAll.addAll()
        String gitStatus = getGitStatus(workingDirectory)

        then: "Changes are successfully staged within git"

        gitStatus.contains("Your branch is up-to-date with")

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def getGitStatus(workingDirectory){
        "git status".execute(null, workingDirectory).text
    }

}