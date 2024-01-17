# HindlimbClasping

Download [JDK 17 or later](http://jdk.java.net/) for your operating system.
Make sure `JAVA_HOME` is properly set to the JDK installation directory. 

## Modular - Maven

`HindlimbClasping` is modular project to run with Maven

### Linux / Mac

If you run on Linux or Mac, follow these steps:

    cd HindlimbClasping
    mvn clean javafx:run

To create and run a custom JRE:

    cd HindlimbClasping
    mvn clean javafx:jlink
    target/HindlimbClasping/bin/launcher

### Windows

If you run on Windows, follow these steps:

    cd HindlimbClasping
    mvn clean javafx:run

To create and run a custom JRE:

    cd HindlimbClasping
    mvn clean javafx:jlink
    target\HindlimbClasping\bin\launcher