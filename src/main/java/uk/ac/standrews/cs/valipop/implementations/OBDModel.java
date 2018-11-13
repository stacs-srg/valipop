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
package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.events.EventLogic;
import uk.ac.standrews.cs.valipop.events.birth.NBirthLogic;
import uk.ac.standrews.cs.valipop.events.death.NDeathLogic;
import uk.ac.standrews.cs.valipop.events.init.InitLogic;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.AnalyticsRunner;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.ContingencyTableFactory;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordGenerationFactory;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    public static final String CODE_VERSION = "dev-bf";
    public static final int MINIMUM_POPULATION_SIZE = 100;

    private static Logger log = new Logger(OBDModel.class);

    private Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;
    private Population population;
    private AdvanceableDate currentTime;
    private ProgramTimer simTimer;

    private EventLogic deathLogic = new NDeathLogic();
    private EventLogic birthLogic = new NBirthLogic();

    private static final int MAX_ATTEMPTS = 10;

    private static final CompoundTimeUnit MAX_AGE = new CompoundTimeUnit(110, TimeUnit.YEAR);

    public static void setUpFileStructureAndLogs(String runPurpose, String startTime, String resultsPath) throws IOException {

        // This has to be run prior to creating the Config file as the file structure and log file need to be created
        // prior to the first logging event that occurs when generating the config
        // And errors in this method are sent to standard error

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        Logger.setLogFilePath(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath));
    }

    public OBDModel(String startTime, Config config) {

        this.config = config;

        // Set up simulation parameters
        currentTime = config.getTS();

        population = new Population(config);

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), startTime),
                config.getVarPath(), startTime, config.getRunPurpose(), CODE_VERSION, config.getSimulationTimeStep(),
                config.getInputWidth(), config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(),
                config.getTE()), config.getBirthFactor(), config.getDeathFactor(), config.getRecoveryFactor(),
                config.getProportionalRecoveryFactor(), config.getBinomialSampling(), config.getMinBirthSpacing(),
                config.getOutputRecordFormat(), config.getT0PopulationSize());
    }

    public void runSimulation() {

        for (int countAttempts = 0; countAttempts < MAX_ATTEMPTS; countAttempts++) {
            if (successfulRun()) break;
        }

        recordFinalSummary();
    }

    private boolean successfulRun() {

        try {
            simTimer = new ProgramTimer();
            runSimulationAttempt();
            return true;

        } catch (InsufficientNumberOfPeopleException e) {

            resetSimulation(simTimer);
            return false;
        }
    }

    private void runSimulationAttempt() throws InsufficientNumberOfPeopleException {

        while (!simulationFinished()) {
            simulationStep();
        }

        logResults();
        recordSummary();

        NBirthLogic.orders.close(); // TODO why closed here when might be used in another simulation attempt?
    }

    private void simulationStep() throws InsufficientNumberOfPeopleException {

        MemoryUsageAnalysis.log();
        StringBuilder logEntry = new StringBuilder(currentTime + "\t");

        if (initialisationFinished() && populationTooSmall()) {

            cleanUpAfterUnsuccessfulAttempt();
            throw new InsufficientNumberOfPeopleException("Seed size likely too small");
        }

        if (!simulationStarted()) {
            summary.setStartPop(population.getLivingPeople().getNumberOfPeople());
        }

        createBirths(logEntry);

        if (!initialisationFinished() && timeFromInitialisationStartIsWholeTimeUnit()) {
            initialisePeople(logEntry);

        } else if (initialisationFinished()) {
            recordInitialisationFinished(logEntry);
        }

        createDeaths(logEntry);

        if (simulationStarted()) {
            population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
        }

        advanceSimulationTime();
        recordPeopleCounts(logEntry);

        log.info(logEntry.toString());
    }

    private void recordPeopleCounts(StringBuilder logEntry) {

        logEntry.append(population.getLivingPeople().getNumberOfPeople() + "\t");
        logEntry.append(population.getDeadPeople().getNumberOfPeople());
    }

    private void advanceSimulationTime() {

        currentTime = currentTime.advanceTime(config.getSimulationTimeStep());
    }

    private void resetSimulation(ProgramTimer simTimer) {

        summary.setCompleted(false);
        summary.setSimRunTime(simTimer.getRunTimeSeconds());
        summary.outputSummaryRowToFile();

        deathLogic.resetEventCount();
        birthLogic.resetEventCount();
    }

    private void recordSummary() {

        summary.setCompleted(true);
        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());
        summary.setEligibilityChecks(population.getPopulationCounts().getEligibilityChecks());
        summary.setFailedEligibilityChecks(population.getPopulationCounts().getFailedEligibilityChecks());
    }

    private void recordFinalSummary() {

        MemoryUsageAnalysis.log();

        summary.setTotalPop(population.getAllPeople(config.getT0(), config.getTE(), MAX_AGE).getNumberOfPeople());
        summary.setSimRunTime(simTimer.getRunTimeSeconds());
    }

    private void logResults() {

        log.info("TKilled\t" + deathLogic.getEventCount());
        log.info("TBorn\t" + birthLogic.getEventCount());
        log.info("Ratio\t" + deathLogic.getEventCount() / (double) birthLogic.getEventCount());
    }

    private void recordInitialisationFinished(StringBuilder logEntry) {
        logEntry.append(0 + "\t");
    }

    private void initialisePeople(StringBuilder logEntry) {

        int numberInitialised = InitLogic.handleInitPeople(config, currentTime, population, desired);
        logEntry.append(numberInitialised + "\t");
    }

    private void createDeaths(StringBuilder logEntry) {

        int numberDying = deathLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
        logEntry.append(numberDying + "\t");
    }

    private void createBirths(StringBuilder logEntry) {

        int numberBorn = birthLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
        logEntry.append(numberBorn + "\t");
    }

    private void cleanUpAfterUnsuccessfulAttempt() {

        summary.setCompleted(false);

        logResults();

        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());
    }

    public void analyseAndOutputPopulation() throws PreEmptiveOutOfMemoryWarning {
        analyseAndOutputPopulation(true);
    }

    public void analyseAndOutputPopulation(boolean outputSummaryRow) {

        if (config.getOutputTables()) {
            // the 5 year step back is to combat the kick in the early stages of the CTtables for STAT - run in RStudio with no cleaning to see - potential bug in CTtree?
            ContingencyTableFactory.generateContingencyTables(population.getAllPeople(), desired, config, summary, 0, 5);
        }

        ProgramTimer recordTimer = new ProgramTimer();
        if (config.getOutputRecordFormat() != RecordFormat.NONE) {
            RecordGenerationFactory.outputRecords(config.getOutputRecordFormat(), FileUtils.getRecordsDirPath().toString(), population.getAllPeople(), config.getT0());
        }
        summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());

        try {
            PrintStream resultsOutput = new PrintStream(FileUtils.getDetailedResultsPath().toFile(), "UTF-8");
            AnalyticsRunner.runAnalytics(population.getAllPeople(config.getT0(), config.getTE(), MAX_AGE), resultsOutput);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        MemoryUsageAnalysis.log();

        summary.setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
        MemoryUsageAnalysis.reset();

        if (outputSummaryRow) {
            summary.outputSummaryRowToFile();
        }

        log.info("OBDModel --- Output complete");
    }

    private boolean populationTooSmall() {

        return population.getLivingPeople().getAll().size() < MINIMUM_POPULATION_SIZE;
    }

    private boolean timeNotAfter(ValipopDate date) {
        return DateUtils.dateBeforeOrEqual(currentTime, date);
    }

    private boolean initialisationFinished() {
        return !InitLogic.inInitPeriod(currentTime);
    }

    private boolean simulationStarted() {
        return !timeNotAfter(config.getT0());
    }

    private boolean simulationFinished() {
        return !timeNotAfter(config.getTE());
    }

    private boolean timeFromInitialisationStartIsWholeTimeUnit() {
        return DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep(), config.getTS());
    }

    public Population getPopulation() {
        return population;
    }

    public PopulationStatistics getDesiredPopulationStatistics() {
        return desired;
    }

    public SummaryRow getSummaryRow() {
        return summary;
    }
}
