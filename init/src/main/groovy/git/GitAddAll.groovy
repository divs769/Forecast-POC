package git

class GitAddAll {

    private String projectRoot

    GitAddAll(projectRoot){
        this.projectRoot = projectRoot
    }

    def addAll(){

        def workingDirectory = new File(projectRoot)
        def addAll = "git add .".execute(null, workingDirectory)
        addAll.waitFor()

    }

}