#!/usr/bin/env bash

##############################################################################
##
##  Generate Release APK Script
##
##############################################################################

rm ./*.apk
./gradlew --stacktrace assembledebug

user_name=$(whoami)

if (($#==0)); then
    branch_name=$(git symbolic-ref -q HEAD)
    branch_name=${branch_name##refs/heads/}
    branch_name=${branch_name:-HEAD}
else
    branch_name=${1}
fi

DATE=`date +%m-%d@%H:%M`
name="Boolio-"${branch_name}"-staging-${user_name}"$DATE".apk"

mv "app/build/outputs/apk/app-debug.apk" $name

curl -F "file=@"$name -F channels=C061BEBML -F token=xoxp-5155145927-5155145929-6045455457-f08295 https://slack.com/api/files.upload
