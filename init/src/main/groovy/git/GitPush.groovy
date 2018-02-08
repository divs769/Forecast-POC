package git

class GitPush {

    private String projectRoot

    GitPush(projectRoot){
        this.projectRoot = projectRoot
    }

    def push(){

        def workingDirectory = new File(projectRoot)
        def push = "git push".execute(null, workingDirectory)
        push.waitFor()

    }

}