Simple SensorML Generator
Carlos Rueda   MMI / MBARI
2009-09-25

A simple protoype to generate SensorML from a basic set of user-given attributes and
definitions from the MMI ORR.

NOTE: This project depends on Google Web Toolkit version 1.5.x  (Newer versions may work
but haven't been tested.)  You will need to install it from
   http://code.google.com/webtoolkit/versions.html

- Building with Ant

Use sample.build.properties to create your own build.properties file.
NOTE: Do not check in your build.properties copy. The sample.build.properties file is 
the one being maintained under version control.

Edit your build.properties to adjust any necessary properties. For example, indicate the
location of your GWT installation in the GWT_HOME property. See the file for more details.

Building the WAR:
	ant war
Running in hosted mode:
	ant shell

Note that there is no Ant target to deploy the war.

version.properties: this file determines the version of the tool. The string there is used
by build.xml to update some dependent resources.


- Building with Eclipse

Adjust the build path for gwt-user.jar so it points to the actual library in your system.
This should resolve any pending symbols for a successful compilation.

Running SmlMor from within Eclipse:
  Edit smlmor.launch to adjust any GWT related library so it points to the correct location
  in your system. (Search for gwt-*.jar to quickly determine which parts require adjusment)
  Then right-click smlmor.launch and select Run As -> smlmor
  Select Debug As -> smlmor for debugging.

