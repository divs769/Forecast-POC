#!/bin/bash
if [ -z "$1" ]
  then
    echo "serviceName (service name) not supplied"
    exit 1
fi
if [ -z "$2" ]
  then
    echo "serviceVersion (desired version number) not supplied"
    exit 1
fi
if [ -z "$3" ]
  then
    echo "teamAccountNumber (desired team account number) not supplied"
    exit 1
fi

serviceName=$1
serviceVersion=$2
teamAccountNumber=$3

s3VersionPath="s3://service.${teamAccountNumber}.sd.com/version/"
s3VersionQuery="aws s3 cp ${s3VersionPath}${serviceName} temp --only-show-errors"
${s3VersionQuery}

if [ ! -f temp ]
then 
  echo "Cannot access S3 version bucket"
  exit 1 
else 
  serviceDeployedVersion=$(cat temp)
  rm temp
fi

#serviceGreatestVersion holds the greatest of serviceDeployedVersion and serviceVersion
serviceGreatestVersion=$(printf "${serviceVersion}\n${serviceDeployedVersion}" | sort -Vr | head -1)

if [ ${serviceVersion} = ${serviceDeployedVersion} ];
  then
    echo "Error: The desired version number, ${serviceVersion} is identical to the current version number ${serviceDeployedVersion} present at ${s3ServiceVersionPath}${serviceName}."
    exit 1
elif [ ${serviceVersion} = ${serviceGreatestVersion} ];
  then
    echo "Success: The desired version number, ${serviceVersion} is greater than the current version number ${serviceDeployedVersion} present at ${s3ServiceVersionPath}${serviceName}."
    exit 0
elif [ ${serviceVersion} != ${serviceGreatestVersion} ];
  then
    echo "Error: The desired version number, ${serviceVersion} is less than the current version number ${serviceDeployedVersion} present at ${s3ServiceVersionPath}${serviceName}."
    exit 1
fi
