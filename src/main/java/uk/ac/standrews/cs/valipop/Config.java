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
    private boolean deterministic = false;

    // Filter method to exclude dot files from data file directory streams
    private DirectoryStream.Filter<Path> filter = file -> {
        Path path = file.getFileName();
        if (path != null) {
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
    public Config(Path pathToConfigFile, String runPurpose, String startTime) {

        try {
            this.runPurpose = runPurpose;
            this.startTime = startTime;

            for (String line : InputFileReader.getAllLines(pathToConfigFile)) {

                String[] split = line.split("=");

                for (int i = 0; i < split.length; i++) {
                    split[i] = split[i].trim();
                }

                switch (split[0]) {

                    case "var_data_files":
                        initialiseVarPaths(split[1]);
                        break;

                    case "results_save_location":
                        resultsSavePath = Paths.get(split[1]);
                        break;

                    case "simulation_time_step":

                        simulationTimeStep = new CompoundTimeUnit(split[1]);
                        break;

                    case "input_width":
                        inputWidth = new CompoundTimeUnit(split[1]);
                        break;

                    case "tS":
                        tS = new MonthDate(split[1]);
                        break;

                    case "t0":
                        t0 = new MonthDate(split[1]);
                        break;

                    case "tE":
                        tE = new MonthDate(split[1]);
                        break;

                    case "t0_pop_size":
                        t0PopulationSize = Integer.parseInt(split[1]);
                        break;

                    case "set_up_br":
                        setUpBR = Double.parseDouble(split[1]);
                        break;

                    case "set_up_dr":
                        setUpDR = Double.parseDouble(split[1]);
                        break;

                    case "min_birth_spacing":
                        minBirthSpacing = Integer.parseInt(split[1]);
                        break;

                    case "min_gestation_period":
                        minGestationPeriodDays = Integer.parseInt(split[1]);
                        break;

                    case "binominal_sampling":
                        binomialSampling = split[1].toLowerCase().equals("true");
                        break;

                    case "birth_factor":
                        birthFactor = Double.parseDouble(split[1]);
                        break;

                    case "death_factor":
                        deathFactor = Double.parseDouble(split[1]);
                        break;

                    case "recovery_factor":
                        recoveryFactor = Double.parseDouble(split[1]);
                        break;

                    case "proportional_recovery_factor":
                        proportionalRecoveryFactor = Double.parseDouble(split[1]);
                        break;

                    case "output_record_format":
                        switch (split[1]) {
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
                        break;

                    case "output_tables":
                        outputTables = split[1].toLowerCase().equals("true");
                        break;

                    case "deterministic":
                        deterministic = split[1].toLowerCase().equals("true");
                        break;
                }
            }
        } catch (IOException e) {
            log.fatal("error reading config: " + e.getMessage());
            throw new RuntimeException(e);
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

    private DirectoryStream<Path> getDirectories(Path path) {

        try {
            return Files.newDirectoryStream(path, filter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DirectoryStream<Path> getVarOrderedBirthPaths() {
        return getDirectories(varOrderedBirthPaths);
    }

    public DirectoryStream<Path> getVarMaleLifetablePaths() {
        return getDirectories(varMaleLifetablePaths);
    }

    public DirectoryStream<Path> getVarMaleDeathCausesPaths() {
        return getDirectories(varMaleDeathCausesPaths);
    }

    public DirectoryStream<Path> getVarFemaleLifetablePaths() {
        return getDirectories(varFemaleLifetablePaths);
    }

    public DirectoryStream<Path> getVarFemaleDeathCausesPaths() {
        return getDirectories(varFemaleDeathCausesPaths);
    }

    public DirectoryStream<Path> getVarMultipleBirthPaths() {
        return getDirectories(varMultipleBirthPaths);
    }

    public DirectoryStream<Path> getVarIllegitimateBirthPaths() {
        return getDirectories(varIllegitimateBirthPaths);
    }

    public DirectoryStream<Path> getVarMarriagePaths() {
        return getDirectories(varMarriagePaths);
    }

    public DirectoryStream<Path> getVarPartneringPaths() {
        return getDirectories(varPartneringPaths);
    }

    public DirectoryStream<Path> getVarSeparationPaths() {
        return getDirectories(varSeparationPaths);
    }

    public DirectoryStream<Path> getVarBirthRatioPath() {
        return getDirectories(varBirthRatioPaths);
    }

    public DirectoryStream<Path> getVarMaleForenamePath() {
        return getDirectories(varMaleForenamePaths);
    }

    public DirectoryStream<Path> getVarFemaleForenamePath() {
        return getDirectories(varFemaleForenamePaths);
    }

    public DirectoryStream<Path> getVarSurnamePath() {
        return getDirectories(varSurnamePaths);
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
        int seed = 0;
        return seed;
    }

    public boolean deterministic() {
        return deterministic;
    }
}
