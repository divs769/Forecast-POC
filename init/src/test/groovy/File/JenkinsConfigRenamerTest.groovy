package File

import naming.NameFormatter
import spock.lang.Specification
import file.JenkinsConfigRenamer

class JenkinsConfigRenamerTest extends Specification {

    def "I have the JenkinsConfigRenamer task to run"(){

        given: "I have a task to run"

        String serviceName = 'jenkinsconfigrenamertest'
        String displayableServiceName = new NameFormatter().getDisplayableServiceName(serviceName)

        JenkinsConfigRenamer jenkinsConfigRenamer = new JenkinsConfigRenamer(getProjectRoot(),'/init/src/test/resources/JenkinsConfigTest.xml',
                'git@bitbucket.org:ShopDirect/jenkinsconfigrenamertest.git',displayableServiceName)

        when: "I run the task"

        boolean returnedValue = jenkinsConfigRenamer.run()

        then: "The Jenkins Config is renamered and a success message is returned"

        returnedValue == true
        testJenkinsConfigRenamer() == false

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    def testJenkinsConfigRenamer(){
        File file = new File(getProjectRoot() + '/init/src/test/resources/JenkinsConfigTest.xml')
        String contents = file.getText('UTF-8')
        contents.contains('Microservice Template')
    }

}