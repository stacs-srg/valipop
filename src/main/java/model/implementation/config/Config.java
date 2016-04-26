package model.implementation.config;

import model.time.CompoundTimeUnit;
import model.time.TimeClock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Config {

    public static Logger log = LogManager.getLogger(Config.class);

    private TimeClock tS;
    private TimeClock t0;
    private TimeClock tE;

    private CompoundTimeUnit deathTimeStep;
    private CompoundTimeUnit birthTimeStep;

    private static final String ordersBirthSubFile = "ordered_birth";
    private static final String deathSubFile = "death";
    private static final String multipleBirthSubFile = "multiple_birth";
    private static final String partneringSubFile = "partnering";
    private static final String separationSubFile = "separation";

    private Path varBirthPaths;
    private Path varDeathPaths;
    private Path varMultipleBirthPaths;
    private Path varPartneringPaths;
    private Path varSeparationPaths;


    public Config(Path pathToConfigFile) {

        Collection<String> configInput = InputFileReader.getAllLines(pathToConfigFile);

        for(String l : configInput) {

            String[] split = l.split("=");
            for(int i = 0; i < split.length; i++)
                split[i] = split[i].trim();

            String path = split[1];

            switch(split[0]) {
                case "var_data_files":
                    varBirthPaths = Paths.get(path, ordersBirthSubFile);
                    varDeathPaths = Paths.get(path, deathSubFile);
                    varMultipleBirthPaths = Paths.get(path, multipleBirthSubFile);
                    varPartneringPaths = Paths.get(path, partneringSubFile);
                    varSeparationPaths = Paths.get(path, separationSubFile);
                    break;
                case "death_time_step":
                    deathTimeStep = new CompoundTimeUnit(split[1]);
                    break;
                case "birth_time_step":
                    birthTimeStep = new CompoundTimeUnit(split[1]);
                    break;
                case "tS":
                    tS = new TimeClock(split[1]);
                    break;
                case "t0":
                    t0 = new TimeClock(split[1]);
                    break;
                case "tE":
                    tE = new TimeClock(split[1]);
                    break;

            }

        }

    }

    public DirectoryStream<Path> getVarOrderedBirthPaths() {
        try {
            return Files.newDirectoryStream(varBirthPaths);
        } catch (IOException e) {
            log.fatal("Error reading in birth files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarDeathPaths() {
        try {
            return Files.newDirectoryStream(varDeathPaths);
        } catch (IOException e) {
            log.fatal("Error reading in death files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarMultipleBirthPaths() {
        try {
            return Files.newDirectoryStream(varMultipleBirthPaths);
        } catch (IOException e) {
            log.fatal("Error reading in multiple birth files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarPartneringPaths() {
        try {
            return Files.newDirectoryStream(varPartneringPaths);
        } catch (IOException e) {
            log.fatal("Error reading in partnering files. Will now exit.");
            System.exit(1);
        }
        return null;
    }


    public DirectoryStream<Path> getVarSeperationPaths() {
        try {
            return Files.newDirectoryStream(varSeparationPaths);
        } catch (IOException e) {
            log.fatal("Error reading in separation files. Will now exit.");
            System.exit(1);
        }
        return null;
    }

    public TimeClock gettS() {
        return tS;
    }

    public TimeClock getT0() {
        return t0;
    }

    public TimeClock gettE() {
        return tE;
    }

    public CompoundTimeUnit getBirthTimeStep() {
        return birthTimeStep;
    }

    public CompoundTimeUnit getDeathTimeStep() {
        return deathTimeStep;
    }
}
