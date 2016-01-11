#!/usr/bin/env python

###################################################
#An example of wrapping the gcc compiler to execute
# the adaptors.
####################################################

import subprocess
import os
import shlex
import sys

#################################################
# User defined options
#
# HOUSE_KEEPING = TOIF HOUSE_KEEPING File
# OUTPUT_DIR = Location where TOIF result files
#            will be created. Directory will be 
#            created if it does not exist
# COMPILER = process name of original compiler which 
#          will called after TOIF is done.
#          if set to none compile step will be skipped
#
# DEBUG = simple flag for debugging
#################################################
HOUSE_KEEPING = "toifTest/housekeeping"
OUTPUT_DIR    = "toifTest"
COMPILER      = "gcc"
DEBUG         = False

#################################################
# Define which tools we wish to run and any options that 
# we wish to pass
#
# To disable a tool set name to None. For example:
#    CPP_CHECK=None
#################################################
CPP_CHECK="cppcheck"                # run cppcheck
RATS="rats"                         # run rats
SPLINT="splint"                     # run splint

###########################################
# String arrays to track options to the 
# compiler
###########################################
include = ""        # -I options
options = ""        # -D options

# This is a list of files that we will assume are source
CList = [".c",".cpp",".C",".CPP"]
TOIF_INCLUDES = []      # Array of -I options
TOIF_DEFINES  = []      # Array of -D options
TOIF_SOURCE   = None    # Do we have source file

SUB_PROCESS = []        # Array of TOIF took jobs

##########################################################
# Create output directory of it does not exist
##########################################################
if not os.path.exists( OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)

##########################################################
#for all the arguments passed to compiler, get the ones we need.
##########################################################
for arg in sys.argv:
    # Handle -I options
    if arg.startswith( "-I" ):
        TOIF_INCLUDES.append( arg )
        continue
        
    # handle -D options
    if arg.startswith( "-D" ):
        TOIF_DEFINES.append( arg)
        continue
    
    # handle source file (assume 1 source per compile)    
    for cType in CList:
        if arg.endswith( cType ):
            TOIF_SOURCE = arg
            break
            

if DEBUG == True:           
    print "TOIF includes=", TOIF_INCLUDES
    print "TOIF DEFINES", TOIF_DEFINES
    print "TOIF_SOURCE", TOIF_SOURCE 

#########################################################
# Perform TOIF processing if we have detected source
#########################################################
if TOIF_SOURCE != None:
    # Build common TOIF parameter block
    commonArgs = ["--housekeeping",HOUSE_KEEPING,"--outputdirectory",OUTPUT_DIR,"--inputfile",TOIF_SOURCE]
    
    #commonArgs.append( "--" )
    #commonArgs.extend( TOIF_INCLUDES )
    #commonArgs.extend( TOIF_DEFINES )
    
    if DEBUG == True:
        print "common TOIF args=", commonArgs
    
    # Do cppcheck if enabled
    if CPP_CHECK != None:
        cppcheck_cmd = ["toif", "--adaptor","Cppcheck"]
        cppcheck_cmd.extend( commonArgs )
        print cppcheck_cmd
        
        # Start cppcheck subprocess
        p = subprocess.Popen( cppcheck_cmd, shell=False)
        SUB_PROCESS.append( p )
    
    # Do Rats if enabled 
    if RATS != None:
        rats_cmd = ["toif", "--adaptor","Rats"]
        rats_cmd.extend( commonArgs )
        print rats_cmd
        
        # start RATS subprocess
        p = subprocess.Popen( rats_cmd, shell=True )
        SUB_PROCESS.append( p )
    
    # Do splint if enabled
    if SPLINT != None:
        splint_cmd = ["toif", "--adaptor","Splint"]
        splint_cmd.extend( commonArgs )
        print splint_cmd
        
        # Start splint subprocess
        p = subprocess.Popen( splint_cmd, shell=False )
        SUB_PROCESS.append( p )
        
    ######################################
    # Wait for each subprocess to complete
    ######################################
    for p in SUB_PROCESS:
       p.wait()    

#run the compiler with all the original arguments
# if None skip compiler step
if COMPILER != None:
    subprocess.call([COMPILER]+sys.argv[1:])








