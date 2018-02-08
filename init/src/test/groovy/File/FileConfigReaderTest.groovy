package file

import spock.lang.Specification

class FileConfigReaderTest extends Specification {

    def "Read fileconfig into list of strings"() {

        given: "a file exists with parameter lines"

        FileConfigReader fileConfigReader = new FileConfigReader('src/test/resources/reader-service-stack-config-test.txt')

        when: "the file config is retrieved"

        List<String> configLines = fileConfigReader.getConfig()

        then: "the lines are equal to the file"

        configLines.size() == 2
        true == containsLine(configLines, "LineOne")
        true == containsLine(configLines, "LineTwo")
    }

    private boolean containsLine(List<String> configLines, String expectedConfigLine) {

        for (String line : configLines) {
            if (expectedConfigLine == line)
                return true
        }

        return false
    }
}
