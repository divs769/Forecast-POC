#!/usr/bin/env bash
if [ -z "$1" ]
  then
    echo "sdgName NOT supplied"
    exit 1
fi
if [ -z "$2" ]
  then
    echo "sdgPackage NOT supplied"
    exit 1
fi
if [ -z "$3" ]
  then
    echo "sdgVersion NOT supplied"
    exit 1
fi
if [ -z "$4" ]
  then
    echo "team account number NOT supplied"
    exit 1
fi

sdgName=$1
sdgPackage=$2
sdgVersion=$3
teamAccountNumber=$4

cp sources/Dockerfile build/libs/Dockerfile
cd build/libs && docker build --no-cache=true --build-arg jarpath=. --build-arg jarname=${sdgPackage} -t ${sdgName}:latest .
aws ecr get-login --registry-id ${teamAccountNumber} --no-include-email --region eu-west-1 | sh

docker tag ${sdgName}:latest ${teamAccountNumber}.dkr.ecr.eu-west-1.amazonaws.com/${sdgName}:latest
docker push ${teamAccountNumber}.dkr.ecr.eu-west-1.amazonaws.com/${sdgName}:latest
docker tag ${sdgName}:latest ${teamAccountNumber}.dkr.ecr.eu-west-1.amazonaws.com/${sdgName}:${sdgVersion}
docker push ${teamAccountNumber}.dkr.ecr.eu-west-1.amazonaws.com/${sdgName}:${sdgVersion}
