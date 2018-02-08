package file

import task.Task

class ReadMeRenamer implements Task {

    private String projectRoot

    public static final String ORIGINAL_SERVICE_NAME_DISPLAYABLE = "Microservice Template"
    private String readmeFilePath
    private String serviceNameDisplayable

    ReadMeRenamer (projectRoot, readmeFilePath, serviceNameDisplayable){
        this.projectRoot = projectRoot
        this.readmeFilePath = readmeFilePath
        this.serviceNameDisplayable = serviceNameDisplayable
    }

    boolean run(){
        rename(serviceNameDisplayable)
        println "ReadMeRenamer successful!\n"
        true
    }

    def rename(String serviceNameDisplayable) {

        File file = new File(projectRoot + readmeFilePath)
        String contents = file.getText('UTF-8')
        contents = contents.replace(ORIGINAL_SERVICE_NAME_DISPLAYABLE, serviceNameDisplayable)
        file.setText(contents)
    }
}
