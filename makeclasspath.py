#!/usr/bin/env python

# For python2
from __future__ import print_function

import platform
import glob
import os
import sys

def notice(*objs):
    print("+", *objs)
def warning(*objs):
    print("!", *objs, file=sys.stderr)
def newline():
    print()

osname = platform.system()

xml = """<?xml version="1.0" encoding="UTF-8"?>
<classpath>
    <classpathentry kind="src" path="src"/>
    <classpathentry kind="output" path="bin"/>
    
    <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
    <classpathentry kind="con" path="org.eclipse.jdt.junit.JUNIT_CONTAINER/4"/>
    
    <classpathentry kind="lib" path="external-libs/commons-lang3-3.3.2.jar">
        <attributes>
            <attribute name="javadoc_location" value="jar:platform:/resource/chimple2/external-libs/commons-lang3-3.3.2-javadoc.jar!/"/>
        </attributes>
    </classpathentry>
    <classpathentry kind="lib" path="external-libs/commons-math3-3.3.jar">
        <attributes>
            <attribute name="javadoc_location" value="jar:platform:/resource/chimple2/external-libs/commons-math3-3.3-javadoc.jar!/"/>
        </attributes>
    </classpathentry>
    <classpathentry kind="lib" path="external-libs/jcommon-1.0.21.jar"/>
    <classpathentry kind="lib" path="external-libs/jfreechart-1.0.17.jar"/>
    <classpathentry kind="lib" path="external-libs/jzy3d-api-0.9.1.jar"/>
    <classpathentry kind="lib" path="external-libs/jogl-all.jar">
        <attributes>
            <attribute name="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY" value="chimple2/external-libs"/>
        </attributes>
    </classpathentry>
    <classpathentry kind="lib" path="external-libs/gluegen-rt.jar">
        <attributes>
            <attribute name="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY" value="chimple2/external-libs"/>
        </attributes>
    </classpathentry>
"""

if osname == "Windows":
    notice("Searching for MATLAB JMI library...")
    folders = (glob.glob("%ProgramFiles(x86)%\MATLAB*")
               + glob.glob("%ProgramW6432%\MATLAB*")
               + glob.glob("%ProgramFiles%\MATLAB*"))
    morefolders = folders
    for folder in folders:
        morefolders += glob.glob(folder + "\R*")
    for folder in morefolders:
        path = folder+"\java\jar\jmi.jar"
        if(os.path.exists(path)):
            xml += """<classpathentry kind="lib" path="%s"/>\n"""%path
            notice("Found it at "+path+"!")
            break
    else:
        warning("Could not find MATLAB JMI library; you will have to add it manually.")
    
    notice("Searching for Processing core library...")
    # I don't think Processing has a default install directory on Windows?
    warning("Could not find Processing library; you will have to add it manually.")
    
elif osname == "Darwin":
    home = os.path.expanduser("~")
    
    notice("Searching for MATLAB JMI library...")
    folders = (glob.glob("/Applications/MATLAB*")
               + glob.glob(home+"/Applications/MATLAB*"))
    for folder in folders:
        path = folder+"/java/jar/jmi.jar"
        if(os.path.exists(path)):
            xml += """<classpathentry kind="lib" path="%s"/>\n"""%path
            notice("Found it at "+path+"!")
            break
    else:
        warning("Could not find MATLAB JMI library; you will have to add it manually.")
    
    notice("Searching for Processing core library...")
    folders = (glob.glob("/Applications/Processing.app")
               + glob.glob(home+"/Applications/Processing.app"))
    for folder in folders:
        path = folder+"/Contents/Java/core.jar"
        if(os.path.exists(path)):
            xml += """<classpathentry kind="lib" path="%s"/>\n"""%path
            notice("Found it at "+path+"!")
            break
    else:
        warning("Could not find Processing core library; you will have to add it manually.")
    
else:
    warning("I don't know how to automatically find external libraries "
          + "for your operating system ("+osname+").")
    warning("You will have to configure the Eclipse build path manually "
          + "for these libraries.")
    
xml += "</classpath>"

newline()
notice("Writing .classpath...")
with open(".classpath", "w") as file:
    file.write(xml)
newline()
notice("Done!")
