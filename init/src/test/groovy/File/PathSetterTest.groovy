package File

import spock.lang.Specification
import file.PathSetter

class PathSetterTest extends Specification {

    def "Return correct values"(){

        given:"There are some paths that I need"

        PathSetter pathSetter = new PathSetter()

        when:"I call the method"

        String returned = pathSetter.getPath(key)

        then:"I get the values I was expecting"

        returned == expected

        where:"There are the following key and expected values"
        key                                   |     expected
        'serviceStackConfigFilePath'          |     '/init/src/main/resources/service-stack-config.txt'
        'serviceConfigFilePath'               |     '/src/main/resources/service-config'
        'dashboardFilePath'                   |     '/init/Dashboard.json'
        'readMeFilePath'                      |     '/README.md'
        'jenkinsConfigFilePath'               |     '/init/src/main/resources/JenkinsConfig.xml'
        'jenkinsPort'                         |     ':8080'
        's3VersionBucketPath'                 |     'service.%s.lsdg.com/version'
        'gitCommitMessage'                    |     'ServiceInitialised'

    }

    def "Return project root"(){

        given:"There is a project root"

        String key = "projectRoot"
        PathSetter pathSetter = new PathSetter()

        when:"I call the method"

        String returned = pathSetter.getPath(key)

        then:"I get the values I was expecting"

        returned != null

    }
    
}
