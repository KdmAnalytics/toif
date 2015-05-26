@echo off

:: This script runs a number of checks on the xml files created as a result of running the TOIF adaptor on a set of files (running the "cmdtoif-1.16.0.bat" script).
:: To execute the script - ./<path to script> <path to directory containing the result of running the TOIF Adaptor>. For example, if script is in /home/TOIF/sh and the TOIF Adaptor output is in C:\TOIF\output and if I was to run the command from the /home/kdm/TOIF/output directory then in my command prompt window I would enter - ../sh/count.sh /home/kdm/TOIF/output
:: This script is set to work on a Windows 7 machine with the necessary output directory from running the TOIF adaptor on some files (C:\TOIF\output).

:: total number of files analyzed by TOIF Adaptor
dir /b /a:-D "C:\TOIF\output\*.toif.xml" | FIND "" /v /c

::Total number of findings in the files analyzed by TOIF Adaptor (using POWERSHELL)
::powershell
::Get-ChildItem C:\TOIF\output\*.toif.xml | get-content | select-string -pattern '"toif:Finding"' | measure-object
::exit

::Total number of analyzed files that report a FindBugs + security plugin finding 
findstr /m /c:"SECURITY_PLUGIN" C:\TOIF\output\*.toif.xml | find /c ":"

::Total number of analyzed files that DO report a finding
findstr /m /c:"toif:Finding" C:\TOIF\output\*.toif.xml | find /c ":"

::Total number of analyzed files that DO NOT report a finding (using POWERSHELL)
::powershell
::Get-ChildItem C:\TOIF\output\*.toif.xml | gci | Where-Object { !( $_ | select-string '"toif:Finding"' -quiet) } | measure-object
::exit

::This counts the total number of "real" unique file names within the set of analyzed files. Any file that has more than 1 defect tool reporting a finding is only counted once. For example, if the total number of files being analyzed is 48 and the total number of files not reporting a defect is 18 then the total number of unique files is 30 but 1 file reports 3 findings reported by a different defect tool then the true count is 28: 48-30-2=28 since the 3 findings should only be counted as 1.
::uniqueFilesWithFindings=$(find ${outDir} -name "*.toif.xml" |xargs  grep -l "toif:Finding\"" | awk -F"." '{print $1"."$2}' | sort -u | wc -l )

::this counts the number defect tools reporting a finding on the same file along with the path to the file
::numberOfToolsReportingOnSameFile=$(find ${outDir} -name "*.toif.xml" |xargs  grep -l "toif:Finding\"" | awk -F"." '{print $1"."$2}' | sort | uniq -cd)

PAUSE
EXIT 0