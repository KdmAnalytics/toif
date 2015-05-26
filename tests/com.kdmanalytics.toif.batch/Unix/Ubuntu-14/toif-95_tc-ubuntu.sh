#!/bin/bash
#check and set path for toif 1.16.0 and test directory that will run batch files
export PATH=usr/bin/python:$PATH
export PATH=/home/kdma/TOIF/tests:$PATH
export PATH=/home/kdma/TOIF/toif-1.16.0:$PATH

echo $PATH

# executes adaptor command on a c file that acts as a host for stubbed input data on jlint
toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdma/TOIF/output3 --housekeeping=/home/kdma/TOIF/toif-1.16.0/Examples/Housekeeping

if [[ ${#} != 1 ]]; then
  echo "Error: Directory to search is missing"
  echo ""
  echo "Usage: ${0} [DIR]"
  exit -1
fi

outDir=${1}

#Total number of findings in the files analyzed by TOIF Adaptor for jlint
findingCount1=$(find ${outDir} -wholename "/home/kdma/TOIF/output3/Fake.class.jlint.toif.xml" | xargs grep "toif:Finding\"" | wc -l)
findingCount2=$(find ${outDir} -wholename "/home/kdma/TOIF/output3/Fake.class.jlint" | xargs grep "Test.java:1:" | wc -l)

# The result should be 40 for the jlint xml file and 41 for jlint file
echo "Total number of findings in the analyzed jlint toif xml file:${findingCount1}"
echo "Total number of findings in the analyzed jlint file:${findingCount2}"

exit 0

