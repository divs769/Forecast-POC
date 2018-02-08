package file

import spock.lang.Specification

class S3ReaderTest extends Specification {

    def "Determine whether we can successfully read a specific S3 bucket"(){

        S3Reader s3Reader = new S3Reader()

        def bucketPath = 'test.669711333016.lsdg.com'
        def fileName = 'S3ReaderTest'
        def expected = 'TestContents'

        given: "That there are cloud formation details that we aim to write to an S3 bucket"

        when: "We call the write method within the S3Writer class"

        String returned = s3Reader.read(bucketPath, fileName, "default")

        then: "Content is returned from the correct S3 bucket"

        returned == expected

    }

}