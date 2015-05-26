#!/bin/bash

# this script runs a number of checks on the xml files created as a result of running the TOIF adaptor on a set of files.
# to execute the script - ./<path to script> <path to directory containg the result of running the TOIF Adaptor>. For example, if script is in /home/TOIF/sh and the TOIF Adaptor output is in /home/kdm/TOIF/output and if I was to run the command from the /home/kdm/TOIF/output directory then in my command prompt window I would enter - ../sh/count.sh /home/kdm/TOIF/output
# # This script is set to work on KEN-PC on VM image Ubuntu-14.04.1 and Fedora-17_3

if [[ ${#} != 1 ]]; then
  echo "Error: Directory to search is missing"
  echo ""
  echo "Usage: ${0} [DIR]"
  exit -1
fi

outDir=${1}

# total number of files analyzed by TOIF Adaptor
fileCount=$(find ${outDir} -name "*.xml" | wc -l)

#Total number of findings in the files analyzed by TOIF Adaptor
findingCount=$(find ${outDir} -name "*.xml" | xargs grep "toif:Finding\"" | wc -l)

#Total number of analyzed files that report a FindBugs + security plugin finding 
securityFindingCount=$(find ${outDir} -name "*.xml" | xargs grep "SECURITY_PLUGIN" | wc -l)

# Total number of analyzed files that do not report a finding
nofindingCount=$(find ${outDir} -name "*.xml" | xargs grep -L "toif:Finding\"" | wc -l)

#This counts the total number of "real" unique file names within the set of analyzed files. Any file that has more than 1 defect tool reporting a finding is only counted once. For example, if the total number of files being analyzed is 48 and the total number of files not reporting a defect is 18 then the total number of unique files is 30 but 1 file reports 3 findings reported by a different defect tool then the true count is 28: 48-30-2=28 since the 3 findings should only be counted as 1.
uniqueFilesWithFindings=$(find ${outDir} -name "*.toif.xml" |xargs  grep -l "toif:Finding\"" | awk -F"." '{print $1"."$2}' | sort -u | wc -l )

# this counts the number defect tools reporting a finding on the same file along with the path to the file
numberOfToolsReportingOnSameFile=$(find ${outDir} -name "*.toif.xml" |xargs  grep -l "toif:Finding\"" | awk -F"." '{print $1"."$2}' | sort | uniq -cd)

echo "Total number of analyzed files is: ${fileCount}"
echo "Total number of findings in the analyzed files:${findingCount}"
echo "Total number of analyzed files that report a security finding is:${securityFindingCount}"
echo "Total number of analyzed files that do not report a finding is:${nofindingCount}"
echo "Total unique number of analyzed files that report a finding is:${uniqueFilesWithFindings}"
echo -e "Total number of defect tools that report a finding on the same analyzed file along with the path to the file:\n${numberOfToolsReportingOnSameFile}"
