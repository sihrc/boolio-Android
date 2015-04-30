rm ./*.apk
./gradlew assemblerelease

user_name=$(whoami)

if (($#==0)); then
    branch_name=$(git symbolic-ref -q HEAD)
    branch_name=${branch_name##refs/heads/}
    branch_name=${branch_name:-HEAD}
else
    branch_name=${1}
fi

DATE=`date +%m-%d@%H:%M`
name="Boolio-"${branch_name}"-production-${user_name}-"$DATE".apk"

mv "app/build/outputs/apk/app-release.apk" $name

curl -F "file=@"$name -F channels=G03TSBRV8 -F token=xoxp-2417793652-2417793654-2760868112-82d164 https://slack.com/api/files.upload
