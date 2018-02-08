package file

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream

class S3Reader {

    def read(String bucketName, String fileName, String environment) {

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(environment)).build()
        S3Object s3Object = amazonS3.getObject(bucketName, fileName)
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()
        String s3Contents = s3ObjectInputStream.getText().toString()
        s3ObjectInputStream.close()
        s3Contents

    }

}