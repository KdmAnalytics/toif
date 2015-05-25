#! /bin/bash

#
# ./jlintUpdate jlint.msg idconfig jlintAdaptorConfiguration
#
#   jlint.msg - the jlint.msg file from the new jlint version
#   idconfig  - the idconfig file from the existing adaptor
#   jlintAdaptorConfiguration - the configuration file from the existing adaptor
#
# Will result in two new files. idconfig.new and jlintAdaptorConfiguration.new
#


#check options
if [ $# -ne 3 ]; then
  exit 1
fi

for var in "$@"
do
  if [ ! -f $var ]; then
    echo "file does not exist: "$var
    exit 1
  fi
done


newMessages=$1
oldID=$2
oldConfig=$3
newID="idConfig.new"
newConfig="JlintAdaptorConfiguration.new"

#cleanup
if [ -f $newID ]; then
  rm $newID
fi

if [ -f $newConfig ]; then
  rm $newConfig
fi

grep "MSG" $newMessages | grep -v "//" | grep -v "Verification complete"| sed s/%[0-9][a-z]/\.\*/g | sed s/\"\)//g | awk -F"," '{printf $4";"$2" \n"}' | sed s/^\"//g | sed s/\\.\\*[\]\/:\.\'\,]/\\.\\*/g | sed s/[\[\/:\.\'\,]\\.\\*/\\.\\*/g | sed s/\\.\\*\\.\\*/\\.\\*/g | sed s/'; '/';'/g > $newID

for id in $(awk -F';' '{print $2}' $newID); do

  found=$(grep -e ^$id"=" $oldConfig)
  foundElement=$(grep -e ^$id"Element=" $oldConfig)

  if [ -z $found ]; then
    echo -e $id"=" | tr -d '[[:space:]]' >> $newConfig
    echo >> $newConfig
  else
    if [ -n "${foundElement}" ]; then
      echo -e $foundElement >> $newConfig

    fi
    echo -e $found | tr -d '[[:space:]]' >> $newConfig
    echo >> $newConfig
  fi
  echo >> $newConfig
done


