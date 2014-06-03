#! /usr/bin/bash 

echo wrapper $*
echo $PATH

HOUSE_KEEPING="/c/kdm/housekeeping.txt"
OUT_DIR="/c/tmp/toif"
TOIF=toif
USER_GCC=/c/kdm/gcc/bin/gcc.exe
CPPCHECK_ARGS="--enable=all"
RATS_ARGS=""
SPLINT_ARGS="+posixlib -weak"

TOIF_INCLUDES=()
TOIF_DEFINES=()
TOIF_SOURCE="NONE"

for arg in $*
do
    echo $arg
    if [[ $arg == -I* ]] ;
    then
        #echo "include: $arg"
        TOIF_INCLUDES=( "${TOIF_INCLUDES[@]} $arg")
    fi

    if [[ $arg == -D* ]] ;
    then
        #echo "define: $arg"
        TOIF_DEFINES=( "${TOIF_DEFINES[@]} $arg")
    fi

    if [[ $arg == *.c ]] ;
    then
        #echo "source: $arg"
        TOIF_SOURCE=$arg
        
    fi
done
echo ${TOIF_INCLUDES[@]} >> /tmp/robert.txt 
echo "${TOIF_DEFINES[@]}"
echo "${TOIF_SOURCE}"

##################################################
# Check if we will accept line for TOIF processing
##################################################
if [ ${TOIF_SOURCE} != "NONE" ] ;
then
   #echo "found" >> /tmp/robert.txt
   COMMON_ARGS=("--housekeeping=${HOUSE_KEEPING} --outputdirectory=${OUT_DIR} --inputfile=${TOIF_SOURCE} -- ")
   
   #################################
   # Start processing for each tool
   #################################
   ${TOIF} --adaptor=cppcheck ${COMMON_ARGS[@]} ${CPPCHECK_ARGS} &
   ${TOIF} --adaptor=rats     ${COMMON_ARGS[@]} ${RATS_ARGS} &
   ${TOIF} --adaptor=splint   ${COMMON_ARGS[@]} ${SPLINT_ARGS} &

   wait
fi

##################################################
# Perform users compile command now
##################################################
${USER_GCC} $*
exit $?



      


