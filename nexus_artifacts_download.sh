#!/bin/bash -e
# Artifactory location
server="https://userid:pswd@xxxxxx.de"
repo="repository/ccccccc-snapshot"
# Maven artifact location
folders="war1 war2 jar1 jar2"
echo $folders
for VARIABLE in ${folders}
do
  echo $VARIABLE
  artifact="net/pay_co/$VARIABLE"
  echo $artifact
  path="$server/$repo/$artifact"
  echo $path
  version=`curl -s "$path/maven-metadata.xml" | grep latest | sed "s/.*<latest>\([^<]*\)<\/latest>.*/\1/"`
  echo $version
  build=`curl -s "$path/$version/maven-metadata.xml" | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
  echo $build
  if [[ ( "$VARIABLE" == "brain" ) || ( "$VARIABLE" == "merchantServiceArea3" ) || ( "$VARIABLE" == "PaycoTool" ) ]]
  then
    jar="$VARIABLE-$build.war"
  else
    jar="$VARIABLE-$build.jar"
  fi
  url="$path/$version/$jar"
  echo $url
  wget -q -N $url
  sleep 5
done

#wars and Jars are in folder
