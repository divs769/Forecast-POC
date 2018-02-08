package file

import spock.lang.Specification

class S3FormatterTest extends Specification {

    def "Determine whether I can format a string taken from S3 into key/value pairs "() {

        S3Formatter s3formatter = new S3Formatter()

        given: "That there is an unformatted string taken from S3"

        String s3String = "ElasticIPOne=52.214.136.240" + "\n" +
                          "ElasticIPThree=54.229.78.182" + "\n" +
                          "ElasticIPTwo=52.210.117.35"

        Map expected = [ElasticIPOne:'52.214.136.240', ElasticIPThree:'54.229.78.182', ElasticIPTwo:'52.210.117.35']

        when: "I call the toKeyValuePairs method"

        def returned = s3formatter.format(s3String)

        then: "The method returns a key value pair array"

        returned == expected

    }

}