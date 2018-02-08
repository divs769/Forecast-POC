#!/bin/sh

if [ -z "$3" ]
  then
    echo "Role To Assume NOT supplied"
    exit 1
fi
if [ -z "$4" ]
  then
    echo "External ID NOT supplied"
    exit 1
fi

assumeRoleTimeout=900
serviceName=$1
newlyDeployedVersionNumber=$2
roleToAssumeARN=$3
externalId=$4

awsTargetGroupName=$serviceName

assumeRoleCommand="aws sts assume-role --role-arn $roleToAssumeARN --role-session-name PostDeploymentVersionCheckSession --external-id $externalId --duration-seconds $assumeRoleTimeout"
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

awsDescribeTargetGroups=$(aws elbv2 describe-target-groups --names $awsTargetGroupName --region eu-west-1 )

if [ $? != 0 ]; then
   echo "AWS is reporting an error (above). Post-deployment fail."
   exit 1
fi

awsLoadBalancerARN=$(echo "$awsDescribeTargetGroups" | grep loadbalancer/app | cut -d \" -f2)

awsDescribeLoadBalancers=$(aws elbv2 describe-load-balancers --load-balancer-arns $awsLoadBalancerARN --region eu-west-1)

if [ $? != 0 ]; then
   echo "AWS is reporting an error (above). Post-deployment fail."
   exit 1
fi

awsLoadBalancerDNS=$(echo "$awsDescribeLoadBalancers=" | grep DNSName | cut -d \: -f2 | cut -d \" -f2)

for numberOfTimesChecked in {1..5};
do
  liveVersionNumber=$(curl -s ${awsLoadBalancerDNS}/version | grep \"ServiceVersion\" | cut -f2 -d":" | cut -d '"' -f2)

  if [ $newlyDeployedVersionNumber = $liveVersionNumber ]; then
   echo "Success. Version ${newlyDeployedVersionNumber} is live at ${awsLoadBalancerDNS}"
   exit 0
 else
   echo "Deployment failure. Version ${liveVersionNumber} is live at ${awsLoadBalancerDNS}, instead of the expected ${newlyDeployedVersionNumber}."
   if [ $numberOfTimesChecked == 5 ]; then
     echo "Tried refreshing endpoint 5 times, service is still on incorrect version!"
     exit 1
   fi
fi
sleep 5
done
