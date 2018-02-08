package File

import spock.lang.Specification
import naming.NameFormatter
import file.GrafanaDashboardRenamer

class GrafanaDashboardRenamerTest extends Specification {

    def "GrafanaDashboardRenamer task carried out successfully"(){

        given:"I have a task"

        String serviceName = 'grafanarenamerunittest'
        String displayableServiceName = new NameFormatter().getDisplayableServiceName(serviceName)

        GrafanaDashboardRenamer grafanaDashboardRenamer = new GrafanaDashboardRenamer(getProjectRoot()
                ,'/init/src/test/resources/DashboardTest.json', serviceName, displayableServiceName)

        when: "I run the task"

        boolean returnValue = grafanaDashboardRenamer.run()

        then: "GrafanaDashboard is successfully renamed and a success message is returned"

        returnValue == true
        testGrafanaDashboardRenamer() == false
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    boolean testGrafanaDashboardRenamer(){
        File file = new File(getProjectRoot() + '/init/src/test/resources/DashboardTest.json')
        String contents = file.getText('UTF-8')
        contents.contains('microservice-template')
    }

}