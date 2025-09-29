@echo off
javac -cp "lib\gson-2.8.9.jar;lib\java-dotenv-5.2.2.jar;lib\sqlite-jdbc-3.36.0.3.jar;lib\kotlin-stdlib-2.2.20.jar" -d out src\com\moviemanager\*.java src\com\moviemanager\api\*.java src\com\moviemanager\dao\*.java src\com\moviemanager\db\*.java src\com\moviemanager\model\*.java src\com\moviemanager\theme\*.java src\com\moviemanager\ui\*.java
java -cp "out;lib\gson-2.8.9.jar;lib\java-dotenv-5.2.2.jar;lib\sqlite-jdbc-3.36.0.3.jar;lib\kotlin-stdlib-2.2.20.jar" com.moviemanager.Main
