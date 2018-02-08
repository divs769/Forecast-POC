package versioning

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import spock.lang.Specification

class VersionInitialiserSpockTest extends Specification {

    def "VersionInitialiserSpockTest is carried out successfully"(){

        given:"I have a task to run"

        String S3BucketLocation = 'test-version.%s.sd.com/version'
        String serviceName = 'versioninitialiserspocktest'

        VersionInitialiser versionInitialiser = new VersionInitialiser(serviceName,'669711333016',S3BucketLocation)

        when: "I run the task"

        boolean returnedValue = versionInitialiser.run()

        then: "the version initialiser process is carried out and a success"

        returnedValue == true
        checkS3objectExists(serviceName) == true
    }

    boolean checkS3objectExists(String serviceName){
        AmazonS3Client amazonS3 = AmazonS3ClientBuilder.defaultClient()
        try {
            amazonS3.getObject('test-version.669711333016.sd.com/version', serviceName)
            true
        }catch(Exception e){
            false
        }
    }
}