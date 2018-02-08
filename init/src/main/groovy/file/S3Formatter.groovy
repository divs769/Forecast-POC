package file

class S3Formatter {

    def format(String string){

        def parameterMap = [:]

        string.splitEachLine('\n'){ line ->
            def split = line[0].split('=')
            parameterMap.put(split[0],split[1])
        }
        parameterMap
    }
}