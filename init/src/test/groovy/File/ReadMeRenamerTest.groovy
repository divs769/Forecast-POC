package File

import naming.NameFormatter
import spock.lang.Specification
import file.ReadMeRenamer

class ReadMeRenamerTest extends Specification {

    def"ReadMeRenamer task carried out successfully"(){

        given:"I have a task"

        String serviceName = 'readmerenamerunittest'
        String displayableServiceName = new NameFormatter().getDisplayableServiceName(serviceName)

        ReadMeRenamer readMeRenamer = new ReadMeRenamer(getProjectRoot(), '/init/src/test/resources/READMEtest.md',displayableServiceName)

        when: "I run the task"

        boolean returnedValue = readMeRenamer.run()

        then: "The ReadMe file is renamed"

        returnedValue == true
        readMeContainsMicroserviceTemplate() == false

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    boolean readMeContainsMicroserviceTemplate(){
        File file = new File(getProjectRoot() + '/init/src/test/resources/READMEtest.md')
        String contents = file.getText('UTF-8')
        contents.contains('Microservice Template')

    }

}