
#  `classli` Installation on Linux

The following steps describes how to install `classli` on a Mac OS X operating system:

1. Install [JRE](https://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jre.html#CFHBJIIG) or [JDK](https://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html#BJFGGEFG) version 8.0 or higher.

    - Open a new Bash Shell window.
    - To make sure Java is available, in the opened terminal window type `java -version` and press enter; you should see the version and build number of Java.
    - If not, make sure Java installation directory is added to `PATH` environment variable, see [this](https://java.com/en/download/help/path.xml) guide for more information.

2. Download the `classli.zip` which contains the Linux binary from [here](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/lastSuccessfulBuild/artifact/record_classification/target/classli.zip).

3. Extract the downloaded zip file.
   
4. Place the extracted `classli` binary in `/usr/local/bin` folder. 

    - Make sure the `/usr/local/bin` is present in `PATH` environment variable.
    - If you do not have permissions to copy files into `/usr/local/bin` folder, or you prefer not to, then:
        - place the downloaded `classli` binary into a folder of your choice, and
        - add the folder's absolute path to the `PATH` environment variable.

5. To make sure all is well, open a new Bash Shell window and type `classli -h`, which should print the `classli` usage.
