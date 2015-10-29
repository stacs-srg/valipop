# `classli` Installation on Mac OS X

The following steps describes how to install `classli` on a Mac OS X operating system:

1. Install [JRE](https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jre.html#CHDGECEB) or [JDK](https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html#CHDBADCG) version 8.0 or higher.
    
    - See [this FAQ](https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_install_faq.html#CHDJEDDB) for more information.
    - Open the terminal app located in `/Applications/Utilities/Terminal.app`.
    - To make sure Java is available, in the opened terminal window type `java -version` and press enter; you should see the version and build number of Java.
    - If not, make sure Java installation directory is added to `PATH` environment variable, see [this](https://java.com/en/download/help/path.xml) guide for more information.

2. Download the `classli.zip` which contains the Mac OS X binary from [here](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/lastSuccessfulBuild/artifact/record_classification/target/classli.zip).

3. Extract the downloaded zip file.
   
4. Place the extracted `classli` binary in `/usr/local/bin` folder. 

    - Make sure the `/usr/local/bin` is present in `PATH` environment variable.
    - If you do not have permissions to copy files into `/usr/local/bin` folder, or you prefer not to, then:
        - place the downloaded `classli` binary into a folder of your choice, and
        - add the folder's absolute path to the `PATH` environment variable.

5. To make sure all is well, open a new terminal window and type `classli -h`, which should print the `classli` usage.

