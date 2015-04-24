#javaAdaptors.py

import subprocess
import os
import shlex
import sys


#  Can be used with an ant target similar to this:
#
#  <!-- my target -->
#  <target name="mytarget" depends="build">
#    <apply executable="python">
#      <fileset dir="${build.dir}">
#        <patternset>
#          <include name="**/*.class"/>
#        </patternset>
#      </fileset>
#      <arg value="javaAdaptors.py"/>
#      <srcfile/>
#    </apply>
#  </target>
#




inFile = sys.argv[1]

HOUSE_KEEPING = "/toifTest/housekeeping"
OUTPUT_DIR    = "toifTest"

commonArgs = ["--housekeeping",HOUSE_KEEPING,"--outputdirectory",OUTPUT_DIR,"--inputfile",inFile]

SUB_PROCESS = []

fb_cmd = ["toif", "--adaptor","Findbugs"]
fb_cmd.extend( commonArgs )
print fb_cmd
p = subprocess.Popen( fb_cmd, shell=False)
SUB_PROCESS.append( p )

jl_cmd = ["toif", "--adaptor","Jlint"]
jl_cmd.extend( commonArgs )
print jl_cmd
p = subprocess.Popen( jl_cmd, shell=False)
SUB_PROCESS.append( p )

for p in SUB_PROCESS:
  p.wait() 
