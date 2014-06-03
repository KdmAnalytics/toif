#javaAdaptors.py

import subprocess
import os
import shlex
import sys

CP = "\"C:/Users/adam/Desktop/adaptors/*\";"
HOUSEKEEPING = "C:/Users/adam/Desktop/adaptors/housekeepingExamples/sphinxHouseKeeping"
OUTDIR = "C:/Users/adam/Desktop/sph4"

inFile = sys.argv[1]
auxclasspath = sys.argv[2]

inFile = inFile.replace("\\","/")
auxclasspath = auxclasspath.replace("\\","/")

print("file: "+inFile)
jlintCommand = shlex.split("java -cp " + CP + " ToifAdaptor --adaptor JlintAdaptor --houseKeeping " + HOUSEKEEPING + " --inputFile " + inFile + " --outputDirectory " + OUTDIR)
jlint = subprocess.Popen(jlintCommand,stdout=subprocess.PIPE)

findbugsCommand = shlex.split("java -cp " + CP + " ToifAdaptor --adaptor FindbugsAdaptor --houseKeeping " + HOUSEKEEPING + " --inputFile " + inFile + " --outputDirectory " + OUTDIR + " -auxclasspath " + auxclasspath)
findbugs = subprocess.Popen(findbugsCommand,stdout=subprocess.PIPE)
