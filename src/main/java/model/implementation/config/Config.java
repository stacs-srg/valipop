package model.implementation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Config {

    public static Logger log = LogManager.getLogger(Config.class);

    private static final String birthSubFile = "birth";
    private static final String deathSubFile = "death";
    private static final String multipleBirthSubFile = "multiple_birth";
    private static final String partneringSubFile = "partnering";
    private static final String separationSubFile = "separation";

    private Path varBirthFiles;
    private Path varDeathFiles;
    private Path varMultipleBirthFiles;
    private Path varPartneringFiles;
    private Path varSeparationFiles;

//    private

    public DirectoryStream<Path> getVarBirthFiles() {
        try {
            return Files.newDirectoryStream(varBirthFiles);
        } catch (IOException e) {
            log.fatal("Error reading in birth files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarDeathFiles() {
        try {
            return Files.newDirectoryStream(varDeathFiles);
        } catch (IOException e) {
            log.fatal("Error reading in death files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarMultipleBirthFiles() {
        try {
            return Files.newDirectoryStream(varMultipleBirthFiles);
        } catch (IOException e) {
            log.fatal("Error reading in multiple birth files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarPartneringFiles() {
        try {
            return Files.newDirectoryStream(varPartneringFiles);
        } catch (IOException e) {
            log.fatal("Error reading in partnering files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarSeperationFiles() {
        try {
            return Files.newDirectoryStream(varSeparationFiles);
        } catch (IOException e) {
            log.fatal("Error reading in separation files. Will now exit.");
            System.exit(1);
        }
        return null;
    }

    public Config(String pathToConfigFile) {

        String[] configInput = InputFileReader.getAllLines(pathToConfigFile);

        for(String l : configInput) {

            String[] split = l.split("=");
            for(int i = 0; i < split.length; i++)
                split[i] = split[i].trim();

            String path = split[1];

            switch(split[0]) {
                case "var_data_files":
                    varBirthFiles = Paths.get(path, birthSubFile);
                    varDeathFiles = Paths.get(path, deathSubFile);
                    varMultipleBirthFiles = Paths.get(path, multipleBirthSubFile);
                    varPartneringFiles = Paths.get(path, partneringSubFile);
                    varSeparationFiles = Paths.get(path, separationSubFile);
                    break;
                case "death_time_step":

                    break;
                case "birth_time_step":

                    break;
            }

        }

    }

}
