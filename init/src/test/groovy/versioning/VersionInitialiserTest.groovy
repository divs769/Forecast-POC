package versioning

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import file.AWSIdentityRetriever
import org.junit.Before
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class VersionInitialiserTest {

    private VersionInitialiser versionInitialiser
    private AmazonS3Client amazonS3Client
    AWSIdentityRetriever awsIdentityRetriever

    @Before
    void setUp() throws Exception {
        versionInitialiser = new VersionInitialiser()
        amazonS3Client = AmazonS3ClientBuilder.defaultClient()
        awsIdentityRetriever = new AWSIdentityRetriever()
    }

    @Test
    void copyFile() throws Exception {

        def accountID = awsIdentityRetriever.getAccountID("default")
        String fileName = "test-file"

        String bucketName = "service." + accountID + ".lsdg.com/version"

        versionInitialiser.copyFile(fileName,accountID)

        String output = readFile(bucketName, fileName)

        try {
            assertThat(output, is("0.0.0"))
        }
        finally {
            deleteFile(bucketName, fileName)
        }
    }

    private String readFile(String bucketName, String fileName) {
        amazonS3Client.getObjectAsString(bucketName, fileName)
    }

    private void deleteFile(String bucketName, String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName)
    }
}