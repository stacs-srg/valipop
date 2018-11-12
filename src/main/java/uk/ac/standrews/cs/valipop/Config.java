/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop;

import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InputFileReader;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.exceptions.InvalidTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.DateTimeException;
import java.util.Collection;

/**
 * This class provides the configuration for the Simulation model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Config {

    private static final String birthSubFile = "birth";
    private static final String orderedBirthSubFile = "ordered_birth";
    private static final String multipleBirthSubFile = "multiple_birth";
    private static final String illegitimateBirthSubFile = "illegitimate_birth";
    private static final String birthRatioSubFile = "ratio_birth";

    private static final String relationshipsSubFile = "relationships";
    private static final String partneringSubFile = "partnering";
    private static final String separationSubFile = "separation";
    private static final String marriageSubFile = "marriage";

    private static final String deathSubFile = "death";
    private static final String maleDeathSubFile = "males";
    private static final String femaleDeathSubFile = "females";
    private static final String lifetableSubFile = "lifetable";
    private static final String deathCauseSubFile = "cause";

    private static final String annotationsSubFile = "annotations";
    private static final String maleForenameSubFile = "male_forename";
    private static final String femaleForenameSubFile = "female_forename";
    private static final String surnameSubFile = "surname";
    private static final String occupationSubFile = "occupation";
    private static final String geographySubFile = "geography";
    private static final String locationsSubFile = "locations";
    private static final String migrationSubFile = "migration";

    public static final Logger log = new Logger(Config.class);

    private MonthDate tS;
    private MonthDate t0;
    private MonthDate tE;
    private int t0PopulationSize;
    private double setUpBR;
    private double setUpDR;
    private CompoundTimeUnit simulationTimeStep;

    private String varPath;
    private Path varOrderedBirthPaths;
    private Path varMaleLifetablePaths;
    private Path varMaleDeathCausesPaths;
    private Path varFemaleLifetablePaths;
    private Path varFemaleDeathCausesPaths;
    private Path varMultipleBirthPaths;
    private Path varIllegitimateBirthPaths;
    private Path varPartneringPaths;
    private Path varSeparationPaths;
    private Path varBirthRatioPaths;
    private Path varMaleForenamePaths;
    private Path varFemaleForenamePaths;
    private Path varSurnamePaths;
    private Path varMarriagePaths;

    private Path resultsSavePath;

    private final String runPurpose;

    private int minBirthSpacing;
    private int minGestationPeriodDays;

    private double birthFactor;
    private double deathFactor;
    private double recoveryFactor;
    private double proportionalRecoveryFactor;
    private CompoundTimeUnit inputWidth;

    private RecordFormat outputRecordFormat;
    private boolean outputTables = true;

    private final String startTime;

    private boolean binomialSampling = true;
    private int seed = 0;
    private boolean deterministic = false;

    // Filter method to exclude dot files from data file directory streams
    private DirectoryStream.Filter<Path> filter = file -> {
        Path path = file.getFileName();
        if(path != null) {
            return !path.toString().matches("^\\..+");
        }
        throw new IOException("Failed to get Filename");
    };

    public Config(AdvanceableDate tS, AdvanceableDate t0, AdvanceableDate tE, int t0PopulationSize, double setUpBR, double setUpDR,
                  CompoundTimeUnit simulationTimeStep, String varPath, String resultsSavePath, final String runPurpose,
                  int minBirthSpacing, int minGestationPeriodDays, boolean binomialSampling,
                  double birthFactor, double deathFactor, double recoveryFactor, double proportionalRecoveryFactor,
                  CompoundTimeUnit inputWidth, RecordFormat outputRecordFormat, String startTime, int seed, boolean deterministic) {

        initialiseVarPaths(varPath);

        this.resultsSavePath = Paths.get(resultsSavePath);

        this.tS = tS.getMonthDate();
        this.t0 = t0.getMonthDate();
        this.tE = tE.getMonthDate();
        this.t0PopulationSize = t0PopulationSize;
        this.setUpBR = setUpBR;
        this.setUpDR = setUpDR;
        this.simulationTimeStep = simulationTimeStep;
        this.binomialSampling = binomialSampling;
        this.runPurpose = runPurpose;
        this.minBirthSpacing = minBirthSpacing;
        this.minGestationPeriodDays = minGestationPeriodDays;
        this.birthFactor = birthFactor;
        this.deathFactor = deathFactor;
        this.recoveryFactor = recoveryFactor;
        this.proportionalRecoveryFactor = proportionalRecoveryFactor;
        this.inputWidth = inputWidth;
        this.outputRecordFormat = outputRecordFormat;
        this.startTime = startTime;
        this.deterministic = deterministic;
    }

    /**
     * This constructor reads in the file at the given path and stores the given configuration.
     *
     * @param pathToConfigFile The path to the location of the configuration file
     * @throws InvalidTimeUnit
     * @throws DateTimeException
     * @throws NumberFormatException
     */
    public Config(Path pathToConfigFile, String runPurpose, String startTime) throws InvalidTimeUnit, DateTimeException, NumberFormatException, IOException {

        this.runPurpose = runPurpose;
        this.startTime = startTime;

        Collection<String> configInput = InputFileReader.getAllLines(pathToConfigFile);

        // Iterate over config file
        for (String l : configInput) {

            String[] split = l.split("=");

            for (int i = 0; i < split.length; i++)
                split[i] = split[i].trim();

            String path = split[1];

            switch (split[0]) {
                case "var_data_files":
                    initialiseVarPaths(path);
                    break;
                case "results_save_location":
                    resultsSavePath = Paths.get(split[1]);
                    break;
                case "simulation_time_step":
                    try {
                        simulationTimeStep = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("simulation_time_step " + e.getMessage());
                        throw e;
                    }
                    break;
                case "input_width":
                    try {
                        inputWidth = new CompoundTimeUnit(split[1]);
                    } catch (InvalidTimeUnit e) {
                        log.fatal("input_width " + e.getMessage());
                        throw e;
                    }
                    break;
                case "tS":
                    try {
                        tS = new MonthDate(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("tS " + e.getMessage());
                        throw e;
                    }
                    break;
                case "t0":
                    try {
                        t0 = new MonthDate(split[1]);
                    } catch (DateTimeException e) {
                        log.fatal("t0 " + e.getMessage());
                        throw e;
                    }
                    break;
                case "tE":
                    try {
                        tE = new MonthDate(split[1]);
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
                        log.fatal("set_up_br " + e.getMessage());
                        throw e;
                    }
                    break;
                case "set_up_dr":
                    try {
                        setUpDR = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("set_up_dr " + e.getMessage());
                        throw e;
                    }
                    break;
                case "min_birth_spacing":
                    try {
                        minBirthSpacing = Integer.parseInt(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("min_birth_spacing " + e.getMessage());
                        throw e;
                    }
                    break;
                case "min_gestation_period":
                    try {
                        minGestationPeriodDays = Integer.parseInt(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("min_gestation_period " + e.getMessage());
                        throw e;
                    }
                    break;
                case "binominal_sampling":
                    binomialSampling = split[1].toLowerCase().equals("true");
                    break;
                case "birth_factor":
                    try {
                        birthFactor = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("birth_factor " + e.getMessage());
                        throw e;
                    }
                    break;
                case "death_factor":
                    try {
                        deathFactor = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("death_factor " + e.getMessage());
                        throw e;
                    }
                    break;
                case "recovery_factor":
                    try {
                        recoveryFactor = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("recovery_factor " + e.getMessage());
                        throw e;
                    }
                    break;
                case "proportional_recovery_factor":
                    try {
                        proportionalRecoveryFactor = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        log.fatal("proportional_recovery_factor " + e.getMessage());
                        throw e;
                    }
                    break;
                case "output_record_format":
                    switch(split[1]) {
                        case "DS":
                            outputRecordFormat = RecordFormat.DS;
                            break;
                        case "VIS_PROCESSING":
                            outputRecordFormat = RecordFormat.VIS_PROCESSING;
                            break;
                        case "EG_SKYE":
                            outputRecordFormat = RecordFormat.EG_SKYE;
                            break;
                        case "NONE":
                            outputRecordFormat = RecordFormat.NONE;
                            break;
                        default:
                            String m = "output_record_format - given option not recognised";
                            log.fatal(m);
                            throw new InvalidParameterException(m);
                    }
                case "output_tables":
                    outputTables = split[1].toLowerCase().equals("true");
                    break;
                case "deterministic":
                    deterministic = split[1].toLowerCase().equals("true");
                    break;
            }
        }
    }

    private void initialiseVarPaths(String path) {

        varPath = path;
        varOrderedBirthPaths = Paths.get(path, birthSubFile, orderedBirthSubFile);
        varMultipleBirthPaths = Paths.get(path, birthSubFile, multipleBirthSubFile);
        varIllegitimateBirthPaths = Paths.get(path, birthSubFile, illegitimateBirthSubFile);
        varBirthRatioPaths = Paths.get(path, birthSubFile, birthRatioSubFile);

        String deathSubPath = Paths.get(path, deathSubFile).toString();
        varMaleLifetablePaths = Paths.get(deathSubPath, maleDeathSubFile, lifetableSubFile);
        varMaleDeathCausesPaths = Paths.get(deathSubPath, maleDeathSubFile, deathCauseSubFile);
        varFemaleLifetablePaths = Paths.get(deathSubPath, femaleDeathSubFile, lifetableSubFile);
        varFemaleDeathCausesPaths = Paths.get(deathSubPath, femaleDeathSubFile, deathCauseSubFile);

        String relationshipSubPath = Paths.get(path, relationshipsSubFile).toString();
        varPartneringPaths = Paths.get(relationshipSubPath, partneringSubFile);
        varSeparationPaths = Paths.get(relationshipSubPath, separationSubFile);
        varMarriagePaths = Paths.get(relationshipSubPath, marriageSubFile);

        String annotationsSubPath = Paths.get(path, annotationsSubFile).toString();
        varMaleForenamePaths = Paths.get(annotationsSubPath, maleForenameSubFile);
        varFemaleForenamePaths = Paths.get(annotationsSubPath, femaleForenameSubFile);
        varSurnamePaths = Paths.get(annotationsSubPath, surnameSubFile);
    }

    public String getVarPath() {
        return varPath;
    }

    public DirectoryStream<Path> getVarOrderedBirthPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varOrderedBirthPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in birth files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarMaleLifetablePaths() throws IOException {
        try {
            return Files.newDirectoryStream(varMaleLifetablePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in male death files: " + varMaleLifetablePaths;
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarMaleDeathCausesPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varMaleDeathCausesPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in male death files: " + varMaleDeathCausesPaths;
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarFemaleLifetablePaths() throws IOException {
        try {
            return Files.newDirectoryStream(varFemaleLifetablePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in female death files: " + varFemaleLifetablePaths;
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarFemaleDeathCausesPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varFemaleDeathCausesPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in male death files: " + varFemaleDeathCausesPaths;
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

    public DirectoryStream<Path> getVarIllegitimateBirthPaths() throws IOException {
        try {
            return Files.newDirectoryStream(varIllegitimateBirthPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in illegitimate birth files";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarMarriagePaths() throws IOException {
        try {
            return Files.newDirectoryStream(varMarriagePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in marriage files";
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

    public DirectoryStream<Path> getVarBirthRatioPath() throws IOException {
        try {
            return Files.newDirectoryStream(varBirthRatioPaths, filter);
        } catch (IOException e) {
            String message = "Error reading in birth ratio file";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarMaleForenamePath() throws IOException {
        try {
            return Files.newDirectoryStream(varMaleForenamePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in male forename file";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarFemaleForenamePath() throws IOException {
        try {
            return Files.newDirectoryStream(varFemaleForenamePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in female forename file";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public DirectoryStream<Path> getVarSurnamePath() throws IOException {
        try {
            return Files.newDirectoryStream(varSurnamePaths, filter);
        } catch (IOException e) {
            String message = "Error reading in surname file";
            log.fatal(message);
            throw new IOException(message, e);
        }
    }

    public MonthDate getTS() {
        return tS;
    }

    public MonthDate getT0() {
        return t0;
    }

    public MonthDate getTE() {
        return tE;
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

    public Path getResultsSavePath() {
        return resultsSavePath;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getRunPurpose() {
        return runPurpose;
    }

    public CompoundTimeUnit getInputWidth() {
        return inputWidth;
    }

    public boolean getBinomialSampling() {
        return binomialSampling;
    }

    public int getMinBirthSpacing() {
        return minBirthSpacing;
    }

    public double getBirthFactor() {
        return birthFactor;
    }

    public double getDeathFactor() {
        return deathFactor;
    }

    public double getRecoveryFactor() {
        return recoveryFactor;
    }

    public double getProportionalRecoveryFactor() {
        return proportionalRecoveryFactor;
    }

    public RecordFormat getOutputRecordFormat() {
        return outputRecordFormat;
    }

    public boolean getOutputTables() {
        return outputTables;
    }

    public int getMinGestationPeriodDays() {
        return minGestationPeriodDays;
    }

    public int getSeed() {
        return seed;
    }

    public boolean deterministic() {
        return deterministic;
    }
}
