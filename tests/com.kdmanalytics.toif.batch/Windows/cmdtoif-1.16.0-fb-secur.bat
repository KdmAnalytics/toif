:: check and set path for toif 1.16.0 and findbugs 3.0 + security plugin and all release supported generators as well as 7-zip
IF EXIST C:\TOIF\DefectGenerators\Cppcheck SET PATH=C:\TOIF\DefectGenerators\Cppcheck;%PATH%
IF EXIST C:\TOIF\DefectGenerators\findbugs-3.0.0-secur\bin SET PATH=C:\TOIF\DefectGenerators\findbugs-3.0.0-secur\bin;%PATH%
IF EXIST C:\TOIF\DefectGenerators\jlint-3.0 SET PATH=C:\TOIF\DefectGenerators\jlint-3.0;%PATH%
IF EXIST C:\TOIF\DefectGenerators\rats-2.3 SET PATH=C:\TOIF\DefectGenerators\rats-2.3;%PATH%
IF EXIST C:\TOIF\DefectGenerators\splint\bin SET PATH=C:\TOIF\DefectGenerators\splint\bin;%PATH%
IF EXIST C:\toif-1.16.0 SET PATH=C:\toif-1.16.0;%PATH%
IF EXIST C:\Expat2.1.0\Bin SET PATH=C:\Expat2.1.0\Bin;%PATH%
IF EXIST C:\7-Zip\ SET PATH=C:\7-Zip\;%PATH%
::executes adaptor command on c files for cppcheck, rats, and splint
::cppcheck
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\file1.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\tar\incremen.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\tar\create.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\tar\list.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\tar\tar.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\C\tar\buffer.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Tasks.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Main.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Captain.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Pirate.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Crew.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=cppcheck --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Ship.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::rats
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\file1.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\tar\incremen.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\tar\create.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\tar\list.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\tar\tar.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\C\tar\buffer.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Tasks.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Main.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Captain.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Pirate.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Crew.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=rats --inputfile=C:\TOIF\TestFiles\Cpp\pirates-2.1\src\Ship.cpp --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::splint
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\file1.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\tar\incremen.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\tar\create.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\tar\list.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\tar\tar.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=splint --inputfile=C:\TOIF\TestFiles\C\tar\buffer.c --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::executes adaptor command on class files for findbugs, and jlint
::findbugs
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\Tuple.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\jboss\ClusteredConsoleServlet.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\jboss\Server.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\jboss\JUDDIServlet.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\glassfish\QBrowser.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\glassfish\UniversalClient.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\FindbugsWarningsByExample1.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\FindbugsWarningsByExample2.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\Test.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\AbstractLesson.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\Course.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\DatabaseUtilities.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\Exec.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\Interceptor.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\LegacyLoader.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\LessonAdapter.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\LessonTracker.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\webgoat\ParameterParser.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::jlint
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\Tuple.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\jboss\ClusteredConsoleServlet.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\jboss\Server.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\jboss\JUDDIServlet.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\glassfish\QBrowser.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\glassfish\UniversalClient.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\FindbugsWarningsByExample1.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\FindbugsWarningsByExample2.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\Test.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\AbstractLesson.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\Course.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\DatabaseUtilities.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\Exec.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\Interceptor.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\LegacyLoader.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\LessonAdapter.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\LessonTracker.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\webgoat\ParameterParser.class --outputdirectory=c:\TOIF\output-secur --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=C:\TOIF\output2-secur\test-secur.kdm --inputfile=c:\TOIF\output-secur
:: runs 7-z to unzip the the assimilator output file and create a test directory that contains a test.kdm file.
7z e C:\TOIF\output2-secur\test-secur.kdm -oc:\TOIF\output2-secur\test\ *test-secur.kdm -r
EXIT 0

