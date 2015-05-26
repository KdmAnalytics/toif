#!/bin/bash

# check and set path for toif 1.16.0 and test directory that will run bash files

export PATH=/home/kdma/TOIF/tests/findbugs:$PATH
export PATH=/home/kdma/TOIF/tests/jlint:$PATH
export PATH=/home/kdma/TOIF/tests:$PATH
export PATH=/home/kdma/TOIF/toif-1.16.0:$PATH
export PATH=/usr/bin/7za/:$PATH
echo $PATH

# executes adaptor command on a c file that acts as a host for stubbed input data on both findbugs and jlint
toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdma/TOIF/output3 --housekeeping=/home/kdma/TOIF/toif-1.16.0/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdma/TOIF/output3 --housekeeping=/home/kdma/TOIF/toif-1.16.0/Examples/Housekeeping

# executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=/home/kdma/TOIF/output3-parser/test.kdm --inputfile=/home/kdma/TOIF/output3

# unzips the assimilator output file and places the .kdm file in a test directory
7z x /home/kdma/TOIF/output3-parser/test.kdm -o/home/kdma/TOIF/output3-parser/test
exit 0
