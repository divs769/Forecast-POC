package versioning

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import task.Task

class VersionInitialiser implements Task {

    private String awsSourceFileName = "RENAME-ME"
    private String serviceName
    private String teamAccountNumber
    private String awsBucketFormat

    VersionInitialiser(serviceName, teamAccountNumber, awsBucketFormat){
        this.serviceName = serviceName
        this.teamAccountNumber = teamAccountNumber
        this.awsBucketFormat = awsBucketFormat
    }

    boolean run(){
        copyFile(serviceName,teamAccountNumber)
        println "VersionInitialiser successful!\n"
        true
    }

    def copyFile(String destinationFileName, String accountNumber) {

        AmazonS3Client amazonS3Client = AmazonS3ClientBuilder.defaultClient()
        String awsBucketName = String.format(awsBucketFormat, accountNumber)
        amazonS3Client.copyObject(awsBucketName, awsSourceFileName, awsBucketName, destinationFileName)

    }

}