package file

class JenkinsConfigReader {

    def read(String jenkinsConfigPath){

        new File(jenkinsConfigPath).getText("UTF-8")
    }

}