#!/bin/bash

# check and set path for toif 1.16.0 and findbugs 3.0 and all release supported generators as well as 7zip
export PATH=/home/kdma/TOIF/DefectGenerators/cppcheck-1.40/cli:$PATH
export PATH=/home/kdma/TOIF/DefectGenerators/findbugs-3.0.0-secur/bin:$PATH
export PATH=/home/kdma/TOIF/DefectGenerators/jlint-3.0:$PATH
export PATH=/home/kdma/TOIF/DefectGenerators/rats-2.3:$PATH
export PATH=/home/kdma/TOIF/DefectGenerators/splint-3.1.2/bin:$PATH
export PATH=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT:$PATH
export PATH=/usr/bin/7za/:$PATH
echo $PATH

# boo="toif --h k k k k"
# ${boo}

# executes adaptor command on c files for cppcheck, rats, and splint

# cppcheck
toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Tasks.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/main.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Captain.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Pirate.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Crew.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=cppcheck --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Ship.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping


# rats
toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Tasks.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/main.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Captain.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Pirate.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Crew.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=rats --inputfile=/home/kdma/TOIF/TestFiles/Cpp/pirates-2.1/src/Ship.cpp --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

#splint
toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/file1.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/tar/incremen.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/tar/create.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/tar/list.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/tar/tar.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=splint --inputfile=/home/kdma/TOIF/TestFiles/C/tar/buffer.c --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping


#executes adaptor command on class files for findbugs, and jlint

#findbugs
toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/Tuple.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/ClusteredConsoleServlet.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/Server.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/JUDDIServlet.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/glassfish/QBrowser.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/glassfish/UniversalClient.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/FindbugsWarningsByExample1.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/FindbugsWarningsByExample2.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/Test.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/AbstractLesson.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Course.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/DatabaseUtilities.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping
toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Exec.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Interceptor.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LegacyLoader.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LessonAdapter.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LessonTracker.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=findbugs --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/ParameterParser.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping


#jlint
toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/Tuple.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/ClusteredConsoleServlet.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/Server.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/jboss/JUDDIServlet.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/glassfish/QBrowser.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/glassfish/UniversalClient.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/FindbugsWarningsByExample1.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/FindbugsWarningsByExample2.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/Test.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/AbstractLesson.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Course.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/DatabaseUtilities.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Exec.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/Interceptor.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LegacyLoader.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LessonAdapter.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/LessonTracker.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

toif --adaptor=jlint --inputfile=/home/kdma/TOIF/TestFiles/Java/webgoat/ParameterParser.class --outputdirectory=/home/kdma/TOIF/output-secur --housekeeping=/home/kdma/TOIF/toif-1.16.0-SNAPSHOT/Examples/Housekeeping

# executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=/home/kdma/TOIF/output2-secur/test.kdm --inputfile=/home/kdma/TOIF/output-secur

# runs 7-z to unzip the the assimilator output file and create a test directory that contains a test.kdm file.
7z x /home/kdma/TOIF/output2-secur/test.kdm -o/home/kdma/TOIF/output2-secur/test
exit 0

