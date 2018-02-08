package file

class FileConfigReader {

    private String fileLocation

    FileConfigReader(String fileLocation) {
        this.fileLocation = fileLocation
    }

    def getConfig() {

        def file = new File(fileLocation)
        file.readLines()
    }
}