package config;

import model.time.CompoundTimeUnit;
import model.time.InvalidTimeUnit;
import model.time.DateClock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.InputFileReader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.Collection;

/**
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


    public Config(Path pathToConfigFile) {

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
                case "death_time_step":
                    try {
                        deathTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("Invalid time unit specified for death timestep");
                        System.exit(3);
                    }
                    break;
                case "birth_time_step":
                    try {
                        birthTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("Invalid time unit specified for birth timestep");
                        System.exit(3);
                    }
                    break;
                case "simulation_time_step":
                    try {
                        simulationTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("Invalid time unit specified for simulation timestep");
                        System.exit(3);
                    }
                    break;
                case "tS":
                    try {
                        tS = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("Invalid Fate format for tS: " + e.getMessage());
                        System.exit(3);
                    }
                    break;
                case "t0":
                    try {
                        t0 = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("Invalid Fate format for t0: " + e.getMessage());
                        System.exit(3);
                    }
                    break;
                case "tE":
                    try {
                        tE = new DateClock(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("Invalid Fate format for tE: " + e.getMessage());
                        System.exit(3);
                    }
                    break;
                case "t0_pop_size":
                    try {
                        t0PopulationSize = Integer.parseInt(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("t0 Population size not a valid integer");
                        System.exit(3);
                    }
                    break;
                case "set_up_br":
                    try {
                        setUpBR = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("set up birth rate not a valid number");
                        System.exit(3);
                    }
                    break;
                case "set_up_dr":
                    try {
                        setUpDR = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("set up death rate not a valid number");
                        System.exit(3);
                    }
                    break;


            }

        }

    }

    public boolean checkConfigValid() {

        // Things to check
        // The initial date will line up with the time steps

        return false;
    }

    private DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
        public boolean accept(Path file) throws IOException {
            return !file.getFileName().toString().matches("^\\..+");
        }
    };

    public DirectoryStream<Path> getVarOrderedBirthPaths() {
        try {
            return Files.newDirectoryStream(varBirthPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in birth files. Will now exit.");
            System.exit(101);
        }
        return null;
    }


    public DirectoryStream<Path> getVarMaleDeathPaths() {
        try {
            return Files.newDirectoryStream(varMaleDeathPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in male death files. Will now exit.");
            System.exit(101);
        }
        return null;
    }

    public DirectoryStream<Path> getVarFemaleDeathPaths() {
        try {
            return Files.newDirectoryStream(varFemaleDeathPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in female death files. Will now exit.");
            System.exit(101);
        }
        return null;
    }


    public DirectoryStream<Path> getVarMultipleBirthPaths() {
        try {
            return Files.newDirectoryStream(varMultipleBirthPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in multiple birth files. Will now exit.");
            System.exit(101);
        }
        return null;
    }


    public DirectoryStream<Path> getVarPartneringPaths() {
        try {
            return Files.newDirectoryStream(varPartneringPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in partnering files. Will now exit.");
            System.exit(101);
        }
        return null;
    }


    public DirectoryStream<Path> getVarSeparationPaths() {
        try {
            return Files.newDirectoryStream(varSeparationPaths, filter);
        } catch (IOException e) {
            log.fatal("Error reading in separation files. Will now exit.");
            System.exit(101);
        }
        return null;
    }

    public DateClock gettS() {
        return tS;
    }

    public DateClock getT0() {
        return t0;
    }

    public DateClock gettE() {
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
}
