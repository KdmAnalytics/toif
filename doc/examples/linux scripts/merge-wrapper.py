#!/usr/bin/env python

###################################################
#An example of wrapping the gcc compiler to execute
# the adaptors.
####################################################

import subprocess
import os
import shlex
import sys

##########################################################
#for all the arguments passed to compiler, get the ones we need.
##########################################################
for arg in sys.argv:
    print arg