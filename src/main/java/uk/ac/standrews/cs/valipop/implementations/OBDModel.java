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

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.EntityFactory;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.FemaleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PersonCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.AnalyticsRunner;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.ContingencyTableFactory;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.*;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.selectionApproaches.SharedLogic;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordGenerationFactory;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DeathDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.OperableLabelledValueSet;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    private static final String CODE_VERSION = "dev-bf";
    private static final int MINIMUM_POPULATION_SIZE = 100;

    private static Logger log = new Logger(OBDModel.class);

    private Config config;
    private SummaryRow summary;
    private PopulationStatistics desired;
    private Population population;
    private AdvanceableDate currentTime;
    private ProgramTimer simTimer;

    private static final int MAX_ATTEMPTS = 10;

    private static final CompoundTimeUnit MAX_AGE = new CompoundTimeUnit(110, TimeUnit.YEAR);

    private static PrintWriter birthOrders;
    private int birthsCount = 0;
    private int deathCount = 0;
    private DeathDateSelector deathDateSelector = new DeathDateSelector();

    private static RandomGenerator randomNumberGenerator;

    private static int currentHypotheticalPopulationSize;

    private static CompoundTimeUnit initTimeStep;
    private static ValipopDate endOfInitPeriod;

    private static int numberOfBirthsInThisTimestep = 0;

    public static void setUpInitParameters(Config config, PopulationStatistics desiredPopulationStatistics) {

        randomNumberGenerator = desiredPopulationStatistics.getRandomGenerator();

        currentHypotheticalPopulationSize = calculateStartingPopulationSize(config);
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfInitPeriod = config.getTS().advanceTime(
                new CompoundTimeUnit(desiredPopulationStatistics.getOrderedBirthRates(config.getTS().getYearDate()).getLargestLabel().getValue(), TimeUnit.YEAR));

        log.info("End of Initialisation Period set: " + endOfInitPeriod.toString());

        initTimeStep = config.getSimulationTimeStep();
    }

    public static int handleInitPeople(Config config, AdvanceableDate currentTime, Population population, PopulationStatistics ps) {

        // calculate hypothetical number of expected births
        int hypotheticalBirths = calculateChildrenToBeBorn(currentHypotheticalPopulationSize, config.getSetUpBR() * initTimeStep.toDecimalRepresentation());

        int shortFallInBirths = hypotheticalBirths - numberOfBirthsInThisTimestep;
        numberOfBirthsInThisTimestep = 0;

        // calculate hypothetical number of expected deaths
        int hypotheticalDeaths = calculateNumberToDie(currentHypotheticalPopulationSize, config.getSetUpDR() * initTimeStep.toDecimalRepresentation());

        // update hypothetical population
        currentHypotheticalPopulationSize = currentHypotheticalPopulationSize + hypotheticalBirths - hypotheticalDeaths;

        if (shortFallInBirths >= 0) {
            // add Orphan Children to the population
            for (int i = 0; i < shortFallInBirths; i++) {
                EntityFactory.formOrphanChild(currentTime, getTimeStep(), population, ps);
            }
        } else {
            double removeN = Math.abs(shortFallInBirths) / 2.0;
            int removeMales;
            int removeFemales;

            if (removeN % 1 != 0) {
                removeMales = (int) Math.ceil(removeN);
                removeFemales = (int) Math.floor(removeN);
            } else {
                removeMales = (int) removeN;
                removeFemales = (int) removeN;
            }

            try {
                for (int i = 0; i < removeMales; i++) {
                    population.getLivingPeople().getMales().removeNPersons(removeMales, currentTime, initTimeStep, true);
                }

                for (int i = 0; i < removeFemales; i++) {
                    population.getLivingPeople().getFemales().removeNPersons(removeFemales, currentTime, initTimeStep, true);
                }

            } catch (InsufficientNumberOfPeopleException e) {
                // Never should happen
                throw new Error();
            }
        }

        return shortFallInBirths;
    }

    public static void incrementBirthCount(int n) {
        numberOfBirthsInThisTimestep += n;
    }

    public static boolean inInitPeriod(ValipopDate currentTime) {
        return DateUtils.dateBeforeOrEqual(currentTime, endOfInitPeriod);
    }

    public static CompoundTimeUnit getTimeStep() {
        return initTimeStep;
    }

    private static int calculateStartingPopulationSize(Config config) {
        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.getTS(), config.getT0()).getCount()));
    }

    private static int calculateChildrenToBeBorn(int sizeOfCohort, Double birthRate) {
        return SharedLogic.calculateNumberToHaveEvent(sizeOfCohort, birthRate, randomNumberGenerator);
    }

    private static int calculateNumberToDie(int people, Double deathRate) {
        return SharedLogic.calculateNumberToHaveEvent(people, deathRate, randomNumberGenerator);
    }


    public static void setUpFileStructureAndLogs(String runPurpose, String startTime, String resultsPath) throws IOException {

        // This has to be run prior to creating the Config file as the file structure and log file need to be created
        // prior to the first logging event that occurs when generating the config
        // And errors in this method are sent to standard error

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        Logger.setLogFilePath(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath));
        birthOrders = FileUtils.mkDumpFile("order.csv");
    }



    public OBDModel(String startTime, Config config) {

        this.config = config;

        // Set up simulation parameters
        currentTime = config.getTS();

        population = new Population(config);

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        setUpInitParameters(config, desired);

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

    public Population getPopulation() {
        return population;
    }

    public PopulationStatistics getDesiredPopulationStatistics() {
        return desired;
    }

    public SummaryRow getSummaryRow() {
        return summary;
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

        birthOrders.close(); // TODO why closed here when might be used in another simulation attempt?
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

        resetDeathCount();
        resetBirthsCount();
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

        log.info("TKilled\t" + getDeathCount());
        log.info("TBorn\t" + getBirthsCount());
        log.info("Ratio\t" + getDeathCount() / (double) getBirthsCount());
    }

    private void recordInitialisationFinished(StringBuilder logEntry) {
        logEntry.append(0 + "\t");
    }

    private void initialisePeople(StringBuilder logEntry) {

        int numberInitialised = handleInitPeople(config, currentTime, population, desired);
        logEntry.append(numberInitialised + "\t");
    }

    private void createDeaths(StringBuilder logEntry) {

        int numberDying = deathEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
        logEntry.append(numberDying + "\t");
    }

    private void createBirths(StringBuilder logEntry) {

        int numberBorn = birthEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
        logEntry.append(numberBorn + "\t");
    }

    private void cleanUpAfterUnsuccessfulAttempt() {

        summary.setCompleted(false);

        logResults();

        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());
    }

    private boolean populationTooSmall() {

        return population.getLivingPeople().getAll().size() < MINIMUM_POPULATION_SIZE;
    }

    private boolean initialisationFinished() {
        return !inInitPeriod(currentTime);
    }

    private boolean simulationStarted() {
        return !DateUtils.dateBeforeOrEqual(currentTime, config.getT0());
    }

    private boolean simulationFinished() {
        return !DateUtils.dateBeforeOrEqual(currentTime, config.getTE());
    }

    private boolean timeFromInitialisationStartIsWholeTimeUnit() {
        return DateUtils.matchesInterval(currentTime, getTimeStep(), config.getTS());
    }

    private int birthEvent(Config config, AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                           Population population, PopulationStatistics desiredPopulationStatistics) {

        int bornAtTS = 0;

        FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        Iterator<AdvanceableDate> divDates = femalesLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvanceableDate divDate;
        // For each division in the population data store upto the current date
        while (divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate.advanceTime(consideredTimePeriod), currentDate).getCount();

            int cohortSize = femalesLiving.getAllPersonsBornInTimePeriod(divDate, consideredTimePeriod).size();

            Set<IntegerRange> inputOrders = desiredPopulationStatistics.getOrderedBirthRates(currentDate).getColumnLabels();

            for (IntegerRange order : inputOrders) {

                Collection<IPerson> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);

                BirthStatsKey key = new BirthStatsKey(age, order.getValue(), cohortSize, consideredTimePeriod, currentDate);
                SingleDeterminedCount determinedCount =
                        (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

                int birthAdjust = 0;
                if (determinedCount.getDeterminedCount() != 0) {

                    int adjuster = new Double(Math.ceil(config.getBirthFactor())).intValue();

                    int bound = 1000000;
                    if (desiredPopulationStatistics.getRandomGenerator().nextInt(bound) < Math.abs(config.getBirthFactor() / adjuster) * bound) {

                        if (config.getBirthFactor() < 0) {
                            birthAdjust = adjuster;
                        } else {
                            birthAdjust = -1 * adjuster;
                        }
                    }
                }

                int numberOfChildren = determinedCount.getDeterminedCount() + birthAdjust;

                // Make women into mothers

                MotherSet mothers = selectMothers(config, people, numberOfChildren, desiredPopulationStatistics, currentDate, consideredTimePeriod, population);

                // Partner females of age who don't have partners
                int cancelledChildren = partnerEvent(mothers.needPartners, desiredPopulationStatistics, currentDate, consideredTimePeriod, population, config);

                int childrenMade = mothers.newlyProducedChildren - cancelledChildren;

                bornAtTS += childrenMade;
                incrementBirthCount(childrenMade);

                if (childrenMade > birthAdjust) {
                    childrenMade = childrenMade - birthAdjust;
                } else {
                    childrenMade = 0;
                }

                determinedCount.setFulfilledCount(childrenMade);

                birthOrders.println(currentDate.getYear() + "," + age + "," + order + "," + childrenMade + "," + numberOfChildren);

                desiredPopulationStatistics.returnAchievedCount(determinedCount);
            }
        }

        birthsCount += bornAtTS;

        return bornAtTS;
    }

    private int getBirthsCount() {
        return birthsCount;
    }

    private void resetBirthsCount() {
        birthsCount = 0;
    }

    private MotherSet selectMothers(Config config, Collection<IPerson> females, int numberOfChildren,
                                    PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate,
                                    CompoundTimeUnit consideredTimePeriod, Population population) {

        Collection<NewMother> needPartners = new ArrayList<>();
        Collection<IPerson> havePartners = new ArrayList<>();

        if (females.size() == 0) {
            return new MotherSet( needPartners);
        }

        // TODO adjust this to also permit age variations
        List<IPerson> femalesAL = new ArrayList<>(females);

        int ageOfMothers = ageOnDate(femalesAL.get(0), currentDate);

        MultipleDeterminedCount requiredBirths =
                calcNumberOfPregnanciesOfMultipleBirth(ageOfMothers, numberOfChildren, desiredPopulationStatistics, currentDate, consideredTimePeriod, config);

        int childrenMade = 0;

        Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren = new HashMap<>();

        LabelledValueSet<IntegerRange, Integer> motherCountsByMaternities =
                new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().getLabels(), 0);

        OperableLabelledValueSet<IntegerRange, Integer> remainingMothersToFind =
                new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().clone());

        IntegerRange highestBirthOption;

        try {
            highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();
        } catch (NoSuchElementException e) {
            return new MotherSet( needPartners);
        }

        CollectionUtils.shuffle(femalesAL, desiredPopulationStatistics.getRandomGenerator());

        for (IPerson female : femalesAL) {

            if (childrenMade >= numberOfChildren) {
                break;
            }

            if (eligible(female, desiredPopulationStatistics, currentDate)) {

                childrenMade += highestBirthOption.getValue();

                if (needsNewPartner(female, currentDate)) {
                    needPartners.add(new NewMother(female, highestBirthOption.getValue()));

                } else {

                    addChildrenToCurrentPartnership(female, highestBirthOption.getValue(), currentDate, consideredTimePeriod, population, desiredPopulationStatistics, config);
                    havePartners.add(female);

                    int numberOfChildrenInLatestPartnership = numberOfChildrenInLatestPartnership(female);

                    try {
                        continuingPartneredFemalesByChildren.get(numberOfChildrenInLatestPartnership).add(female);
                    } catch (NullPointerException e) {
                        continuingPartneredFemalesByChildren.put(numberOfChildrenInLatestPartnership, new ArrayList<>(Collections.singleton(female)));
                    }
                }

                // updates count of remaining mothers to find
                int furtherMothersNeededForMaternitySize = remainingMothersToFind.get(highestBirthOption) - 1;
                remainingMothersToFind.update(highestBirthOption, furtherMothersNeededForMaternitySize);

                // updates count of mother found
                motherCountsByMaternities.update(highestBirthOption, motherCountsByMaternities.getValue(highestBirthOption) + 1);

                if (furtherMothersNeededForMaternitySize <= 0) {
                    try {
                        highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();
                    } catch (NoSuchElementException e) {
                        // In this case we have created all the new mothers and children required
                        break;
                    }
                }
            }
        }

        separationEvent(continuingPartneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population, config);

        requiredBirths.setFulfilledCount(motherCountsByMaternities);
        desiredPopulationStatistics.returnAchievedCount(requiredBirths);

        return new MotherSet( needPartners, childrenMade);
    }

    private MultipleDeterminedCount calcNumberOfPregnanciesOfMultipleBirth(int ageOfMothers, int numberOfChildren, PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate,
                                                                           CompoundTimeUnit consideredTimePeriod, Config config) {

        MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, consideredTimePeriod, currentDate);
        return (MultipleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);
    }

    private boolean eligible(IPerson potentialMother, PopulationStatistics desiredPopulationStatistics, ValipopDate currentDate) {

        IPerson lastChild = PopulationNavigation.getLastChild(potentialMother);

        if (lastChild != null) {

            ExactDate earliestDateOfNextChild = DateUtils.calculateExactDate(lastChild.getBirthDate(), desiredPopulationStatistics.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentDate
            return DateUtils.dateBefore(earliestDateOfNextChild, currentDate);

        } else {
            // i.e. there is no previous child and thus no limitation to birth
            return true;
        }
    }

    private void addChildrenToCurrentPartnership(IPerson mother, int numberOfChildren, AdvanceableDate onDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, Config config) {

        population.getLivingPeople().removePerson(mother);

        IPerson lastChild = PopulationNavigation.getLastChild(mother);
        IPartnership last = lastChild.getParents();
        IPerson child;

        ValipopDate birthDate = null;

        IPerson man = last.getMalePartner();

        for (int c = 0; c < numberOfChildren; c++) {
            if (birthDate == null) {
                child = EntityFactory.makePerson(onDate, birthTimeStep, last, population, ps);
                last.addChildren(Collections.singleton(child));
                birthDate = child.getBirthDate();
            } else {
                child = EntityFactory.makePerson(onDate, last, population, ps);
                last.addChildren(Collections.singleton(child));
            }
        }

        // record that child is legitimate
        IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(ageOnDate(man, birthDate), numberOfChildren, birthTimeStep, birthDate);
        SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) ps.getDeterminedCount(illegitimateKey, config);
        illegitimateCounts.setFulfilledCount(0);
        ps.returnAchievedCount(illegitimateCounts);

        // decide if to cause marriage
        // Decide on marriage
        MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, birthDate), numberOfChildren, birthTimeStep, birthDate);
        SingleDeterminedCount marriageCounts = (SingleDeterminedCount) ps.getDeterminedCount(marriageKey, config);

        if (last.getMarriageDate() != null) {
            // is already married - so return as married
            marriageCounts.setFulfilledCount(numberOfChildren);
        } else {
            boolean toBeMarriedBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numberOfChildren) == 1;

            if (toBeMarriedBirth) {
                marriageCounts.setFulfilledCount(numberOfChildren);
                last.setMarriageDate(EntityFactory.marriageDateSelector.selectDate(lastChild.getBirthDate(), birthDate, ps.getRandomGenerator()));
            } else {
                marriageCounts.setFulfilledCount(0);
            }
        }
        ps.returnAchievedCount(marriageCounts);

        population.getLivingPeople().addPerson(mother);
    }

    private int partnerEvent(Collection<NewMother> needingPartners, PopulationStatistics desiredPopulationStatistics,
                             AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod, Population population, Config config) {

        int forNFemales = needingPartners.size();

        if (forNFemales != 0) {

            LinkedList<NewMother> women = new LinkedList<>(needingPartners);

            int age = ageOnDate(women.getFirst().newMother, currentDate);

            PartneringStatsKey key = new PartneringStatsKey(age, forNFemales, consideredTimePeriod, currentDate);

            MultipleDeterminedCount determinedCounts = (MultipleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

            OperableLabelledValueSet<IntegerRange, Integer> partnerCounts;
            LabelledValueSet<IntegerRange, Integer> achievedPartnerCounts;

            try {
                partnerCounts = new IntegerRangeToIntegerSet(determinedCounts.getDeterminedCount());
                achievedPartnerCounts = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);
            } catch (NullPointerException e) {
                throw new Error("Large population size has lead to accumulated errors in processing of Doubles that the " +
                        "sum of the underlying self correction array no longer approximates to a whole number - " +
                        "make DELTA bigger? Or use a data type that actually works...");
            }

            LabelledValueSet<IntegerRange, Integer> availableMen = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);

            // this section gets all the men in the age ranges we may need to look at
            Map<IntegerRange, LinkedList<IPerson>> allMen = new TreeMap<>();

            for (IntegerRange iR : partnerCounts.getLabels()) {
                AdvanceableDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(iR, currentDate);
                CompoundTimeUnit iRLength = getIRLength(iR);

                LinkedList<IPerson> m = new LinkedList<>(population.getLivingPeople().getMales().getAllPersonsBornInTimePeriod(yobOfOlderEndOfIR, iRLength));

                CollectionUtils.shuffle(m, desiredPopulationStatistics.getRandomGenerator());

                allMen.put(iR, m);
                availableMen.update(iR, m.size());
            }

            OperableLabelledValueSet<IntegerRange, Double> shortfallCounts =
                    new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));

            // this section redistributes the determined partner counts based on the number of available men in each age range
            while (shortfallCounts.countPositiveValues() != 0) {
                LabelledValueSet<IntegerRange, Double> zeroedNegShortfalls = shortfallCounts.zeroNegativeValues();
                int numberOfRangesWithSpareMen = shortfallCounts.countNegativeValues();
                double totalShortfall = zeroedNegShortfalls.getSumOfValues();
                double shortfallToShare = totalShortfall / (double) numberOfRangesWithSpareMen;
                partnerCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesAddNWhereCorrespondingLabelNegativeInLVS(shortfallToShare, shortfallCounts)
                        .valuesSubtractValues(zeroedNegShortfalls)).controlledRoundingMaintainingSum();
                shortfallCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));
            }

            // TODO - upto - question: does infids affect NPA?

            ArrayList<ProposedPartnership> proposedPartnerships = new ArrayList<>();

            // for each age range of males
            for (IntegerRange iR : partnerCounts.getLabels()) {

                int determinedCount = partnerCounts.get(iR);

                LinkedList<IPerson> men = allMen.get(iR);
                IPerson head = null; // keeps track of first man seen to prevent infinite loop

                Collection<NewMother> unmatchedFemales = new ArrayList<>();

                // Keep going until enough females have been matched for this iR
                while (determinedCount > 0) {
                    IPerson man = men.pollFirst();
                    NewMother woman;

                    if (!women.isEmpty()) {
                        woman = women.pollFirst();
                    } else {
                        break;
                    }

                    // if man is head of list - i.e. this is the second time round
                    if (man == head) {
                        // thus female has not been able to be matched
                        unmatchedFemales.add(woman);
                        head = null;

                        // get next woman to check for partnering
                        if (!women.isEmpty()) {
                            woman = women.pollFirst();
                        } else {
                            break;
                        }
                    }

                    // check if there is any reason why these people cannot lawfully be partnered...
                    if (eligible(man, woman.newMother, woman.numberOfChildrenInMaternity, population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config)) {
                        // if they can - then note as a proposed partnership
                        proposedPartnerships.add(new ProposedPartnership(man, woman.newMother, woman.numberOfChildrenInMaternity));
                        determinedCount--;
                        head = null;

                    } else {
                        // else we need to loop through more men - so keep track of the first man we looked at
                        if (head == null) {
                            head = man;
                        }
                        men.addLast(man);
                        women.addFirst(woman);
                    }
                }

                women.addAll(unmatchedFemales);

                // note how many females have been partnered at this age range
                achievedPartnerCounts.add(iR, partnerCounts.get(iR) - determinedCount);
            }

            if (!women.isEmpty()) {
                for (int i = 0; i < women.size(); i++) {
                    NewMother uf = women.get(i);

                    nmLoop:
                    for (IntegerRange iR : partnerCounts.getLabels()) {
                        for (IPerson m : allMen.get(iR)) {
                            if (eligible(m, uf.newMother, uf.numberOfChildrenInMaternity, population, desiredPopulationStatistics, currentDate, consideredTimePeriod, config) && !inPPs(m, proposedPartnerships)) {

                                proposedPartnerships.add(new ProposedPartnership(m, uf.newMother, uf.numberOfChildrenInMaternity));

                                women.remove(uf);
                                i--;
                                break nmLoop;
                            }
                        }
                    }
                }
            }

            int cancelledChildren = getCancelledChildren(population, women);

            LabelledValueSet<IntegerRange, Integer> returnPartnerCounts = determinedCounts.getZeroedCountsTemplate();

            Map<Integer, List<IPerson>> partneredFemalesByChildren = new HashMap<>();

            for (final ProposedPartnership partnership : proposedPartnerships) {

                final IPerson mother = partnership.female;
                final IPerson father = partnership.male;

                final int numChildrenInPartnership = partnership.numberOfChildren;

                // Decide on marriage
                final MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, currentDate), numChildrenInPartnership, consideredTimePeriod, currentDate);
                final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(marriageKey, config);

                final boolean isIllegitimate = !needsNewPartner(father, currentDate);
                final boolean toBeMarriedBirth = !isIllegitimate && (int) Math.round(marriageCounts.getDeterminedCount() / (double) numChildrenInPartnership) == 1;

                final IPartnership marriage = EntityFactory.formNewChildrenInPartnership(numChildrenInPartnership, father, mother, currentDate, consideredTimePeriod, population, desiredPopulationStatistics, isIllegitimate, toBeMarriedBirth);

                // checks if marriage was possible
                if (marriage.getMarriageDate() != null) {
                    marriageCounts.setFulfilledCount(numChildrenInPartnership);
                } else {
                    marriageCounts.setFulfilledCount(0);
                }

                desiredPopulationStatistics.returnAchievedCount(marriageCounts);

                final IntegerRange maleAgeRange = resolveAgeToIntegerRange(father, returnPartnerCounts.getLabels(), currentDate);
                returnPartnerCounts.update(maleAgeRange, returnPartnerCounts.getValue(maleAgeRange) + 1);

                addMotherToMap(partneredFemalesByChildren, mother, numChildrenInPartnership);
            }

            determinedCounts.setFulfilledCount(returnPartnerCounts);
            desiredPopulationStatistics.returnAchievedCount(determinedCounts);

            separationEvent(partneredFemalesByChildren, consideredTimePeriod, currentDate, desiredPopulationStatistics, population, config);

            return cancelledChildren;
        }

        return 0;
    }

    private void addMotherToMap(Map<Integer, List<IPerson>> partneredFemalesByChildren, IPerson mother, int numChildrenInPartnership) {

        if (partneredFemalesByChildren.containsKey(numChildrenInPartnership)) {
            partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
        } else {
            partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>(Collections.singleton(mother)));
        }
    }

    private int getCancelledChildren(Population population, List<NewMother> women) {

        int cancelledChildren = 0;

        if (!women.isEmpty()) {
            for (NewMother m : women) {

                // update position in data structures
                population.getLivingPeople().removePerson(m.newMother);

                cancelledChildren += m.numberOfChildrenInMaternity;
                // cancel birth(s) as no father can be found
                m.newMother.getPartnerships().remove(getLastPartnership(m.newMother));

                population.getLivingPeople().addPerson(m.newMother);
            }
        }
        return cancelledChildren;
    }

    private IntegerRange resolveAgeToIntegerRange(IPerson male, Set<IntegerRange> labels, ValipopDate currentDate) {

        int age = ageOnDate(male, currentDate);

        for (IntegerRange iR : labels) {
            if (iR.contains(age)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Male does not fit in expected ranges...");
    }

    private boolean inPPs(IPerson p, List<ProposedPartnership> pps) {

        for (ProposedPartnership pp : pps) {
            if (pp.male == p || pp.female == p) {
                return true;
            }
        }
        return false;
    }

    private boolean eligible(IPerson man, IPerson woman, int childrenInPregnancy, Population population, PopulationStatistics desiredPopulationStatistics,
                             AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod, Config config) {

        population.getPopulationCounts().incEligibilityCheck();

        boolean eligible = maleAvailable(man, childrenInPregnancy, desiredPopulationStatistics, currentDate, consideredTimePeriod, config) && legallyEligibleToMarry(man, woman);

        if (!eligible) {
            population.getPopulationCounts().incFailedEligibilityCheck();
        }

        return eligible;
    }

    private boolean maleAvailable(IPerson man, int childrenInPregnancy, PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate,
                                  CompoundTimeUnit consideredTimePeriod, Config config) {

        // in the init period any partnering is allowed
        if (DateUtils.dateBeforeOrEqual(currentDate, new YearDate(1791))) {
            return true;
        }

        // Get access to illegitimacy rates
        IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(ageOnDate(man, currentDate), childrenInPregnancy, consideredTimePeriod, currentDate);
        SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(illegitimateKey, config);
        int permitted = (int) Math.round(illegitimateCounts.getDeterminedCount() / (double) childrenInPregnancy);

        if (needsNewPartner(man, currentDate)) {
            // record the legitimate birth
            illegitimateCounts.setFulfilledCount(0);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        if (permitted == 1) {
            // record the illegitimate birth
            illegitimateCounts.setFulfilledCount(childrenInPregnancy);
            desiredPopulationStatistics.returnAchievedCount(illegitimateCounts);
            return true;
        }

        // no birth is to happen on account of this man - therefore we don't report any achieved count for the statistic
        return false;
    }

    private boolean legallyEligibleToMarry(final IPerson man, final IPerson woman) {

        try {
            exclude(femaleAncestorsOf(man), woman);
            exclude(femaleDescendantsOf(man), woman);
            exclude(sistersOf(man), woman);
            exclude(femaleAncestorsOf(descendantsOf(man)), woman);
            exclude(femaleDescendantsOf(ancestorsOf(man)), woman);
            exclude(partnersOf(maleAncestorsOf(man)), woman);
            exclude(partnersOf(maleDescendantsOf(man)), woman);
            exclude(partnersOf(brothersOf(man)), woman);
            exclude(femaleDescendantsOf(siblingsOf(man)), woman);
            exclude(femaleAncestorsOf(partnersOf(man)), woman);
            exclude(femaleDescendantsOf(partnersOf(man)), woman);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    private void exclude(Collection<IPerson> collection, IPerson person) {
        if (collection.contains(person)) throw new RuntimeException();
    }

    private CompoundTimeUnit getIRLength(IntegerRange iR) {

        int length = iR.getMax() - iR.getMin() + 1;

        return new CompoundTimeUnit(length, TimeUnit.YEAR);
    }

    private AdvanceableDate getYobOfOlderEndOfIR(IntegerRange iR, ValipopDate currentDate) {

        return new YearDate(currentDate.getYear() - iR.getMax() - 1);
    }

    private void separationEvent(Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren,
                                 CompoundTimeUnit consideredTimePeriod, ValipopDate currentDate,
                                 PopulationStatistics desiredPopulationStatistics, Population population, Config config) {

        // Consideration of separation is based on number of children in females current partnerships
        for (Map.Entry<Integer, List<IPerson>> entry : continuingPartneredFemalesByChildren.entrySet()) {

            Integer numberOfChildren = entry.getKey();
            int ageOfMothers = 0;

            // Get mothers with given number of children in current partnership
            List<IPerson> mothers = entry.getValue();

            if (mothers.size() != 0) {
                ageOfMothers = ageOnDate(mothers.get(0), currentDate);
            }

            // Get determined count for separations for this group of mothers
            SeparationStatsKey key = new SeparationStatsKey(numberOfChildren, ageOfMothers, mothers.size(), consideredTimePeriod, currentDate);
            SingleDeterminedCount dC = (SingleDeterminedCount) desiredPopulationStatistics.getDeterminedCount(key, config);

            int count = 0;

            // For each mother in this group
            for (IPerson p : mothers) {

                // If enough mothers have been separated then break
                if (count >= dC.getDeterminedCount()) {
                    break;
                }

                // else mark partnership for separation
                separate(getLastPartnership(p),getLastChild(p).getBirthDate(), new CompoundTimeUnit(1, TimeUnit.MONTH));

                count++;
            }

            // Return achieved statistics to the statistics handler
            dC.setFulfilledCount(count);
            desiredPopulationStatistics.returnAchievedCount(dC);
        }
    }

    // Move from year to sim date and time step
    private int deathEvent(Config config, AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                           Population population, PopulationStatistics desiredPopulationStatistics) {

        int killedAtTS = 0;

        killedAtTS += handleDeathsForSex(SexOption.MALE, config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);
        killedAtTS += handleDeathsForSex(SexOption.FEMALE, config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);

        return killedAtTS;
    }

    private int getDeathCount() {
        return deathCount;
    }

    private void resetDeathCount() {
        deathCount = 0;
    }

    private int handleDeathsForSex(SexOption sex, Config config, AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                                   Population population, PopulationStatistics desiredPopulationStatistics) {

        int killedAtTS = 0;

        PersonCollection ofSexLiving = getLivingPeopleOfSex(sex, population);
        Iterator<AdvanceableDate> divDates = ofSexLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvanceableDate divDate;
        // For each division in the population data store up to the current date
        while (divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate, currentDate).getCount();
            int peopleOfAge = ofSexLiving.getNumberOfPersons(divDate, consideredTimePeriod);

            // gets death rate for people of age at the current date
            StatsKey key = new DeathStatsKey(age, peopleOfAge, consideredTimePeriod, currentDate, sex);
            DeterminedCount determinedCount = desiredPopulationStatistics.getDeterminedCount(key, config);

            // Calculate the appropriate number to kill
            Integer numberToKill = ((SingleDeterminedCount) determinedCount).getDeterminedCount();

            int killAdjust = 0;
            if (numberToKill != 0) {

                int bound = 10000;
                if (desiredPopulationStatistics.getRandomGenerator().nextInt(bound) < config.getDeathFactor() * bound) {
                    killAdjust = -1;
                }
            }

            try {
                Collection<IPerson> peopleToKill = ofSexLiving.removeNPersons(numberToKill - killAdjust, divDate, consideredTimePeriod, true);

                int killed = killPeople(peopleToKill, desiredPopulationStatistics, currentDate, consideredTimePeriod, population);
                killedAtTS += killed + killAdjust;

                // Returns the number killed to the distribution manager
                determinedCount.setFulfilledCount(killed);
                desiredPopulationStatistics.returnAchievedCount(determinedCount);

            } catch (InsufficientNumberOfPeopleException e) {
                throw new RuntimeException("Insufficient number of people to kill, - this has occurred when selecting a less than 1 proportion of a population");
            }
        }

        deathCount += killedAtTS;

        return killedAtTS;
    }

    private int killPeople(Collection<IPerson> people, PopulationStatistics desiredPopulationStatistics, AdvanceableDate currentDate, CompoundTimeUnit consideredTimePeriod, Population population) {

        int killed = 0;

        for (IPerson person : people) {

            // choose date of death
            ValipopDate deathDate = deathDateSelector.selectDate(person, desiredPopulationStatistics, currentDate, consideredTimePeriod);

            // execute death
            person.recordDeath(deathDate, desiredPopulationStatistics);
            killed++;

            // move person to correct place in data structure
            population.getDeadPeople().addPerson(person);
        }

        return killed;
    }

    private PersonCollection getLivingPeopleOfSex(SexOption sex, Population population) {

        PeopleCollection livingPeople = population.getLivingPeople();
        return sex == SexOption.MALE ? livingPeople.getMales() : livingPeople.getFemales();
    }

    private boolean needsNewPartner(IPerson person, ValipopDate currentDate) {
        return person.getPartnerships().size() == 0 || partnersToSeparate.contains(person) || lastPartnerDied(person, currentDate);
    }

    public void separate(IPartnership partnership, ValipopDate currentDate, CompoundTimeUnit consideredTimePeriod) {

        partnership.setEarliestPossibleSeparationDate(currentDate);

        partnersToSeparate.add(partnership.getFemalePartner());
        partnersToSeparate.add(partnership.getMalePartner());
    }

    Collection<IPerson> partnersToSeparate = new HashSet<>();

    private class ProposedPartnership {

        private final IPerson male;
        private final IPerson female;
        private final int numberOfChildren;

        ProposedPartnership(IPerson male, IPerson female, int numberOfChildren) {

            this.male = male;
            this.female = female;
            this.numberOfChildren = numberOfChildren;

            partnersToSeparate.remove(male);
            partnersToSeparate.remove(female);
        }
    }

     private class NewMother {

        private final IPerson newMother;
        private final int numberOfChildrenInMaternity;

         NewMother(IPerson newMother, int numberOfChildrenInMaternity) {

            this.newMother = newMother;
            this.numberOfChildrenInMaternity = numberOfChildrenInMaternity;
        }
    }

    private class MotherSet {

        private Collection<NewMother> needPartners;

        // This includes those added to existing partnerships and those marked for creation in the imminent partnering step
        private int newlyProducedChildren;

        MotherSet(Collection<NewMother> needPartners) {
            this.needPartners = needPartners;
        }

        MotherSet(Collection<NewMother> needPartners, int newlyProducedChildren) {
            this(needPartners);
            this.newlyProducedChildren = newlyProducedChildren;
        }
    }
}
