package git

import spock.lang.Specification
import spock.lang.Ignore

class GitPushTest extends Specification {

    @Ignore('To be ignored in CI pipeline')
    def "Push code to my remote repository"(){

        def workingDirectory = new File(getProjectRoot())
        GitPush gitPush = new GitPush(workingDirectory)

        given: "That I have staged and committed changes to my repo"

        when: "I call the push method"

        gitPush.push()
        String gitStatus = getGitStatus(workingDirectory)

        then: "My changes are push up to my remote repo"

        gitStatus.contains("nothing to commit, working tree clean")

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def getGitStatus(workingDirectory){
        "git status".execute(null, workingDirectory).text
    }

}