To run UI version of the melody composition application
1. Unzip the ga-moving-example<VERSION>.zip to a directory.
2. Make sure the JAVA_HOME variable points to a 1.5 JDK or above
3. Make sure the GROOVY_HOME variable points to groovy 1.6 or higher
3. Run the following command from the <PATH_TO_UNZIPPED_ARCHIVE>/classes directory:
groovy --classpath .;../libs/miglayout-3.7-swing.jar;../libs/jgap.jar;../libs/log4j.jar nl/jamiecraane/melodygeneration/ui/App.groovy

To import the source files in your favorite IDE:
1. Unzip the melody-composition<VERSION>.zip to a temporary location
2. Create a new Java project. Make sure a 1.5 JDK or higher is used.
3. import or copy the files in the unzipped src directory to the project's source folder
4. Add the jars in the unzipped libs to the project's classpath
5. compile and run the class with the main method: MoverExample