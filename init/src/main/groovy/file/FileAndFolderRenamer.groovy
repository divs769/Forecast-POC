package file
import static groovy.io.FileType.FILES
import task.Task

class FileAndFolderRenamer implements Task {

    public static final String PACKAGE_PREFIX = 'com.shopdirect.'
    public static final String ORIGINAL_SERVICE_NAME = 'microservicetemplate'
    String[] templateFolders = new String[3]
    private String projectRoot
    private String packageName

    FileAndFolderRenamer(projectRoot, templateFolder, packageName) {
        this.projectRoot = projectRoot
        this.templateFolders = templateFolder
        this.packageName = packageName
    }
    
    boolean run(){
        rename(packageName)
        println "FileAndFolderRenamer successful!\n"
        true
    }

    def rename(String packageName) {

        for (int i = 0; i < templateFolders.length; i++) {
            renameFiles(templateFolders[i], packageName)
            renameFolder(templateFolders[i], packageName)
        }
    }

    private renameFiles(String folderLocation, String serviceName) {

        final String originalPackageName = PACKAGE_PREFIX + ORIGINAL_SERVICE_NAME

        String packageName = PACKAGE_PREFIX + serviceName

        def baseFile = new File(folderLocation)
        if (baseFile.exists()) {
            new File(folderLocation).eachFileRecurse(FILES) {
                String contents = it.getText('UTF-8')
                contents = contents.replace(originalPackageName, packageName)
                it.setText(contents)
            }
        }
    }

    private renameFolder(String folderLocation, String serviceName) {

        int pos = folderLocation.indexOf(ORIGINAL_SERVICE_NAME)
        String folder = folderLocation.substring(0, pos)
        String intendedPackageName = folder + serviceName
        def file = new File(folderLocation)

        if(file.exists()) {
            file.renameTo(intendedPackageName)
        }
        else {
            println(folderLocation + " was not modified as it does not exist. It may have been renamed already.\n")
        }
    }
}
