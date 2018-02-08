package git

import spock.lang.Specification
import spock.lang.Ignore

class GitCommitTest extends Specification {

    @Ignore('To be ignored in CI pipeline')
    def "Commit changes to local repo"(){

        def workingDirectory = new File(getProjectRoot())
        GitCommit gitCommit = new GitCommit(workingDirectory)
        String message = "GitCommitUnitTest"

        given: "That I have just staged changes to the microservice template"

        when: "I call the commit method"

        gitCommit.commit(message)
        String gitStatus = getGitStatus(workingDirectory)

        then: "My changes are committed to my local repository that I currently reside"

        gitStatus.contains("Your branch is ahead of")

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def getGitStatus(workingDirectory){
        "git status".execute(null, workingDirectory).text
    }

}
