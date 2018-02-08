package File

import spock.lang.Specification
import file.FileAndFolderRenamer

class FileAndFolderRenamerTest extends Specification {

    def "FileAndFolderRenamer Task carried out successfully"(){

        given:"I have a task to run"
        
        String[] templateFolders = new String[3]
        templateFolders[0] = projectRoot + '/init/src/test/resources/microservicetemplate'
        templateFolders[1] = projectRoot + '/init/src/test/resources/microservicetemplate'
        templateFolders[2] = projectRoot + '/init/src/test/resources/microservicetemplate'

        FileAndFolderRenamer fileAndFolderRenamer = new FileAndFolderRenamer(getProjectRoot(), templateFolders, 'fileandfolderrenamerunittest')

        when:"I run the task"

        boolean returnedValue = fileAndFolderRenamer.run()

        then:"Files and folders are renamed and a success message is returned"

        returnedValue == true
        testFileAndFolderRenamer() == true
    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

    boolean testFileAndFolderRenamer(){
        File folderURL = new File(getProjectRoot() + '/init/src/test/resources/')
        List list = []
        list.add(folderURL.listFiles())

        return list.each{ value ->
            boolean outcome = false
            if(value.toString().contains('FileAndFolderRenamerUnitTest')){
                outcome = true
            }
            outcome
        }

    }

}