#!/bin/bash
if [ -z "$1" ]
  then
    echo "serviceName not supplied"
    exit 1
fi
if [ -z "$2" ]
  then
    echo "Number of tasks required NOT supplied"
    exit 1
fi
if [ -z "$3" ]
  then
    echo "AWS Repository URL required NOT supplied"
    exit 1
fi
if [ -z "$4" ]
  then
    echo "Role To Assume NOT supplied"
    exit 1
fi
if [ -z "$5" ]
  then
    echo "External ID NOT supplied"
    exit 1
fi
if [ -z "$6" ]
  then
    echo "Environment Name is not supplied "
    exit 1
fi

timeOut=300s
serviceName=$1
desiredNumberOfTasks=$2
awsRepository=$3
roleToAssumeARN=$4
externalId=$5
assumeRoleTimeout=900

environmentId=$6

awsClusterName=microservices
awsServiceName=$serviceName
awsTaskDefinition=$serviceName
awsContainerName=$serviceName

assumeRoleCommand="aws sts assume-role --role-arn $roleToAssumeARN --role-session-name DeployToAWSClusterSession --external-id $externalId --duration-seconds $assumeRoleTimeout"
assumeRoleResult=$(eval $assumeRoleCommand) || { echo "Failure on assumeRoleCommand" ; exit 1; }

SecretAccessKey=$(echo "$assumeRoleResult"|grep 'SecretAccessKey')
SecretAccessKey=$(echo $SecretAccessKey| cut -d':' -f 2)
SecretAccessKey=$(echo $SecretAccessKey| cut -d'"'  -f 2)

AccessKey=$(echo "$assumeRoleResult"|grep 'AccessKeyId')
AccessKey=$(echo $AccessKey|cut -d':' -f 2)
AccessKey=$(echo $AccessKey|cut -d'"' -f 2)

SessionToken=$(echo "$assumeRoleResult"|grep 'SessionToken')
SessionToken=$(echo $SessionToken|cut -d':' -f 2)
SessionToken=$(echo $SessionToken|cut -d',' -f 1)

export AWS_ACCESS_KEY_ID=$AccessKey
export AWS_SECRET_ACCESS_KEY=$SecretAccessKey
export AWS_SESSION_TOKEN=$SessionToken

awsTimeOutEcho="Timed out $timeOut seconds - service unstable, please check AWS Cluster tasks"

registerTask="(aws ecs register-task-definition --region eu-west-1 --family $awsTaskDefinition --container-definitions '[{\"name\": \"${awsContainerName}\", \"image\": \"${awsRepository}\", \"memory\": 512, \"logConfiguration\": { \"logDriver\": \"awslogs\", \"options\": { \"awslogs-group\": \"Services\", \"awslogs-region\": \"eu-west-1\", \"awslogs-stream-prefix\": \"${awsServiceName}\" }}, \"portMappings\": [{\"containerPort\": 8080, \"hostPort\": 0}], \"environment\":[{\"name\":\"SPRING_PROFILES_ACTIVE\",\"value\":\"$environmentId\"}]}]' | grep \"taskDefinitionArn\" | cut -d '/' -f 2 | cut -d '\"' -f 1)"


taskId=$(eval $registerTask) || { echo "Failure on registerTask command" ; exit 1; }

aws ecs update-service --cluster $awsClusterName --service $awsServiceName --task-definition $taskId --desired-count $desiredNumberOfTasks --region eu-west-1 || { echo "Failure on update-service command" ; exit 1; }

timeout ${timeOut} aws ecs wait services-stable --cluster $awsClusterName --services $awsServiceName --region eu-west-1 || { echo ${awsTimeOutEcho} ; exit 1; }

