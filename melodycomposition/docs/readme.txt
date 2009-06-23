To run UI version of the melody composition application
1. Unzip the ga-moving-example<VERSION>.zip to a directory.
2. Make sure the JAVA_HOME variable points to a 1.6 JDK or above
3. Make sure the GROOVY_HOME variable points to groovy 1.6 or higher
3. Run the following command from the <PATH_TO_UNZIPPED_ARCHIVE>/classes directory:
groovy --classpath .;../libs/miglayout-3.7-swing.jar;../libs/jgap.jar;../libs/log4j.jar nl/jamiecraane/melodygeneration/ui/App.groovy
