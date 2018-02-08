package git

class GitUpdater {

    private GitAddAll gitAddAll
    private GitCommit gitCommit
    private GitPush gitPush

    GitUpdater(gitAddAll, gitCommit, gitPush){
        this.gitAddAll = gitAddAll
        this.gitCommit = gitCommit
        this.gitPush = gitPush
    }

    static void main(String[] args){
        def workingDirectory = new File(getProjectRoot())
        GitUpdater gitUpdater = new GitUpdater(new GitAddAll(workingDirectory), new GitCommit(workingDirectory), new GitPush(workingDirectory))
        gitUpdater.update('GitUpdater')
    }

    def update(String message){

        println "Updating your bitBucket repository with the initialised microservice template"

        gitAddAll.addAll()
        gitCommit.commit(message)
        gitPush.push()
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

}