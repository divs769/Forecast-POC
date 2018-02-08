package vpc

class VPCConfigParser {

    Map<String, String> parse(String string){

        Map<String, String> configMap = new HashMap<>()

        string.splitEachLine('\n'){ line ->
            def split = line[0].split('=')
            configMap.put(split[0],split[1])
        }

        configMap
    }
}
