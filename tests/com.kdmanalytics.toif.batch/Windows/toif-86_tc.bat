:: check and set path for toif 1.16.0 and test directory that will run batch files
IF EXIST C:\Python34 SET PATH=C:\Python34\;%PATH%
IF EXIST C:\TOIF\tests SET PATH=C:\TOIF\tests\;%PATH%
IF EXIST C:\toif-1.16.0 SET PATH=C:\toif-1.16.0;%PATH%
IF EXIST C:\7-Zip\ SET PATH=C:\7-Zip\;%PATH%
echo %PATH%
::executes adaptor command on a c file that acts as a host for stubbed input data on findbugs
toif --adaptor=findbugs --inputfile=C:\TOIF\TestFiles\Java\Fake.class --outputdirectory=c:\TOIF\output3 --housekeeping=C:\toif-1.16.0\Examples\Housekeeping

:: count the number of findings in the 
find /c  "xsi:type=""toif:Finding""/" c:\TOIF\output3\Fake.class.findbugs.toif.xml

:: print out the relative file name/path and line number (/n) in a recursive (/s), case insensitive (/i) search for these strings in Fake.class.findbugs.toif.xml file, ignoring files with non-printable characters (/p)
findstr /s /n /i /p /c:"CWE--1" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"DM_EXIT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"GC_UNCHECKED_TYPE_IN_GENERIC_CALL" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"ICAST_BAD_SHIFT_AMOUNT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"DMI_FUTILE_ATTEMPT_TO_CHANGE_MAXPOOL_SIZE_OF_SCHEDULED_THREAD_POOL_EXECUTOR" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"RV_ABSOLUTE_VALUE_OF_RANDOM_INT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"JLM_JSR166_UTILCONCURRENT_MONITORENTER" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"JML_JSR166_CALLING_WAIT_RATHER_THAN_AWAIT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"DM_GC" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"JAXWS_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"JAXRS_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"TAPESTRY_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"WICKET_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"STRUTS1_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"STRUTS2_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"SPRING_ENDPOINT" C:\TOIF\output3\Fake.class.findbugs.toif.xml
findstr /s /n /i /p /c:"ECB_MODE" C:\TOIF\output3\Fake.class.findbugs.toif.xml


:: The result should be that none of the above strings are found
PAUSE
EXIT 0

