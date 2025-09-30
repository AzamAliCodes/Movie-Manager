@echo off
javac -cp "lib\gson-2.8.9.jar;lib\java-dotenv-5.2.2.jar;lib\sqlite-jdbc-3.36.0.3.jar;lib\kotlin-stdlib-2.2.20.jar" -d out @java_files.txt
java -cp "out;lib\gson-2.8.9.jar;lib\java-dotenv-5.2.2.jar;lib\sqlite-jdbc-3.36.0.3.jar;lib\kotlin-stdlib-2.2.20.jar" com.moviemanager.Main
