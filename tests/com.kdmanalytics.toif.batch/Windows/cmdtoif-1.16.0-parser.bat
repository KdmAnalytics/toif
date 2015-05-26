:: check and set path for toif 1.16.0 and test directory that will run batch files
IF EXIST C:\Python34 SET PATH=C:\Python34\;%PATH%
IF EXIST C:\TOIF\tests SET PATH=C:\TOIF\tests\;%PATH%
IF EXIST C:\toif-1.16.0\ SET PATH=C:\toif-1.16.0\;%PATH%
IF EXIST C:\7-Zip\ SET PATH=C:\7-Zip\;%PATH%
echo %PATH%
::executes adaptor command on a c file that acts as a host for stubbed input data on both findbugs and jlint
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\Fake.class --outputdirectory=c:\TOIF\output3 --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
toif --adaptor=jlint --inputfile=C:\TOIF\TestFiles\Java\Fake.class --outputdirectory=c:\TOIF\output3 --housekeeping=C:\toif-1.16.0\Examples\Housekeeping
::executes assimilator command to merge the TOIF findings on the output of running the adaptor commands above 
toif --merge --kdmfile=C:\TOIF\output3-parser\test.kdm --inputfile=C:\TOIF\output3
:: runs 7-z to unzip the the assimilator output file and create a test directory that contains a test.kdm file.
7z e C:\TOIF\output3-parser\test.kdm -oc:\TOIF\output3-parser\test\ *test.kdm -r
EXIT 0

