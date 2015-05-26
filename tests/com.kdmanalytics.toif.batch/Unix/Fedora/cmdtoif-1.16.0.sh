#!/bin/bash

# check and set path for toif 1.16.0 and findbugs 3.0 and all release supported generators as well as 7zip
export PATH=/home/kdm/TOIF/DefectGenerators/cppcheck-1.40:$PATH
export PATH=/home/kdm/TOIF/DefectGenerators/findbugs-3.0.0/bin:$PATH
# export PATH=/home/kdm/TOIF/DefectGenerators/jlint-2.3:$PATH
export PATH=/home/kdm/TOIF/DefectGenerators/rats-2.3:$PATH
export PATH=/home/kdm/TOIF/DefectGenerators/splint-3.1.2/bin:$PATH
export PATH=/home/kdm/TOIF/toif-1.16.0:$PATH
export PATH=/usr/bin/7za/:$PATH
echo $PATH

# boo="toih --h k k k k"
# ${boo}

# executes adaptor command on c files for cppcheck, rats, and splint

# cppcheck
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Tasks.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/main.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Captain.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Pirate.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Crew.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=cppcheck --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Ship.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

# rats
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Tasks.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/main.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Captain.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Pirate.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Crew.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=rats --inputfile=/home/kdm/TOIF/TestFiles/Cpp/pirates-2.1/src/Ship.cpp --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

#splint
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=splint --inputfile=/home/kdm/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

#executes adaptor command on class files for findbugs, and jlint

#findbugs
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/Tuple.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/ClusteredConsoleServlet.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/Server.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/JUDDIServlet.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/glassfish/QBrowser.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/glassfish/UniversalClient.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/FindbugsWarningsByExample1.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/FindbugsWarningsByExample2.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/Test.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/AbstractLesson.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Course.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/DatabaseUtilities.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Exec.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Interceptor.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LegacyLoader.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LessonAdapter.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LessonTracker.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/ParameterParser.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

:'#jlint
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/Tuple.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/ClusteredConsoleServlet.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/Server.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/jboss/JUDDIServlet.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/glassfish/QBrowser.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/glassfish/UniversalClient.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/FindbugsWarningsByExample1.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/FindbugsWarningsByExample2.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/Test.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/AbstractLesson.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Course.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/DatabaseUtilities.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Exec.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/Interceptor.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LegacyLoader.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LessonAdapter.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/LessonTracker.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
toif --adaptor=jlint --inputfile=/home/kdm/TOIF/TestFiles/Java/webgoat/ParameterParser.class --outputdirectory=/home/kdm/TOIF/output --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping
'
# executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=/home/kdm/TOIF/output2/test.kdm --inputfile=/home/kdm/TOIF/output

# runs 7-z to unzip the the assimilator output file and create a test directory that contains a test.kdm file.
7z x /home/kdm/TOIF/output2/test.kdm -o/home/kdm/TOIF/output2/test
exit 0

