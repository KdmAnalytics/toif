:: check and set path for toif 1.16.0 and test directory that will run batch files
IF EXIST C:\Python34 SET PATH=C:\Python34\;%PATH%
IF EXIST C:\TOIF\tests SET PATH=C:\TOIF\tests\;%PATH%
IF EXIST C:\toif-1.16.0 SET PATH=C:\toif-1.16.0;%PATH%

echo %PATH%
::executes adaptor command on a c file that acts as a host for stubbed input data on jlint
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\Fake.class --outputdirectory=c:\TOIF\output3 --housekeeping=C:\toif-1.16.0\Examples\Housekeeping

:: count the number of findings (40) for jlint
find /c  "xsi:type=""toif:Finding""/" c:\TOIF\output3\Fake.class.jlint.toif.xml
find /c  "Test.java:1:" c:\TOIF\output3\Fake.class.jlint


PAUSE
EXIT 0

