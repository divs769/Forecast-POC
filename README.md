# Forecast Poc #

This service will be used to demonstrate how the continuous integration pipeline works for new hires. The service itself will store a list of names in a text file, which can be accessed through a REST API to return a number based on a parameter.

## Building

### The project needs

* JDK 8
* Gradle 3.3

### To import into STS:

* Go to import
* Select Gradle (STS)
* Click browse and select the project root folder
* Click build model
* Tick the file that appears
* Press finish

### To import into Intellij

* Got to File / Open
* Find the gradle file
* Choose open as project

### To run the service in STS:

Right click the application (found the in the src/main folder) and select run as -> Springboot application

### To run the service in Intellij:

Right click the application (found the in the src/main folder) and select Run

### To run the unit test suite:

* Browse to src/tests and right click the java file. Select run as -> JUnitTests.
* Or
* Execute `gradle test`

### To run the acceptance test (requires running service)

* Browse to `src/acceptance-test/java` and right click AcceptanceTests / choose Run
* Or
* Browse to `src/acceptance-test/resources` and right click AcceptanceTests / choose Run
* Or
* Execute `gradle acceptanceTest`

### To run the load test (requires running service)

* Browse to `src/load-test/scala` and right click Engine / choose Run
* Or
* Execute `gradle loadTest`

To check the application is healthy:

Navigate to `http://localhost:8080/health`

To view the API description:

Navigate to `http://localhost:8080/swagger-ui.html`

### To run code quality test

* Execute `gradle sonarqube`

To check results:

Navigate to `http://52.211.132.134:9000/dashboard?id=service-template`

Login Details;
Username `admin`
Password `admin`

## Initialisation ##

### To initialise the project ###

#### You must add AWS credentials for all environments to your aws credentials file you wish to deploy to prior to running init! 

They must be named the same as the environments specified in the ecs-stack-config.txt file `DeployedEnvironments` section and prefix the environment ecs-stack-config.txt files e.g. dev-ecs-stack-config.txt.
 
Follow this link for further details `http://docs.aws.amazon.com/cli/latest/userguide/cli-multiple-profiles.html`

Execute `gradle -b init.gradle sdginit`

This will use the git repository name as your service name

note: if deployment to any of the environments fail - you can run the creation of ECS via the ECSStackInitialiser class - ensure you only have the failed environment in the `DeployedEnvironments` section

### The RegisterCI process takes place during init ###

During initialisation you will be prompted for details to register a continuous integration job, including the CI box public IP, your Jenkins User Id & Api Key (which can be found on you profile within jenkins -> <username> -> Configure -> Show API Token)

note: the server address should not include http:// e.g. localhost:8080

### To register the dashboard ###

(fron git bash) Execute `sh deployToGrafana.sh`

### To upload a copy of the version file to s3 ###

(from git bash) execute 'sh upload_version_s3.sh "bucket name" "name of file"

### To build a client jar (REST models to be used in other projects)

Execute `gradle clientJar`

Any clients can use 

`dependencies {
    compile files('libs/xxxx-client.jar')
}`

### To make available in S3 (with a latest link)

Execute `gradle s3UploadClientJar` 