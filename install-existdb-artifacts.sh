#!/bin/bash

ECHOMODE=echo
EXECMODE=$ECHOMODE
# Parse command-line parameters
while getopts "e" option
    # : after option shows it will have an argument passed with it.
do
    case $option in
        e )
            EXECMODE="exec";;
        * ) # Unknown option
            echo "Invalid option -- $option"
            exit 0 ;;
    esac
done




# try to find the existdb location from pre defined values and use first one
searchPaths=$(echo $HOME/e{X,x}ist{-db,db,})
existdbpaths=( $(ls -ad  $HOME/e{X,x}ist{-db,db,} $USER_EXISTDB_ROOT 2> /dev/null) )
basedir=$existdbpaths

echo "# searchPaths=$searchPaths"

if [ ! -e "$basedir" ]
then
  echo ${existdbpaths[@]}
  echo "Error: can't find existdb installation dir. export USER_EXISTDB_ROOT with directory path of your existdb installation"
  exit 1
fi

V=$(grep version $basedir/VERSION.txt)
version=${V##*=}
echo "# basedir=$basedir"
echo "# version=$version"
echo

jars="exist lib/core/xmldb" # exist-optional exist-versioning

echo "# Please cut and paste this section to install artifacts"
for jar in $jars
do
  cmd="mvn install:install-file -Dfile=$basedir/$jar.jar -DgroupId=org.exist-db -DartifactId=exist-$(basename $jar) -Dversion=$version -Dpackaging=jar"
  echo "# installing artifactId=$jar"
  if [ $EXECMODE = $ECHOMODE ]
  then
    echo "$cmd"
  else 
    $cmd 
  fi
done

echo "# Please cut and paste this section or check consistency with your pom file"
echo
echo " <dependencies>"
echo
echo "   <!-- eXistDB Library -->"
for jar in $jars
do
  echo
  echo "   <dependency>"
  echo "   <groupId>org.exist-db</groupId>"
  echo "   <artifactId>exist-$(basename $jar)</artifactId>"
  echo "   <version>$version</version>"
  echo "   </dependency>"
  echo
done
echo " </dependencies>"


