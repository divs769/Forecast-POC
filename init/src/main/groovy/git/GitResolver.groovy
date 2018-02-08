package git

class GitResolver {

    public static final String GIT_ORIGIN_CMD = "git remote show origin -n"

    private originOutput

    String getRepoName() {
        getRepoNameDotGit().split('[.]')[0]
    }

    String getRepoUrl() {
        (getOriginOutput() =~ "git@.+")[0]
    }

    private String getRepoNameDotGit() {
        (getOriginOutput() =~ "[^/]+[.]git")[0]
    }

    private String getOriginOutput() {

        if(originOutput != null)
            originOutput

        try {
            originOutput = GIT_ORIGIN_CMD.execute().text
        } catch (Exception ex) {
            println("Something went wrong while attempting to communicate with the Git Repo. The command git remote show origin -n seems to have failed.")
            throw ex
        }

    }

}