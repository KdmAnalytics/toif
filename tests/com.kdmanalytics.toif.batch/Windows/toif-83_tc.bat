@echo off

:: This script checks the Examples directory for example scripts and housekeeping file.
 IF EXIST C:\toif-1.16.0\Examples\gcc-python-wrapper.py echo The gcc-python-wrapper.py file exists.
 IF EXIST C:\toif-1.16.0\Examples\Housekeeping echo The Housekeeping file exists.
 IF EXIST C:\toif-1.16.0\Examples\javaAdaptors.py echo The javaAdaptors.py file exists.

PAUSE
EXIT 0