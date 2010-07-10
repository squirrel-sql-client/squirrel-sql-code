#! /usr/bin/env python

# $Id: build.py,v 1.1.1.1 2001-11-20 01:53:44 placson Exp $
# a universal (runs on win32 and unix) build front-end
# any version of python 1.5 or higher should work
# see www.python.org for binary distributions for win32
# python is usually stock on linux

import sys
import os
import string

PROJECT_ROOT = os.path.dirname(sys.argv[0])
ANT_HOME = os.path.join(PROJECT_ROOT, "thirdparty", "apache", "ant")
ANT = os.path.join(ANT_HOME, "bin", "ant")

args = [ANT]
args.extend(sys.argv[1:])
cmd = string.join(args)
os.system(cmd)
