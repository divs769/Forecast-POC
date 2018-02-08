package File

import file.JenkinsConfigReader
import spock.lang.Specification

class JenkinsConfigReaderTest extends Specification {

    JenkinsConfigReader jenkinsConfigReader = new JenkinsConfigReader()

    def "Determine whether I can read an XML file using the JenkinsConfigReaderTest"(){

        given:"There is an XML file that I need to read into a BufferedReader & a StringBuilder"

        String jenkinsConfigPath = "src/test/resources/JenkinsConfigReaderTest.xml"

        when: "I call the read method with a specific xml file path"

        def returned = jenkinsConfigReader.read(jenkinsConfigPath).toString()
        def expected = "<?xml version='1.0' encoding='UTF-8'?>\r\n" +
                       "<test-parameters>\r\n" +
                       "    <parameters>this-is-a-test</parameters>\r\n" +
                       "</test-parameters>"

        then: "I get the expected outcome"

        returned == expected

    }

}