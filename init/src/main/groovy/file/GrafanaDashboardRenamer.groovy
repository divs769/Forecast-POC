package file
import task.Task

class GrafanaDashboardRenamer implements Task {

    private String projectRoot
    
    public static final String ORIGINAL_SERVICE_NAME = "microservice-template"
    public static final String ORIGINAL_SERVICE_NAME_DISPLAYABLE = "Microservice Template"
    private String serviceName
    private String displayableServiceName
    private String dashboardFileName

    GrafanaDashboardRenamer (projectRoot, dashboardFileName, serviceName, displayableServiceName){
        this.projectRoot = projectRoot
        this.dashboardFileName = dashboardFileName
        this.serviceName = serviceName
        this.displayableServiceName = displayableServiceName
    }

    boolean run(){
        rename(serviceName, displayableServiceName)
        println "GrafanaDashboardRenamer successful!\n"
        true
    }

    def rename(String serviceName, String serviceNameDisplayable) {

        File file = new File(projectRoot + dashboardFileName)
        String contents = file.getText('UTF-8')
        contents = contents.replace(ORIGINAL_SERVICE_NAME, serviceName)
        contents = contents.replace(ORIGINAL_SERVICE_NAME_DISPLAYABLE, serviceNameDisplayable)
        file.setText(contents)
    }
}