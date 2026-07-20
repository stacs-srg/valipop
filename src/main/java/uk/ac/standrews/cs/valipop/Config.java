/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop;

import uk.ac.standrews.cs.valipop.exporting.PopulationExportFormat;
import uk.ac.standrews.cs.valipop.population.SerializableConfig;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.utils.InputFileReader;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordExportFormat;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.*;

/**
 * This class provides the configuration for the Simulation model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
@SuppressWarnings("UnusedReturnValue")
public class Config implements Serializable {

    // ---- Constants ----
    private static final Level DEFAULT_LOG_LEVEL = Level.SEVERE;

    private static final boolean DEFAULT_BINOMIAL_SAMPLING_FLAG = true;
    private static final boolean DEFAULT_DETERMINISTIC_FLAG = false;
    private static final boolean DEFAULT_EXPORT_CONTINGENCY_TABLES_FLAG = false;
    private static final boolean DEFAULT_EXPORT_RECORDS_FLAG = false;
    private static final boolean DEFAULT_EXPORT_POPULATION_FLAG = false;

    private static final double DEFAULT_INITIALISATION_BIRTH_RATE = 0.0133;
    private static final double DEFAULT_INITIALISATION_DEATH_RATE = 0.0122;
    private static final double DEFAULT_RECOVERY_FACTOR = 1.0;
    private static final double DEFAULT_PROPORTIONAL_RECOVERY_FACTOR = 1.0;
    private static final double DEFAULT_OVERSIZED_GEOGRAPHY_FACTOR = 1.0;

    private static final Period DEFAULT_SIMULATION_TIME_STEP = Period.ofYears(1);
    private static final Period DEFAULT_DISTRIBUTION_GRANULARITY = Period.ofYears(1);
    private static final Period DEFAULT_MIN_BIRTH_SPACING = Period.ofDays(147);
    private static final Period DEFAULT_MIN_GESTATION_PERIOD = Period.ofDays(147);

    private static final int DEFAULT_SEED = 56854687;
    private static final int DEFAULT_CONTINGENCY_TABLE_STEPBACK = 1;
    private static final double DEFAULT_CONTINGENCY_TABLE_PRECISION = 1E-66;

    private static final String DEFAULT_GROUP_NAME = "default";

    // Input directory structure
    private static final String birthSubFile = "birth";
    private static final String orderedBirthSubFile = "ordered_birth";
    private static final String multipleBirthSubFile = "multiple_birth";
    private static final String adulterousBirthSubFile = "adulterous_birth";
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
    private static final String maleMigrantForenameSubFile = "migration/male_forename";
    private static final String femaleMigrantForenameSubFile = "migration/female_forename";
    private static final String migrantSurnameSubFile = "migration/surname";
    private static final String migrationRateSubFile = "migration/rate";
    private static final String surnameSubFile = "surname";
    private static final String geographySubFile = "geography";

    private static final String maleOccupationSubFile = "occupation/male";
    private static final String femaleOccupationSubFile = "occupation/female";

    private static final String maleOccupationChangeSubFile = "occupation/change/male";
    private static final String femaleOccupationChangeSubFile = "occupation/change/female";

    private static final Logger log = Logger.getLogger(Config.class.getName());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS", Locale.UK);
    public static final String POPULATION_EXPORT_DIR_NAME = "population";
    public static final String LOG_FILE_NAME = "log.txt";
    public static final String RECORDS_EXPORT_DIR_NAME = "records";
    public static final String TEMP_DIR_INDICATOR = "!TEMP!";
    public static final String CONTINGENCY_TABLES_DIR_NAME = "tables";

    private static Level logLevel = DEFAULT_LOG_LEVEL;
    public static final Path DEFAULT_RESULTS_SAVE_PATH = Paths.get("results");
    private final Path DEFAULT_GEOGRAPHY_FILE_PATH = Paths.get("geography.ser");
    private final Path DEFAULT_PROJECT_PATH = Paths.get(".");

    private Path pathToConfigFile;

    // ---- Input directory paths ----

    // Input directory path
    private Path inputDistributionsPath;

    // Paths to leaf directories within the input directory
    private Path varOrderedBirthPaths;
    private Path varMaleLifetablePaths;
    private Path varMaleDeathCausesPaths;
    private Path varFemaleLifetablePaths;
    private Path varFemaleDeathCausesPaths;
    private Path varMultipleBirthPaths;
    private Path varAdulterousBirthPaths;
    private Path varPartneringPaths;
    private Path varSeparationPaths;
    private Path varBirthRatioPaths;
    private Path varMaleForenamePaths;
    private Path varFemaleForenamePaths;
    private Path varMigrantMaleForenamePaths;
    private Path varMigrantFemaleForenamePaths;
    private Path varMigrantSurnamePaths;
    private Path varMigrationRatePaths;
    private Path varSurnamePaths;
    private Path varMarriagePaths;
    private Path varGeographyPaths;
    private Path varMaleOccupationPaths;
    private Path varFemaleOccupationPaths;
    private Path varMaleOccupationChangePaths;
    private Path varFemaleOccupationChangePaths;

    // ---- Run result paths ----

    // Path for summary of results for all runs
    private Path globalSummaryPath;

    // Path for summary of results for all runs within the group
    private Path resultsSummaryPath;

    // Path for detailed results for the run
    private Path detailedResultsPath;

    // Path for the records of a run
    private Path recordsPath;

    // Path for the graphs of a run
    private Path populationExportPath;

    // Path for the contingency tables used in R analysis of a run
    private Path contingencyTablesPath;

    // Path to directory of a run (defaults to the timestamp)
    private Path runPath;

    // ---- Other configuration options ----

    // Factors
    private double initialisationBirthRate = DEFAULT_INITIALISATION_BIRTH_RATE;
    private double initialisationDeathRate = DEFAULT_INITIALISATION_DEATH_RATE;
    private double recoveryFactor = DEFAULT_RECOVERY_FACTOR;
    private double proportionalRecoveryFactor = DEFAULT_PROPORTIONAL_RECOVERY_FACTOR;

    private boolean binomialSampling = DEFAULT_BINOMIAL_SAMPLING_FLAG;
    private boolean deterministic = DEFAULT_DETERMINISTIC_FLAG;
    private boolean exportContingencyTables = DEFAULT_EXPORT_CONTINGENCY_TABLES_FLAG;

    // TODO add flag for validation

    private boolean exportRecords = DEFAULT_EXPORT_RECORDS_FLAG;
    private boolean exportPopulation = DEFAULT_EXPORT_POPULATION_FLAG;

    // Time steps
    private Period simulationTimeStep = DEFAULT_SIMULATION_TIME_STEP;
    private Period minBirthSpacing = DEFAULT_MIN_BIRTH_SPACING;
    private Period minGestationPeriod = DEFAULT_MIN_GESTATION_PERIOD;
    private Period distributionGranularity = DEFAULT_DISTRIBUTION_GRANULARITY;

    // Locations
    private Path projectPath = DEFAULT_PROJECT_PATH;
    private Path summaryResultsDirPath = DEFAULT_RESULTS_SAVE_PATH;
    private Path resultsSavePath = DEFAULT_RESULTS_SAVE_PATH;
    private Path geographyFilePath = DEFAULT_GEOGRAPHY_FILE_PATH;

    private int seed = DEFAULT_SEED;
    private double overSizedGeographyFactor = DEFAULT_OVERSIZED_GEOGRAPHY_FACTOR;

    private int contingencyTableStepback = DEFAULT_CONTINGENCY_TABLE_STEPBACK;
    private double contingencyTablePrecision = DEFAULT_CONTINGENCY_TABLE_PRECISION;

    private String groupName = DEFAULT_GROUP_NAME;
    private RecordExportFormat recordExportFormat;
    private PopulationExportFormat populationExportFormat;

    private LocalDateTime simulationExecutionStartTime = LocalDateTime.now();

    // Simulation period and start size
    private LocalDate initialisationStart;
    private LocalDate simulationStart;
    private LocalDate simulationEnd;
    private Integer targetInitialPopulationSize;

    private Map<String, Consumer<String>> processors;

    public static String formatTimeStamp(final LocalDateTime startTime) {
        return startTime.format(FORMATTER);
    }

    // Initialise configuration programmatically
    public Config(final LocalDate initialisationStart, final LocalDate simulationStart, final LocalDate simulationEnd, final int targetInitialPopulationSize, final Path varPath, final Path resultsDir, final String groupName, final Path summaryResultsDir) throws IOException {

        this.initialisationStart = initialisationStart;
        this.simulationStart = simulationStart;
        this.simulationEnd = simulationEnd;
        this.targetInitialPopulationSize = targetInitialPopulationSize;
        this.inputDistributionsPath = varPath;
        this.resultsSavePath = resultsDir;
        this.groupName = groupName;
        this.summaryResultsDirPath = summaryResultsDir;

        validateOptions();
        setUpFileStructure();
        configureLogging();
        initialiseInputDistributionPaths();
    }

    // Initialise configuration from file
    public Config(final Path pathToConfigFile) throws IOException {

        configureFileProcessors();
        readConfigFile(pathToConfigFile);

        validateOptions();
        setUpFileStructure();
        configureLogging();
        initialiseInputDistributionPaths();
//        setGeographyPath();
    }

    private void setGeographyPath() {

        final Iterator<Path> it = getVarGeographyPaths().iterator();
        setGeographyFilePath(it.next());

        if (it.hasNext())
            throw new UnsupportedOperationException("Only one geography file is supported for each simulation");
    }

    public int getContingencyTableStepback() {
        return contingencyTableStepback;
    }

    public Path getDetailedResultsPath() {
        return detailedResultsPath;
    }

    public Path getRecordsDirPath() {
        return recordsPath;
    }

    public Path getConfigFilePath() {
        return pathToConfigFile;
    }

    public Path getGraphsDirPath() {
        return populationExportPath;
    }

    public Path getContingencyTablesPath() {
        return contingencyTablesPath;
    }

    public Path getGlobalSummaryPath() {
        return globalSummaryPath;
    }

    public Path getResultsSummaryPath() {
        return resultsSummaryPath;
    }

    private static Path pathToLogDir(final String groupName, final LocalDateTime startTime, final Path resultPath) {
        return resultPath.resolve(groupName).resolve(formatTimeStamp(startTime)).resolve("log").resolve(LOG_FILE_NAME);
    }

    public Path getRunPath() {
        return runPath;
    }

    public Path getInputDistributionsPath() {
        return inputDistributionsPath;
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

    public DirectoryStream<Path> getVarMaleOccupationPaths() {
        return getDirectories(varMaleOccupationPaths);
    }

    public DirectoryStream<Path> getVarMaleOccupationChangePaths() {
        return getDirectories(varMaleOccupationChangePaths);
    }

    public DirectoryStream<Path> getVarFemaleOccupationPaths() {
        return getDirectories(varFemaleOccupationPaths);
    }

    public DirectoryStream<Path> getVarFemaleOccupationChangePaths() {
        return getDirectories(varFemaleOccupationChangePaths);
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

    public DirectoryStream<Path> getVarAdulterousBirthPaths() {
        return getDirectories(varAdulterousBirthPaths);
    }

    public DirectoryStream<Path> getVarMarriagePaths() {
        return getDirectories(varMarriagePaths);
    }

    public DirectoryStream<Path> getVarGeographyPaths() {
        return getDirectories(varGeographyPaths);
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

    public DirectoryStream<Path> getVarMigrantMaleForenamePath() { return getDirectories(varMigrantMaleForenamePaths); }

    public DirectoryStream<Path> getVarMigrantFemaleForenamePath() { return getDirectories(varMigrantFemaleForenamePaths); }

    public DirectoryStream<Path> getVarSurnamePath() {
        return getDirectories(varSurnamePaths);
    }

    public DirectoryStream<Path> getVarMigrantSurnamePath() {
        return getDirectories(varMigrantSurnamePaths);
    }

    public DirectoryStream<Path> getVarMigrationRatePath() {
        return getDirectories(varMigrationRatePaths);
    }

    public LocalDate getInitialisationStart() {
        return initialisationStart;
    }

    public LocalDate getSimulationStart() {
        return simulationStart;
    }

    public LocalDate getSimulationEnd() {
        return simulationEnd;
    }

    public Period getSimulationTimeStep() {
        return simulationTimeStep;
    }

    public int getTargetInitialPopulationSize() {
        return targetInitialPopulationSize;
    }

    public double getInitialisationBirthRate() {
        return initialisationBirthRate;
    }

    public double getInitialisationDeathRate() {
        return initialisationDeathRate;
    }

    public LocalDateTime getSimulationExecutionStartTime() {
        return simulationExecutionStartTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public Period getDistributionGranularity() {
        return distributionGranularity;
    }

    public boolean getBinomialSampling() {
        return binomialSampling;
    }

    public Period getMinBirthSpacing() {
        return minBirthSpacing;
    }

    public double getRecoveryFactor() {
        return recoveryFactor;
    }

    public double getProportionalRecoveryFactor() {
        return proportionalRecoveryFactor;
    }

    public RecordExportFormat getRecordExportFormat() {
        return recordExportFormat;
    }

    public PopulationExportFormat getPopulationExportFormat() {
        return populationExportFormat;
    }

    public boolean shouldGenerateContingencyTables() {
        return exportContingencyTables;
    }

    public boolean shouldExportRecords() {
        return exportRecords;
    }

    public boolean shouldExportPopulation() {
        return exportPopulation;
    }

    public Period getMinGestationPeriod() {
        return minGestationPeriod;
    }

    public int getSeed() {
        return seed;
    }

    public boolean deterministic() {
        return deterministic;
    }

    public Config setDeterministic(final boolean deterministic) {

        this.deterministic = deterministic;
        return this;
    }

    public Config setGroupName(final String groupName) {

        this.groupName = groupName;
        return this;
    }

    public Config setSetupBirthRate(final double setUpBR) {

        this.initialisationBirthRate = setUpBR;
        return this;
    }

    public Config setSetupDeathRate(final double setUpDR) {

        this.initialisationDeathRate = setUpDR;
        return this;
    }

    public Config setRecoveryFactor(final double recoveryFactor) {

        this.recoveryFactor = recoveryFactor;
        return this;
    }

    public Config setProportionalRecoveryFactor(final double proportionalRecoveryFactor) {

        this.proportionalRecoveryFactor = proportionalRecoveryFactor;
        return this;
    }

    public Config setDistributionGranularity(final Period distributionGranularity) {

        this.distributionGranularity = distributionGranularity;
        return this;
    }

    public Config setMinBirthSpacing(final Period minBirthSpacing) {

        this.minBirthSpacing = minBirthSpacing;
        return this;
    }

    public Config setMinGestationPeriod(final Period minGestationPeriod) {

        this.minGestationPeriod = minGestationPeriod;
        return this;
    }

    public Config setProjectPath(final Path projectPath) {

        this.projectPath = projectPath;
        return this;
    }

    private DirectoryStream<Path> getDirectories(final Path path) {

        try {
            return Files.newDirectoryStream(path, filter);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialiseInputDistributionPaths() {

        // The path won't be set if this configuration is being created to test validation of pre-generated
        // contingency tables, rather to run a new simulation.
        if (inputDistributionsPath != null) {

            final Path birthPath = inputDistributionsPath.resolve(birthSubFile);
            varOrderedBirthPaths = birthPath.resolve(orderedBirthSubFile);
            varMultipleBirthPaths = birthPath.resolve(multipleBirthSubFile);
            varAdulterousBirthPaths = birthPath.resolve(adulterousBirthSubFile);
            varBirthRatioPaths = birthPath.resolve(birthRatioSubFile);

            final Path deathPath = inputDistributionsPath.resolve(deathSubFile);
            varMaleLifetablePaths = deathPath.resolve(maleDeathSubFile).resolve(lifetableSubFile);
            varMaleDeathCausesPaths = deathPath.resolve(maleDeathSubFile).resolve(deathCauseSubFile);
            varFemaleLifetablePaths = deathPath.resolve(femaleDeathSubFile).resolve(lifetableSubFile);
            varFemaleDeathCausesPaths = deathPath.resolve(femaleDeathSubFile).resolve(deathCauseSubFile);

            final Path relationshipsPath = inputDistributionsPath.resolve(relationshipsSubFile);
            varPartneringPaths = relationshipsPath.resolve(partneringSubFile);
            varSeparationPaths = relationshipsPath.resolve(separationSubFile);
            varMarriagePaths = relationshipsPath.resolve(marriageSubFile);

            final Path annotationsPath = inputDistributionsPath.resolve(annotationsSubFile);
            varMaleForenamePaths = annotationsPath.resolve(maleForenameSubFile);
            varFemaleForenamePaths = annotationsPath.resolve(femaleForenameSubFile);

            varMigrantMaleForenamePaths = annotationsPath.resolve(maleMigrantForenameSubFile);
            varMigrantFemaleForenamePaths = annotationsPath.resolve(femaleMigrantForenameSubFile);

            varSurnamePaths = annotationsPath.resolve(surnameSubFile);
            varMigrantSurnamePaths = annotationsPath.resolve(migrantSurnameSubFile);

            varGeographyPaths = annotationsPath.resolve(geographySubFile);

            varMigrationRatePaths = annotationsPath.resolve(migrationRateSubFile);

            varMaleOccupationPaths = annotationsPath.resolve(maleOccupationSubFile);
            varFemaleOccupationPaths = annotationsPath.resolve(femaleOccupationSubFile);

            varMaleOccupationChangePaths = annotationsPath.resolve(maleOccupationChangeSubFile);
            varFemaleOccupationChangePaths = annotationsPath.resolve(femaleOccupationChangeSubFile);

            setGeographyPath();
        }
    }

    public static void createFileIfDoesNotExist(final Path path) throws IOException {

        if (!Files.exists(path)) {
            createParentDirectoryIfDoesNotExist(path);
            Files.createFile(path);
        }
    }

    public static void createParentDirectoryIfDoesNotExist(final Path path) throws IOException {

        final Path parent_dir = path.getParent();
        if (parent_dir != null)
            Files.createDirectories(parent_dir);
    }

    private static void mkSummaryFile(final Path summaryFilePath) throws IOException {

        if (!summaryFilePath.toFile().exists()) {

            createFileIfDoesNotExist(summaryFilePath);

            final PrintWriter write = new PrintWriter(summaryFilePath.toFile());
            write.println(SummaryRow.getSeparatedHeadings());
            write.close();
        }
    }

    private static void mkDirs(final Path path) throws IOException {

        if (!Files.exists(path))
            if (!new File(path.toString()).mkdirs())
                throw new IOException("couldn't create directories for path: " + path);
    }

    // Filter method to exclude dot files from data file directory streams
    private final DirectoryStream.Filter<Path> filter = file -> {

        Path path = file.getFileName();
        if (path != null)
            return !path.toString().matches("^\\..+");

        throw new IOException("Failed to get Filename");
    };

    // Defines the allowed options in the config file and how to handle their values.
    private void configureFileProcessors() {

        processors = new HashMap<>();

        processors.put("input_distributions_path", value -> inputDistributionsPath = Paths.get(value));
        processors.put("results_save_location", value -> {
            try {
                resultsSavePath = value.equals(TEMP_DIR_INDICATOR) ? Files.createTempDirectory("valipop-results") : Paths.get(value);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        processors.put("summary_results_save_location", value -> summaryResultsDirPath = Paths.get(value));
        processors.put("project_location", value -> projectPath = Paths.get(value));

        processors.put("simulation_time_step", value -> simulationTimeStep = parsePeriod(value, "simulation_time_step"));
        processors.put("distribution_granularity", value -> distributionGranularity = parsePeriod(value, "distribution_granularity"));
        processors.put("min_birth_spacing", value -> minBirthSpacing = parsePeriod(value, "min_birth_spacing"));
        processors.put("min_gestation_period", value -> minGestationPeriod = parsePeriod(value, "min_gestation_period"));

        processors.put("initialisation_start", value -> initialisationStart = parseDate(value, "initialisation_start"));
        processors.put("simulation_start", value -> simulationStart = parseDate(value, "simulation_start"));
        processors.put("simulation_end", value -> simulationEnd = parseDate(value, "simulation_end"));

        processors.put("target_initial_population_size", value -> targetInitialPopulationSize = parsePositiveInteger(value, "target_initial_population_size"));
        processors.put("seed", value -> seed = parseInteger(value, "seed"));
        processors.put("contingency_table_stepback", value -> contingencyTableStepback = parsePositiveInteger(value, "contingency_table_stepback"));
        processors.put("contingency_table_precision", value -> contingencyTablePrecision = parseDouble(value, "contingency_table_precision"));

        processors.put("initialisation_birth_rate", value -> initialisationBirthRate = parseDouble(value, "initialisation_birth_rate"));
        processors.put("initialisation_death_rate", value -> initialisationDeathRate = parseDouble(value, "initialisation_death_rate"));
        processors.put("recovery_factor", value -> recoveryFactor = parseDouble(value, "recovery_factor"));
        processors.put("proportional_recovery_factor", value -> proportionalRecoveryFactor = parseDouble(value, "recovery_factor"));
        processors.put("over_sized_geography_factor", value -> overSizedGeographyFactor = parseOversizedGeographyFactor(value, "over_sized_geography_factor"));

        processors.put("binomial_sampling", value -> binomialSampling = value.equalsIgnoreCase("true"));
        processors.put("record_export", value -> exportRecords = value.equalsIgnoreCase("true"));
        processors.put("population_export", value -> exportPopulation = value.equalsIgnoreCase("true"));
        processors.put("contingency_table_export", value -> exportContingencyTables = value.equalsIgnoreCase("true"));
        processors.put("deterministic", value -> deterministic = value.equalsIgnoreCase("true"));

        processors.put("record_export_format", value -> {
            try {
                recordExportFormat = RecordExportFormat.valueOf(value);
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("'" + value + "' not a valid option for `record_export_format`");
            }
        });

        processors.put("population_export_format", value -> {
            try {
                populationExportFormat = PopulationExportFormat.valueOf(value);
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("'" + value + "' not a valid option for `population_export_format`");
            }
        });

        processors.put("log_level", value -> logLevel = Level.parse(value));
        processors.put("group_name", value -> groupName = value);
    }

    private static LocalDate parseDate(final String value, final String option) {
        try {
            return LocalDate.parse(value);
        } catch (final DateTimeParseException e) {
            throw new IllegalArgumentException("`" + option + "` must a parseable date, not '" + value + "'");
        }
    }

    private static Period parsePeriod(final String value, final String option) {
        try {
            return Period.parse(value);
        } catch (final DateTimeParseException e) {
            throw new IllegalArgumentException("`" + option + "` must be a period of the format 'P<years>Y<months>M<days>D', not '" + value + "'");
        }
    }

    private static int parseInteger(final String value, final String option) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("`" + option + "` must be an integer, not '" + value + "'");
        }
    }

    private static int parsePositiveInteger(final String value, final String option) {
        final int val = parseInteger(value, option);
        if (val < 0)
            throw new IllegalArgumentException("`" + option + "` cannot be a negative number");

        return val;
    }

    private static double parseDouble(final String value, final String option) {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("`" + option + "` must be a floating point number, not '" + value + "'");
        }
    }

    public static double parseOversizedGeographyFactor(final String value, final String option) {
        final double v = parseDouble(value, option);

        if (v < 1)
            throw new IllegalArgumentException("`" + option + "` cannot be less than 1");

        return v;
    }

    public void setOverSizedGeographyFactor(final String value) {
        overSizedGeographyFactor = parseOversizedGeographyFactor(value, "over_sized_geography_factor");
    }

    public String get(final String key) {
        return configMap.get(key);
    }

    private final Map<String, String> configMap = new HashMap<>();

    private void readConfigFile(final Path pathToConfigFile) {

        this.pathToConfigFile = pathToConfigFile;

        try {
            for (final String line : InputFileReader.getAllLines(pathToConfigFile)) {

                final String[] split = line.split("=", -1);

                if (split.length < 2) {
                    throw new IllegalArgumentException("Illegal line '" + line + "' read in config file. Each line should be of the format '<option> = <value>'");
                }

                final String key = split[0].trim();

                // Join remaining equals together if any, in case they were part of the value
                final String value = String.join("=", Arrays.copyOfRange(split, 1, split.length)).trim();

                configMap.put(key, value);

                if (processors.containsKey(key))
                    processors.get(key).accept(value);
            }
        } catch (final IOException e) {
            log.severe("error reading config: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void validateOptions() {

//        if (initialisationStart == null)
//            throw new IllegalArgumentException("`initialisation_start` is required");
//
//        if (simulationStart == null)
//            throw new IllegalArgumentException("`simulation_start` is required");
//
//        if (simulationEnd == null)
//            throw new IllegalArgumentException("`simulation_end` is required");
//
//        if (targetInitialPopulationSize == null)
//            throw new IllegalArgumentException("`target_initial_population_size` is required");
//
//        if (varPath == null)
//            throw new IllegalArgumentException("`input_distributions_path` is required");
//
//        // Ensure ordering of dates
//        if (initialisationStart.isAfter(simulationStart) )
//            throw new IllegalArgumentException("`initialisation_start` cannot be after `simulation_start`");
//
//        if (simulationStart.isAfter(simulationEnd))
//            throw new IllegalArgumentException("`simulation_start` cannot be after `simulation_end`");
//
//        // This allows the simulation enough time to burn in
//        if (simulationStart.getYear() - initialisationStart.getYear() < 150)
//            throw new IllegalArgumentException("`initialisation_start` must be at least 150 years before `simulation_start`");
    }

    private void setUpFileStructure() throws IOException {

        globalSummaryPath = summaryResultsDirPath.resolve( "global-results-summary.csv");
        final Path groupPath = resultsSavePath.resolve(groupName);
        resultsSummaryPath = summaryResultsDirPath.resolve(groupName).resolve( groupName + "-results-summary.csv");
        runPath = groupPath.resolve(formatTimeStamp(simulationExecutionStartTime));
        detailedResultsPath = runPath.resolve("statistics.txt");
        recordsPath = runPath.resolve(RECORDS_EXPORT_DIR_NAME);
        populationExportPath = runPath.resolve(POPULATION_EXPORT_DIR_NAME);
        contingencyTablesPath = runPath.resolve(CONTINGENCY_TABLES_DIR_NAME);
        final Path log = runPath.resolve("log");

        mkDirs(resultsSavePath);
        mkDirs(groupPath);
        mkDirs(runPath);
        mkDirs(recordsPath);
        mkDirs(populationExportPath);
        mkDirs(contingencyTablesPath);
        mkDirs(log);

        mkSummaryFile(globalSummaryPath);
        mkSummaryFile(resultsSummaryPath);
    }

    private void configureLogging() {

        try {

            final Logger globalLogger = Logger.getLogger("");

            // When running sims back to back we need to first stop writing to the old log file
            for(final Handler h : globalLogger.getHandlers()) {
                globalLogger.removeHandler(h);
            }

            final Handler handler = new FileHandler(pathToLogDir(groupName, simulationExecutionStartTime, resultsSavePath).toString());
            handler.setFormatter(new SimpleFormatter());

            globalLogger.addHandler(handler);
            globalLogger.setLevel(logLevel);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getGeographyFilePath() {
        return geographyFilePath;
    }

    public void setGeographyFilePath(final Path geographyFilePath) {
        this.geographyFilePath = geographyFilePath;
    }

    public Config setSeed(final int seed) {
        this.seed = seed;
        return this;
    }

    public void setTimestep(final Period timestep) {
        this.simulationTimeStep = timestep;
    }

    public double getContingencyTablePrecision() {
        return contingencyTablePrecision;
    }

    public void setCTtreePrecision(final double precision) {
        this.contingencyTablePrecision = precision;
    }

    public double getOverSizedGeographyFactor() {
        return overSizedGeographyFactor;
    }

    public SerializableConfig toSerialized() {
        return new SerializableConfig(
            inputDistributionsPath.toString(),
            varOrderedBirthPaths.toString(),
            varMaleLifetablePaths.toString(),
            varMaleDeathCausesPaths.toString(),
            varFemaleLifetablePaths.toString(),
            varFemaleDeathCausesPaths.toString(),
            varMultipleBirthPaths.toString(),
            varAdulterousBirthPaths.toString(),
            varPartneringPaths.toString(),
            varSeparationPaths.toString(),
            varBirthRatioPaths.toString(),
            varMaleForenamePaths.toString(),
            varFemaleForenamePaths.toString(),
            varMigrantMaleForenamePaths.toString(),
            varMigrantFemaleForenamePaths.toString(),
            varMigrantSurnamePaths.toString(),
            varMigrationRatePaths.toString(),
            varSurnamePaths.toString(),
            varMarriagePaths.toString(),
            varGeographyPaths.toString(),
            varMaleOccupationPaths.toString(),
            varFemaleOccupationPaths.toString(),
            varMaleOccupationChangePaths.toString(),
            varFemaleOccupationChangePaths.toString(),
            globalSummaryPath.toString(),
            resultsSummaryPath.toString(),
            detailedResultsPath.toString(),
            recordsPath.toString(),
            populationExportPath.toString(),
            contingencyTablesPath.toString(),
            runPath.toString(),
            initialisationBirthRate,
            initialisationDeathRate,
            recoveryFactor,
            proportionalRecoveryFactor,
            binomialSampling,
            deterministic,
            exportContingencyTables,
            simulationTimeStep,
            minBirthSpacing,
            minGestationPeriod,
            distributionGranularity,
            summaryResultsDirPath.toString(),
            resultsSavePath.toString(),
            geographyFilePath.toString(),
            projectPath.toString(),
            seed,
            overSizedGeographyFactor,
            contingencyTableStepback,
            contingencyTablePrecision,
            groupName,
            recordExportFormat,
            populationExportFormat,
            simulationExecutionStartTime,
            initialisationStart,
            simulationStart,
            simulationEnd,
            targetInitialPopulationSize
        );
    }

    public Config(final SerializableConfig config) {
        this.inputDistributionsPath =Path.of(config.varPath);
        this.varOrderedBirthPaths             =Path.of(config.varOrderedBirthPaths);
        this.varMaleLifetablePaths            =Path.of(config.varMaleLifetablePaths);
        this.varMaleDeathCausesPaths          =Path.of(config.varMaleDeathCausesPaths);
        this.varFemaleLifetablePaths          =Path.of(config.varFemaleLifetablePaths);
        this.varFemaleDeathCausesPaths        =Path.of(config.varFemaleDeathCausesPaths);
        this.varMultipleBirthPaths            =Path.of(config.varMultipleBirthPaths);
        this.varAdulterousBirthPaths          =Path.of(config.varAdulterousBirthPaths);
        this.varPartneringPaths               =Path.of(config.varPartneringPaths);
        this.varSeparationPaths               =Path.of(config.varSeparationPaths);
        this.varBirthRatioPaths               =Path.of(config.varBirthRatioPaths);
        this.varMaleForenamePaths             =Path.of(config.varMaleForenamePaths);
        this.varFemaleForenamePaths           =Path.of(config.varFemaleForenamePaths);
        this.varMigrantMaleForenamePaths      =Path.of(config.varMigrantMaleForenamePaths);
        this.varMigrantFemaleForenamePaths    =Path.of(config.varMigrantFemaleForenamePaths);
        this.varMigrantSurnamePaths           =Path.of(config.varMigrantSurnamePaths);
        this.varMigrationRatePaths            =Path.of(config.varMigrationRatePaths);
        this.varSurnamePaths                  =Path.of(config.varSurnamePaths);
        this.varMarriagePaths                 =Path.of(config.varMarriagePaths);
        this.varGeographyPaths                =Path.of(config.varGeographyPaths);
        this.varMaleOccupationPaths           =Path.of(config.varMaleOccupationPaths);
        this.varFemaleOccupationPaths         =Path.of(config.varFemaleOccupationPaths);
        this.varMaleOccupationChangePaths     =Path.of(config.varMaleOccupationChangePaths);
        this.varFemaleOccupationChangePaths   =Path.of(config.varFemaleOccupationChangePaths);
        this.globalSummaryPath                =Path.of(config.globalSummaryPath);
        this.resultsSummaryPath               =Path.of(config.resultsSummaryPath);
        this.detailedResultsPath              =Path.of(config.detailedResultsPath);
        this.recordsPath                      =Path.of(config.recordsPath);
        this.populationExportPath             =Path.of(config.graphsPath);
        this.contingencyTablesPath            =Path.of(config.contingencyTablesPath);
        this.runPath                          =Path.of(config.runPath);
        this.summaryResultsDirPath            =Path.of(config.summaryResultsDirPath);
        this.resultsSavePath                  =Path.of(config.resultsSavePath);
        this.geographyFilePath                =Path.of(config.geographyFilePath);
        this.projectPath                      =Path.of(config.projectPath);
        this.initialisationBirthRate          =config.initialisationBirthRate;
        this.initialisationDeathRate          =config.initialisationDeathRate;
        this.recoveryFactor                   =config.recoveryFactor;
        this.proportionalRecoveryFactor       =config.proportionalRecoveryFactor;
        this.binomialSampling                 =config.binomialSampling;
        this.deterministic                    =config.deterministic;
        this.exportContingencyTables          =config.outputTables;
        this.simulationTimeStep               =config.simulationTimeStep;
        this.minBirthSpacing                  =config.minBirthSpacing;
        this.minGestationPeriod               =config.minGestationPeriod;
        this.distributionGranularity          =config.distributionGranularity;
        this.seed                             =config.seed;
        this.overSizedGeographyFactor         =config.overSizedGeographyFactor;
        this.contingencyTableStepback         =config.contingencyTableStepback;
        this.contingencyTablePrecision        =config.contingencyTablePrecision;
        this.groupName                        =config.groupName;
        this.recordExportFormat               =config.outputRecordFormat;
        this.populationExportFormat           =config.outputGraphFormat;
        this.simulationExecutionStartTime     =config.simulationExecutionStartTime;
        this.initialisationStart              =config.initialisationStart;
        this.simulationStart                  =config.simulationStart;
        this.simulationEnd                    =config.simulationEnd;
        this.targetInitialPopulationSize      =config.targetInitialPopulationSize;
    }
}
