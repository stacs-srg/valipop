package config;

import utils.time.CompoundTimeUnit;
import utils.time.InvalidTimeUnit;
import utils.time.DateClock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.Collection;

/**
 * This class provides the configuration for the Simulation model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Config {

    private static final String ordersBirthSubFile = "ordered_birth";
    private static final String deathSubFile = "death";
    private static final String maleDeathSubFile = "males";
    private static final String femaleDeathSubFile = "females";
    private static final String multipleBirthSubFile = "multiple_birth";
    private static final String partneringSubFile = "partnering";
    private static final String separationSubFile = "separation";
    public static Logger log = LogManager.getLogger(Config.class);
    private DateClock tS;
    private DateClock t0;
    private DateClock tE;
    private int t0PopulationSize;
    private double setUpBR;
    private double setUpDR;
    private CompoundTimeUnit deathTimeStep;
    private CompoundTimeUnit birthTimeStep;
    private CompoundTimeUnit simulationTimeStep;
    private Path varBirthPaths;
    private Path varMaleDeathPaths;
    private Path varFemaleDeathPaths;
    private Path varMultipleBirthPaths;
    private Path varPartneringPaths;
    private Path varSeparationPaths;
    private Path savePath;

    // Filter method to exclude dot files from data file directory streams
    private DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
        public boolean accept(Path file) throws IOException {
            return !file.getFileName().toString().matches("^\\..+");
        }
    };

    /**
     * This constructor reads in the file at the given path and stores the given configuration.
     *
     * @param pathToConfigFile The path to the location of the configuration file
     * @throws InvalidTimeUnit
     * @throws DateTimeException
     * @throws NumberFormatException
     */
    public Config(Path pathToConfigFile) throws InvalidTimeUnit, DateTimeException, NumberFormatException {

        Collection<String> configInput = InputFileReader.getAllLines(pathToConfigFile);

        for (String l : configInput) {

            String[] split = l.split("=");
            for (int i = 0; i < split.length; i++)
                split[i] = split[i].trim();

            String path = split[1];

            switch (split[0]) {
                case "var_data_files":
                    varBirthPaths = Paths.get(path, ordersBirthSubFile);
                    String deathSubPath = Paths.get(path, deathSubFile).toString();
                    varMaleDeathPaths = Paths.get(deathSubPath, maleDeathSubFile);
                    varFemaleDeathPaths = Paths.get(deathSubPath, femaleDeathSubFile);
                    varMultipleBirthPaths = Paths.get(path, multipleBirthSubFile);
                    varPartneringPaths = Paths.get(path, partneringSubFile);
                    varSeparationPaths = Paths.get(path, separationSubFile);
                    break;
                case "save_location":
                    savePath = Paths.get(split[1]);
                    break;
                case "death_time_step":
                    try {
                        deathTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("death_time_step " + e.getMessage());
                        throw e;
                    }
                    break;
                case "birth_time_step":
                    try {
                        birthTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("birth_time_step " + e.getMessage());
                        throw e;
                    }
                    break;
                case "simulation_time_step":
                    try {
                        simulationTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("simulation_time_step " + e.getMessage());
                        throw e;
                    }
                    break;
                case "tS":
                    try {
                        tS = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("tS " + e.getMessage());
                        throw e;
                    }
                    break;
                case "t0":
                    try {
                        t0 = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("t0 " + e.getMessage());
                        throw e;
                    }
                    break;
                case "tE":
                    try {
                        tE = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("tE " + e.getMessage());
                        throw e;
                    }
                    break;
                case "t0_pop_size":
                    try {
                        t0PopulationSize = Integer.parseInt(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("t0_pop_size " + e.getMessage());
                        throw e;
                    }
                    break;
                case "set_up_br":
                    try {
                        setUpBR = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("set_up _br " + e.getMessage());
                        throw e;
                    }
                    break;
                case "set_up_dr":
                    try {
                        setUpDR = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("set_up _dr " + e.getMessage());
                        throw e;
                    }
                    break;


            }

        }

    }

    public boolean checkConfigValid() {

        // Things to check
        // The initial date will line up with the utils.time steps

        return false;
    }

    public DirectoryStream<Path> getVarOrderedBirthPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varBirthPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in birth files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }


    public DirectoryStream<Path> getVarMaleDeathPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varMaleDeathPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in male death files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarFemaleDeathPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varFemaleDeathPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in female death files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }


    public DirectoryStream<Path> getVarMultipleBirthPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varMultipleBirthPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in multiple birth files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }


    public DirectoryStream<Path> getVarPartneringPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varPartneringPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in partnering files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }


    public DirectoryStream<Path> getVarSeparationPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varSeparationPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in separation files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }


    public DateClock getTS() {
        return tS;
    }

    public DateClock getT0() {
        return t0;
    }

    public DateClock getTE() {
        return tE;
    }

    public CompoundTimeUnit getBirthTimeStep() {
        return birthTimeStep;
    }

    public CompoundTimeUnit getDeathTimeStep() {
        return deathTimeStep;
    }

    public CompoundTimeUnit getSimulationTimeStep() {
        return simulationTimeStep;
    }

    public int getT0PopulationSize() {
        return t0PopulationSize;
    }

    public double getSetUpBR() {
        return setUpBR;
    }

    public double getSetUpDR() {
        return setUpDR;
    }

    public Path getSavePath() {
        return savePath;
    }
}
