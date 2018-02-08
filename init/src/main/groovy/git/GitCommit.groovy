package git

class GitCommit {

    private String projectRoot

    GitCommit(projectRoot){
        this.projectRoot = projectRoot
    }

    def commit(String message){

        def workingDirectory = new File(projectRoot)
        def commit = "git commit  -m '$message'".execute(null, workingDirectory)
        commit.waitFor()

    }

}