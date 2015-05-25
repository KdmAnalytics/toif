#!/bin/bash

#
# jlintWeaknesses.sh jlint.msg [expected]
#
# returns a file [fakeWeakness] that contains a fake string for each possible weakness.
#
#


msgFile=$1
fakeWeaknesses="fakeWeaknesses"
expectedWeaknesses="expectedWeaknesses"


if [ -f "$fakeWeaknesses" ]; then
  rm "$fakeWeaknesses"
fi

while read line; do
  if [[ $line == \/\/* ]]; then
    continue
  fi

  if [[ -z $line ]]; then
    continue
  fi

  #echo $line
  msg=$(echo "$line" | awk -F"\"" '{print $2}')
  id=$(echo "$line" | awk -F"," '{print $2}')
 
  id=$(echo $id | sed s/' '//g )

  echo -e "$id\t$msg" | sed s/%0s/Test/g | sed s/%1d/1/g | sed s/%[0-9]d/999/g | sed s/%[0-9]s/fooString/g | sed s/%[0-9]m/barMethod/g | sed s/%[0-9]c/CruftClass/g | sed s/%[0-9]u/uglyUTFstuff/g >> $fakeWeaknesses
done < $msgFile

if [ $# == 2 ]; then
  if [ -f "$expectedWeaknesses" ]; then
    rm "$expectedWeaknesses"
  fi
  paste $2 $fakeWeaknesses > $expectedWeaknesses
fi
