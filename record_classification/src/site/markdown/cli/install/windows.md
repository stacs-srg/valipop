# `classli` Installation on Windows OS

The following steps describes how to install `classli` on a Windows operating system:

1. Install [JRE](https://docs.oracle.com/javase/8/docs/technotes/guides/install/windows_jre_install.html#CHDEDHAJ) or [JDK](https://docs.oracle.com/javase/8/docs/technotes/guides/install/windows_jdk_install.html#CHDEBCCJ) version 8.0 or higher.

    - Checkout [Windows System Requirements](https://docs.oracle.com/javase/8/docs/technotes/guides/install/windows_system_requirements.html#BABHGIJF) prior to JDK or JRE installation.
    - [Open a Command Prompt window](http://windows.microsoft.com/en-gb/windows-vista/open-a-command-prompt-window)
    - To make sure Java is available, in the Command Prompt window type `java -version` and press enter; you should see the version and build number of Java.
    - If not, make sure Java installation directory is added to `PATH` environment variable, see [this](https://java.com/en/download/help/path.xml) guide for more information.

2. Download the `classli` Windows binary from [here](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/lastSuccessfulBuild/artifact/record_classification/target/classli.exe).

3. Place the downloaded `classli` binary in `%SystemRoot%\system32` folder, where `%SystemRoot%` is the directory in which Windows was installed. 

    - If you do not know where that is,type `echo %SystemRoot%` in the Command Prompt window and press enter to print the path to the _systemroot_ folder. 
    - If you do not have permissions to copy files into that folder, or you prefer not to, then:
        - place the downloaded `classli` binary into a folder of your choice, and
        - add the folder's absolute path to the `PATH` environment variable.  

4. If you have modified the `PATH` environment variable, restart the computer for changes to take effect.

5. To make sure all is well, open a Command Prompt window and type `classli -h`, which should print the `classli` usage.

