#check and set path for toif 1.16.0 and test directory that will run batch files
export PATH=usr/bin/python:$PATH
export PATH=/home/kdm/TOIF/tests:$PATH
export PATH=/home/kdm/TOIF/toif-1.16.0:$PATH
export PATH=/usr/bin/7za/:$PATH
echo $PATH

#executes adaptor command on a c file that acts as a host for stubbed input data on both findbugs and jlint
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdm/TOIF/output3 --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdm/TOIF/output3 --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

#executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=/home/kdm/TOIF/output3-parser/test.kdm --inputfile=/home/kdm/TOIF/output3

#runs 7-z to unzip the the assimilator output file and create a test directory that contains a test.kdm file.
7z x /home/kdm/TOIF/output3-parser/test.kdm -o/home/kdm/TOIF/output3-parser/test
exit 0


