#!/bin/bash
#check and set path for toif 1.16.0 and test directory that will run batch files
export PATH=usr/bin/python:$PATH
export PATH=/home/kdm/TOIF/tests:$PATH
export PATH=/home/kdm/TOIF/toif-1.16.0:$PATH
export PATH=/usr/bin/7za/:$PATH
echo $PATH

# executes adaptor command on a c file that acts as a host for stubbed input data on findbugs
toif --adaptor=findbugs --inputfile=/home/kdm/TOIF/TestFiles/Java/Fake.class --outputdirectory=/home/kdm/TOIF/output3 --housekeeping=/home/kdm/TOIF/toif-1.16.0/Examples/Housekeeping

if [[ ${#} != 1 ]]; then
  echo "Error: Directory to search is missing"
  echo ""
  echo "Usage: ${0} [DIR]"
  exit -1
fi

outDir=${1}

#Total number of findings in the files analyzed by TOIF Adaptor
findingCount=$(find ${outDir} -name "*.xml" | xargs grep "toif:Finding\"" | wc -l)

#Search for these strings
findingCount1=$(find ${outDir} -name "*.xml" | xargs grep "CWE--1" | wc -l)
findingCount2=$(find ${outDir} -name "*.xml" | xargs grep "DM_EXIT" | wc -l)
findingCount2=$(find ${outDir} -name "*.xml" | xargs grep "GC_UNCHECKED_TYPE_IN_GENERIC_CALL" | wc -l)
findingCount2=$(find ${outDir} -name "*.xml" | xargs grep "ICAST_BAD_SHIFT_AMOUNT" | wc -l)
findingCount2=$(find ${outDir} -name "*.xml" | xargs grep "DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION" | wc -l)
findingCount2=$(find ${outDir} -name "*.xml" | xargs grep "DMI_FUTILE_ATTEMPT_TO_CHANGE_MAXPOOL_SIZE_OF_SCHEDULED_THREAD_POOL_EXECUTOR" | wc -l)
findingCount3=$(find ${outDir} -name "*.xml" | xargs grep "RV_ABSOLUTE_VALUE_OF_RANDOM_INT" | wc -l)
findingCount4=$(find ${outDir} -name "*.xml" | xargs grep "JLM_JSR166_UTILCONCURRENT_MONITORENTER" | wc -l)
findingCount4=$(find ${outDir} -name "*.xml" | xargs grep "JML_JSR166_CALLING_WAIT_RATHER_THAN_AWAIT" | wc -l)
findingCount4=$(find ${outDir} -name "*.xml" | xargs grep "DM_GC" | wc -l)
findingCount5=$(find ${outDir} -name "*.xml" | xargs grep "JAXWS_ENDPOINT" | wc -l)
findingCount6=$(find ${outDir} -name "*.xml" | xargs grep "JAXRS_ENDPOINT" | wc -l)
findingCount7=$(find ${outDir} -name "*.xml" | xargs grep "TAPESTRY_ENDPOINT" | wc -l)
findingCount8=$(find ${outDir} -name "*.xml" | xargs grep "WICKET_ENDPOINT" | wc -l)
findingCount9=$(find ${outDir} -name "*.xml" | xargs grep "STRUTS1_ENDPOINT" | wc -l)
findingCount10=$(find ${outDir} -name "*.xml" | xargs grep "STRUTS2_ENDPOINT" | wc -l)
findingCount11=$(find ${outDir} -name "*.xml" | xargs grep "SPRING_ENDPOINT" | wc -l)
findingCount12=$(find ${outDir} -name "*.xml" | xargs grep "ECB_MODE" | wc -l)

# The result should be 460
echo "Total number of findings in the analyzed file:${findingCount}"
# The result should be that none of the above strings are found
echo "Total number of CWE--1 findings in the analyzed file:${findingCount1}"
echo "Total number of DM_EXIT findings in the analyzed file:${findingCount2}"
echo "Total number of GC_UNCHECKED_TYPE_IN_GENERIC_CALL findings in the analyzed file:${findingCount3}"
echo "Total number of ICAST_BAD_SHIFT_AMOUNT findings in the analyzed file:${findingCount4}"
echo "Total number of DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION findings in the analyzed file:${findingCount5}"
echo "Total number of DMI_FUTILE_ATTEMPT_TO_CHANGE_MAXPOOL_SIZE_OF_SCHEDULED_THREAD_POOL_EXECUTOR findings in the analyzed file:${findingCount6}"
echo "Total number of RV_ABSOLUTE_VALUE_OF_RANDOM_INT findings in the analyzed file:${findingCount7}"
echo "Total number of JLM_JSR166_UTILCONCURRENT_MONITORENTER findings in the analyzed file:${findingCount8}"
echo "Total number of JML_JSR166_CALLING_WAIT_RATHER_THAN_AWAIT findings in the analyzed file:${findingCount9}"
echo "Total number of DM_GC findings in the analyzed file:${findingCount10}"
echo "Total number of JAXWS_ENDPOINT findings in the analyzed file:${findingCount11}"
echo "Total number of JAXRS_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of TAPESTRY_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of WICKET_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of STRUTS1_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of STRUTS2_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of SPRING_ENDPOINT findings in the analyzed file:${findingCount12}"
echo "Total number of ECB_MODE findings in the analyzed file:${findingCount12}"

exit 0

