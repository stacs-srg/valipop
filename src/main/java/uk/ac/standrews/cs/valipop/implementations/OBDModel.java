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
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.FemaleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PersonCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.AnalyticsRunner;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.ContingencyTableFactory;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
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
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordGenerationFactory;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DeathDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.MarriageDateSelector;
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
    private static final int EARLIEST_AGE_OF_MARRIAGE = 16;
    private static final int MAX_ATTEMPTS = 10;
    private static final CompoundTimeUnit MAX_AGE = new CompoundTimeUnit(110, TimeUnit.YEAR);

    private static final Logger log = new Logger(OBDModel.class);

    private final Config config;
    private final SummaryRow summary;
    private final PopulationStatistics desired;
    private final Population population;
    private final PrintWriter birthOrders;
    private final RandomGenerator randomNumberGenerator;
    private final ValipopDate endOfInitPeriod;
    private final Collection<IPerson> partnersToSeparate;

    private final DateSelector birthDateSelector;
    private final DeathDateSelector deathDateSelector;
    private final DateSelector marriageDateSelector;

    private AdvanceableDate currentTime;
    private ProgramTimer simTimer;
    private int currentHypotheticalPopulationSize;

    private int birthsCount = 0;
    private int deathCount = 0;
    private int numberOfBirthsInThisTimestep = 0;

    public static void setUpFileStructureAndLogs(final String runPurpose, final String startTime, final String resultsPath) throws IOException {

        // This has to be run prior to creating the Config file as the file structure and log file need to be created
        // prior to the first logging event that occurs when generating the config
        // And errors in this method are sent to standard error

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        Logger.setLogFilePath(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath));
    }

    public OBDModel(final String startTime, final Config config) {

        this.config = config;

        currentTime = config.getTS();

        birthDateSelector = new DateSelector();
        deathDateSelector = new DeathDateSelector();
        marriageDateSelector = new MarriageDateSelector();

        partnersToSeparate = new HashSet<>();
        population = new Population(config);
        desired = new PopulationStatistics(config);

        birthOrders = FileUtils.mkDumpFile("order.csv");
        randomNumberGenerator = desired.getRandomGenerator();
        currentHypotheticalPopulationSize = calculateStartingPopulationSize();

        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        CompoundTimeUnit timeStep = new CompoundTimeUnit(desired.getOrderedBirthRates(currentTime.getYearDate()).getLargestLabel().getValue(), TimeUnit.YEAR);
        endOfInitPeriod = currentTime.advanceTime(timeStep);

        log.info("End of Initialisation Period set: " + endOfInitPeriod);

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

    public void analyseAndOutputPopulation(final boolean outputSummaryRow) {

        if (config.getOutputTables()) {
            // the 5 year step back is to combat the kick in the early stages of the CTtables for STAT - run in RStudio with no cleaning to see - potential bug in CTtree?
            ContingencyTableFactory.generateContingencyTables(population.getAllPeople(), desired, config, summary, 0, 5);
        }

        final ProgramTimer recordTimer = new ProgramTimer();
        if (config.getOutputRecordFormat() != RecordFormat.NONE) {
            RecordGenerationFactory.outputRecords(config.getOutputRecordFormat(), FileUtils.getRecordsDirPath().toString(), population.getAllPeople(), config.getT0());
        }
        summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());

        try (PrintStream resultsOutput = new PrintStream(FileUtils.getDetailedResultsPath().toFile(), "UTF-8")) {

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

    private void runSimulationAttempt() {

        while (!simulationFinished()) {
            simulationStep();
        }

        logResults();
        recordSummary();

        birthOrders.close(); // TODO why closed here when might be used in another simulation attempt?
    }

    private void simulationStep() {

        MemoryUsageAnalysis.log();
        final StringBuilder logEntry = new StringBuilder(currentTime + "\t");

        if (initialisationFinished() && populationTooSmall()) {

            cleanUpAfterUnsuccessfulAttempt();
            throw new InsufficientNumberOfPeopleException("Seed size likely too small");
        }

        if (!simulationStarted()) {
            summary.setStartPop(population.getLivingPeople().getNumberOfPeople());
        }

        final int numberBorn = createBirths();
        logEntry.append(numberBorn + "\t");

        if (!initialisationFinished() && timeFromInitialisationStartIsWholeTimeUnit()) {

            int shortFallInBirths = initialisePeople();
            logEntry.append(shortFallInBirths + "\t");

        } else if (initialisationFinished()) {
            logEntry.append(0 + "\t");
        }

        final int numberDying = createDeaths(SexOption.MALE) + createDeaths(SexOption.FEMALE);
        logEntry.append(numberDying + "\t");

        if (simulationStarted()) {
            population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
        }

        advanceSimulationTime();
        recordPeopleCounts(logEntry);

        log.info(logEntry.toString());
    }

    private void cleanUpAfterUnsuccessfulAttempt() {

        summary.setCompleted(false);

        logResults();

        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());
    }

    private int calculateStartingPopulationSize() {

        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.getTS(), config.getT0()).getCount()));
    }

    private void recordPeopleCounts(final StringBuilder logEntry) {

        logEntry.append(population.getLivingPeople().getNumberOfPeople() + "\t");
        logEntry.append(population.getDeadPeople().getNumberOfPeople());
    }

    private void advanceSimulationTime() {

        currentTime = currentTime.advanceTime(config.getSimulationTimeStep());
    }

    private void resetSimulation(final ProgramTimer simTimer) {

        summary.setCompleted(false);
        summary.setSimRunTime(simTimer.getRunTimeSeconds());
        summary.outputSummaryRowToFile();

        deathCount = 0;
        birthsCount = 0;
    }

    private int initialisePeople() {

        // calculate hypothetical number of expected births
        final CompoundTimeUnit initTimeStep = config.getSimulationTimeStep();

        final int hypotheticalBirths = calculateNumberToHaveEvent(config.getSetUpBR() * initTimeStep.toDecimalRepresentation());

        final int shortFallInBirths = hypotheticalBirths - numberOfBirthsInThisTimestep;
        numberOfBirthsInThisTimestep = 0;

        // calculate hypothetical number of expected deaths
        final int hypotheticalDeaths = calculateNumberToHaveEvent(config.getSetUpDR() * initTimeStep.toDecimalRepresentation());

        // update hypothetical population
        currentHypotheticalPopulationSize += hypotheticalBirths - hypotheticalDeaths;

        adjustPopulationNumbers(shortFallInBirths);

        return shortFallInBirths;
    }

    private void adjustPopulationNumbers(final int shortFallInBirths) {

        if (shortFallInBirths >= 0) {
            // add Orphan Children to the population
            for (int i = 0; i < shortFallInBirths; i++) {

                final IPerson person = makePersonWithRandomBirthDate(currentTime, null, false);
                population.getLivingPeople().addPerson(person);
            }
        } else {

            final int excessBirths = Math.abs(shortFallInBirths);
            final int numberOfFemalesToRemove = excessBirths / 2;
            final int numberOfMalesToRemove = excessBirths - numberOfFemalesToRemove;

            CompoundTimeUnit timeStep = config.getSimulationTimeStep();

            // TODO why is this a loop? Seems to try to remove n people n times...
            for (int i = 0; i < numberOfMalesToRemove; i++) {
                population.getLivingPeople().getMales().removeNPersons(numberOfMalesToRemove, currentTime, timeStep, true);
            }

            for (int i = 0; i < numberOfFemalesToRemove; i++) {
                population.getLivingPeople().getFemales().removeNPersons(numberOfFemalesToRemove, currentTime, timeStep, true);
            }
        }
    }

    private int createBirths() {

        int bornAtTS = 0;
        final CompoundTimeUnit consideredTimePeriod = config.getSimulationTimeStep();

        final FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        final Iterator<AdvanceableDate> divisionDates = femalesLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvanceableDate divDate;

        // For each division in the population data store up to the current date
        while (divisionDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divisionDates.next(), currentTime)) {

            final int age = DateUtils.differenceInYears(divDate.advanceTime(consideredTimePeriod), currentTime).getCount();
            final int cohortSize = femalesLiving.getAllPersonsBornInTimePeriod(divDate, consideredTimePeriod).size();

            final Set<IntegerRange> inputOrders = desired.getOrderedBirthRates(currentTime).getColumnLabels();

            for (IntegerRange order : inputOrders) {

                final Collection<IPerson> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);

                final BirthStatsKey key = new BirthStatsKey(age, order.getValue(), cohortSize, consideredTimePeriod, currentTime);
                final SingleDeterminedCount determinedCount = (SingleDeterminedCount) desired.getDeterminedCount(key, config);

                int birthAdjust = 0;
                if (determinedCount.getDeterminedCount() != 0) {

                    int adjuster = new Double(Math.ceil(config.getBirthFactor())).intValue();

                    int bound = 1000000;
                    if (desired.getRandomGenerator().nextInt(bound) < Math.abs(config.getBirthFactor() / adjuster) * bound) {

                        if (config.getBirthFactor() < 0) {
                            birthAdjust = adjuster;
                        } else {
                            birthAdjust = -1 * adjuster;
                        }
                    }
                }

                final int numberOfChildren = determinedCount.getDeterminedCount() + birthAdjust;

                // Make women into mothers

                final MotherSet mothers = selectMothers(people, numberOfChildren);

                // Partner females of age who don't have partners
                final int cancelledChildren = createPartnerships(mothers.needPartners);

                int childrenMade = mothers.newlyProducedChildren - cancelledChildren;

                bornAtTS += childrenMade;
                numberOfBirthsInThisTimestep += childrenMade;

                childrenMade = childrenMade > birthAdjust ? childrenMade - birthAdjust : 0;

                determinedCount.setFulfilledCount(childrenMade);

                birthOrders.println(currentTime.getYear() + "," + age + "," + order + "," + childrenMade + "," + numberOfChildren);

                desired.returnAchievedCount(determinedCount);
            }
        }

        birthsCount += bornAtTS;
        return bornAtTS;
    }

    private int createDeaths(final SexOption sex) {

        final CompoundTimeUnit consideredTimePeriod = config.getSimulationTimeStep();

        int killedAtTS = 0;

        final PersonCollection ofSexLiving = getLivingPeopleOfSex(sex);
        final Iterator<AdvanceableDate> divDates = ofSexLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvanceableDate divDate;
        // For each division in the population data store up to the current date
        while (divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentTime)) {

            final int age = DateUtils.differenceInYears(divDate, currentTime).getCount();
            final int peopleOfAge = ofSexLiving.getNumberOfPersons(divDate, consideredTimePeriod);

            // gets death rate for people of age at the current date
            final StatsKey key = new DeathStatsKey(age, peopleOfAge, consideredTimePeriod, currentTime, sex);
            final DeterminedCount determinedCount = desired.getDeterminedCount(key, config);

            // Calculate the appropriate number to kill
            final Integer numberToKill = ((SingleDeterminedCount) determinedCount).getDeterminedCount();

            int killAdjust = 0;
            if (numberToKill != 0) {

                int bound = 10000;
                if (desired.getRandomGenerator().nextInt(bound) < config.getDeathFactor() * bound) {
                    killAdjust = -1;
                }
            }

            try {
                final Collection<IPerson> peopleToKill = ofSexLiving.removeNPersons(numberToKill - killAdjust, divDate, consideredTimePeriod, true);

                final int killed = killPeople(peopleToKill);
                killedAtTS += killed + killAdjust;

                // Returns the number killed to the distribution manager
                determinedCount.setFulfilledCount(killed);
                desired.returnAchievedCount(determinedCount);

            } catch (InsufficientNumberOfPeopleException e) {
                throw new RuntimeException("Insufficient number of people to kill, - this has occurred when selecting a less than 1 proportion of a population");
            }
        }

        deathCount += killedAtTS;

        return killedAtTS;
    }

    private int createPartnerships(final Collection<NewMother> mothersNeedingPartners) {

        final int forNFemales = mothersNeedingPartners.size();

        if (forNFemales != 0) {

            final LinkedList<NewMother> women = new LinkedList<>(mothersNeedingPartners);

            final int age = ageOnDate(women.getFirst().newMother, currentTime);

            final PartneringStatsKey key = new PartneringStatsKey(age, forNFemales, config.getSimulationTimeStep(), currentTime);

            final MultipleDeterminedCount determinedCounts = (MultipleDeterminedCount) desired.getDeterminedCount(key, config);

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

            final LabelledValueSet<IntegerRange, Integer> availableMen = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0);

            // this section gets all the men in the age ranges we may need to look at
            final Map<IntegerRange, LinkedList<IPerson>> allMen = new TreeMap<>();

            for (IntegerRange range : partnerCounts.getLabels()) {

                final AdvanceableDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(range, currentTime);
                final CompoundTimeUnit rangeLength = getRangeLength(range);

                final LinkedList<IPerson> men = new LinkedList<>(population.getLivingPeople().getMales().getAllPersonsBornInTimePeriod(yobOfOlderEndOfIR, rangeLength));

                CollectionUtils.shuffle(men, desired.getRandomGenerator());

                allMen.put(range, men);
                availableMen.update(range, men.size());
            }

            OperableLabelledValueSet<IntegerRange, Double> shortfallCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));

            // this section redistributes the determined partner counts based on the number of available men in each age range
            while (shortfallCounts.countPositiveValues() != 0) {

                final LabelledValueSet<IntegerRange, Double> zeroedNegShortfalls = shortfallCounts.zeroNegativeValues();
                final int numberOfRangesWithSpareMen = shortfallCounts.countNegativeValues();
                final double totalShortfall = zeroedNegShortfalls.getSumOfValues();
                final double shortfallToShare = totalShortfall / (double) numberOfRangesWithSpareMen;

                partnerCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesAddNWhereCorrespondingLabelNegativeInLVS(shortfallToShare, shortfallCounts)
                        .valuesSubtractValues(zeroedNegShortfalls)).controlledRoundingMaintainingSum();

                shortfallCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen));
            }

            // TODO - upto - question: does infids affect NPA?

            final List<ProposedPartnership> proposedPartnerships = new ArrayList<>();

            // for each age range of males
            for (IntegerRange range : partnerCounts.getLabels()) {

                int determinedCount = partnerCounts.get(range);

                final LinkedList<IPerson> men = allMen.get(range);
                IPerson head = null; // keeps track of first man seen to prevent infinite loop

                final Collection<NewMother> unmatchedFemales = new ArrayList<>();

                // Keep going until enough females have been matched for this range
                while (determinedCount > 0) {

                    final IPerson man = men.pollFirst();
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
                    if (eligible(man, woman.newMother, woman.numberOfChildrenInMaternity)) {
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
                achievedPartnerCounts.add(range, partnerCounts.get(range) - determinedCount);
            }

            if (!women.isEmpty()) {
                for (int i = 0; i < women.size(); i++) {
                    final NewMother uf = women.get(i);

                    nmLoop:
                    for (IntegerRange range : partnerCounts.getLabels()) {
                        for (final IPerson man : allMen.get(range)) {
                            if (eligible(man, uf.newMother, uf.numberOfChildrenInMaternity) && !inPartnerships(man, proposedPartnerships)) {

                                proposedPartnerships.add(new ProposedPartnership(man, uf.newMother, uf.numberOfChildrenInMaternity));

                                women.remove(uf);
                                i--;
                                break nmLoop;
                            }
                        }
                    }
                }
            }

            final int cancelledChildren = getCancelledChildren(population, women);

            final LabelledValueSet<IntegerRange, Integer> returnPartnerCounts = determinedCounts.getZeroedCountsTemplate();

            final Map<Integer, List<IPerson>> partneredFemalesByChildren = new HashMap<>();

            for (final ProposedPartnership partnership : proposedPartnerships) {

                final IPerson mother = partnership.female;
                final IPerson father = partnership.male;

                final int numChildrenInPartnership = partnership.numberOfChildren;

                // Decide on marriage
                final MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, currentTime), numChildrenInPartnership, config.getSimulationTimeStep(), currentTime);
                final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desired.getDeterminedCount(marriageKey, config);

                final boolean isIllegitimate = !needsNewPartner(father, currentTime);
                final boolean toBeMarriedBirth = !isIllegitimate && (int) Math.round(marriageCounts.getDeterminedCount() / (double) numChildrenInPartnership) == 1;

                final IPartnership marriage = createPartnership(numChildrenInPartnership, father, mother, isIllegitimate, toBeMarriedBirth);

                // checks if marriage was possible
                if (marriage.getMarriageDate() != null) {
                    marriageCounts.setFulfilledCount(numChildrenInPartnership);
                } else {
                    marriageCounts.setFulfilledCount(0);
                }

                desired.returnAchievedCount(marriageCounts);

                final IntegerRange maleAgeRange = resolveAgeToIntegerRange(father, returnPartnerCounts.getLabels(), currentTime);
                returnPartnerCounts.update(maleAgeRange, returnPartnerCounts.getValue(maleAgeRange) + 1);

                addMotherToMap(partneredFemalesByChildren, mother, numChildrenInPartnership);
            }

            determinedCounts.setFulfilledCount(returnPartnerCounts);
            desired.returnAchievedCount(determinedCounts);

            separationEvent(partneredFemalesByChildren);

            return cancelledChildren;
        }

        return 0;
    }

    private IPartnership createPartnership(final int numberOfChildren, final IPerson father, final IPerson mother, final boolean illegitimate, final boolean marriedAtBirth) throws PersonNotFoundException {

        try {
            population.getLivingPeople().removePerson(mother);
            population.getLivingPeople().removePerson(father);

        } catch (PersonNotFoundException e) {
            throw new PersonNotFoundException("Could not remove parents for population position update when creating new partnership");
        }

        final IPartnership partnership = new Partnership(father, mother);

        final List<IPerson> children = new ArrayList<>(numberOfChildren);

        // This ensures twins are born on the same day
        ValipopDate childrenBirthDate = null;

        // the loop here allows for the multiple children in pregnancies
        for (int i = 0; i < numberOfChildren; i++) {

            final IPerson child;
            if (childrenBirthDate == null) {
                // Make first child
                child = makePersonWithRandomBirthDate(currentTime, partnership, illegitimate);
                population.getLivingPeople().addPerson(child);

            } else {
                // Make subsequent children
                child = makePerson(childrenBirthDate, partnership, illegitimate);
                population.getLivingPeople().addPerson(child);
            }

            childrenBirthDate = child.getBirthDate();
            children.add(child);
        }

        if (marriedAtBirth) {

            // nth child - then previous child birth date - NOT possible here, this is a method for new partnerships
            // first child - then death or divorce of previous spouses or coming of age

            // for mother
            final ValipopDate motherLastPrevPartneringEvent = getDateOfLastLegitimatePartnershipEventBeforeDate(mother, childrenBirthDate);

            // for father
            final ValipopDate fatherLastPrevPartneringEvent = getDateOfLastLegitimatePartnershipEventBeforeDate(father, childrenBirthDate);

            final ValipopDate earliestPossibleMarriageDate = DateUtils.getLatestDate(motherLastPrevPartneringEvent, fatherLastPrevPartneringEvent);

            if (DateUtils.dateBefore(earliestPossibleMarriageDate, childrenBirthDate)) {
                // if there is a tenable marriage date then select it
                partnership.setMarriageDate(marriageDateSelector.selectDate(earliestPossibleMarriageDate, childrenBirthDate, desired.getRandomGenerator()));
            } else {
                partnership.setMarriageDate(null);
            }

        } else {
            partnership.setMarriageDate(null);
        }

        partnership.setPartnershipDate(childrenBirthDate);
        partnership.addChildren(children);

        population.getLivingPeople().addPartnershipToIndex(partnership);

        mother.recordPartnership(partnership);
        father.recordPartnership(partnership);

        // re-insert parents into population, this allows their position in the data structure to be updated
        population.getLivingPeople().addPerson(mother);
        population.getLivingPeople().addPerson(father);

        return partnership;
    }

    private ValipopDate getDateOfLastLegitimatePartnershipEventBeforeDate(final IPerson person, final ValipopDate date) {

        ValipopDate latestDate = getEarliestMarriageDate(person);

        for (final IPartnership partnership : person.getPartnerships()) {
            if (DateUtils.dateBefore(partnership.getPartnershipDate(), date)) {
                final List<IPerson> children = partnership.getChildren();
                if (!children.isEmpty()) {
                    if (!children.get(0).isIllegitimate()) {
                        // this partnership has legitimate children

                        // thus check separation date
                        final ValipopDate separationDate = partnership.getEarliestPossibleSeparationDate();
                        if (separationDate != null && DateUtils.dateBefore(latestDate, separationDate)) {
                            latestDate = separationDate;
                        }

                        // partner death date
                        final ValipopDate partnerDeath = partnership.getPartnerOf(person).getDeathDate();
                        if (partnerDeath != null && DateUtils.dateBefore(latestDate, partnerDeath)) {
                            latestDate = partnerDeath;
                        }
                    }
                } else {
                    System.err.println("Do we now have childless marriages? - If so write this code!");
                }
            }
        }

        return latestDate;
    }

    private ValipopDate getEarliestMarriageDate(IPerson person) {

        final ValipopDate birthDate = person.getBirthDate();

        // Handle the leap year baby...
        final ValipopDate temp = birthDate.getMonthDate().advanceTime(EARLIEST_AGE_OF_MARRIAGE, TimeUnit.YEAR);

        if (temp.getMonth() == DateUtils.FEB && !DateUtils.isLeapYear(temp.getYear()) && birthDate.getDay() == DateUtils.DAYS_IN_LEAP_FEB) {
            return new ExactDate(birthDate.getDay() - 1, temp.getMonth(), temp.getYear());
        } else {
            return new ExactDate(birthDate.getDay(), temp.getMonth(), temp.getYear());
        }
    }

    private static SexOption getSex(final PopulationCounts counts, final PopulationStatistics statistics, final ValipopDate currentDate) {

        final double sexBalance = counts.getAllTimeSexRatio();

        if (sexBalance < statistics.getMaleProportionOfBirths(currentDate)) {

            counts.newMale();
            return SexOption.MALE;

        } else {

            counts.newFemale();
            return SexOption.FEMALE;
        }
    }

    private IPerson makePerson(final ValipopDate birthDate, final IPartnership parents, final boolean illegitimate) {

        SexOption sex = getSex(population.getPopulationCounts(), desired, birthDate);
        return new Person(sex, birthDate, parents, desired, illegitimate);
    }

    private IPerson makePersonWithRandomBirthDate(final ValipopDate currentDate, final IPartnership parents, final boolean illegitimate) {

        final ValipopDate birthDate = birthDateSelector.selectDate(currentDate, config.getSimulationTimeStep(), desired.getRandomGenerator());
        return makePerson(birthDate, parents, illegitimate);
    }

    private boolean populationTooSmall() {

        return population.getLivingPeople().getAll().size() < MINIMUM_POPULATION_SIZE;
    }

    private boolean initialisationFinished() {
        return !inInitPeriod(currentTime);
    }

    private boolean inInitPeriod(ValipopDate currentTime) {
        return DateUtils.dateBeforeOrEqual(currentTime, endOfInitPeriod);
    }

    private boolean simulationStarted() {
        return !DateUtils.dateBeforeOrEqual(currentTime, config.getT0());
    }

    private boolean simulationFinished() {
        return !DateUtils.dateBeforeOrEqual(currentTime, config.getTE());
    }

    private boolean timeFromInitialisationStartIsWholeTimeUnit() {
        return DateUtils.matchesInterval(currentTime, config.getSimulationTimeStep(), config.getTS());
    }

    private MotherSet selectMothers(final Collection<IPerson> females, final int numberOfChildren) {

        if (females.size() > 0) {

            final Collection<NewMother> needPartners = new ArrayList<>();

            // TODO adjust this to also permit age variations
            final List<IPerson> femalesAL = new ArrayList<>(females);

            final int ageOfMothers = ageOnDate(femalesAL.get(0), currentTime);

            final MultipleDeterminedCount requiredBirths = calcNumberOfPregnanciesOfMultipleBirth(ageOfMothers, numberOfChildren);

            final LabelledValueSet<IntegerRange, Integer> motherCountsByMaternities = new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().getLabels(), 0);

            final OperableLabelledValueSet<IntegerRange, Integer> remainingMothersToFind = new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().clone());

            try {
                IntegerRange highestBirthOption = remainingMothersToFind.getLargestLabelOfNoneZeroValue();

                CollectionUtils.shuffle(femalesAL, desired.getRandomGenerator());

                int childrenMade = 0;
                final Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren = new HashMap<>();

                for (final IPerson female : femalesAL) {

                    if (childrenMade >= numberOfChildren) {
                        break;
                    }

                    if (eligible(female)) {

                        childrenMade += highestBirthOption.getValue();

                        if (needsNewPartner(female, currentTime)) {
                            needPartners.add(new NewMother(female, highestBirthOption.getValue()));

                        } else {

                            addChildrenToCurrentPartnership(female, highestBirthOption.getValue());
                            int numberOfChildrenInLatestPartnership = numberOfChildrenInLatestPartnership(female);

                            try {
                                continuingPartneredFemalesByChildren.get(numberOfChildrenInLatestPartnership).add(female);

                            } catch (NullPointerException e) {
                                continuingPartneredFemalesByChildren.put(numberOfChildrenInLatestPartnership, new ArrayList<>(Collections.singleton(female)));
                            }
                        }

                        // updates count of remaining mothers to find
                        final int furtherMothersNeededForMaternitySize = remainingMothersToFind.get(highestBirthOption) - 1;
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

                separationEvent(continuingPartneredFemalesByChildren);

                requiredBirths.setFulfilledCount(motherCountsByMaternities);
                desired.returnAchievedCount(requiredBirths);

                return new MotherSet(needPartners, childrenMade);

            } catch (NoSuchElementException e) {
                return new MotherSet(needPartners);
            }
        } else return new MotherSet();
    }

    private MultipleDeterminedCount calcNumberOfPregnanciesOfMultipleBirth(final int ageOfMothers, final int numberOfChildren) {

        final MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, config.getSimulationTimeStep(), currentTime);
        return (MultipleDeterminedCount) desired.getDeterminedCount(key, config);
    }

    private boolean eligible(final IPerson potentialMother) {

        final IPerson lastChild = PopulationNavigation.getLastChild(potentialMother);

        if (lastChild != null) {

            final ExactDate earliestDateOfNextChild = DateUtils.calculateExactDate(lastChild.getBirthDate(), desired.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentTime
            return DateUtils.dateBefore(earliestDateOfNextChild, currentTime);

        } else {
            // i.e. there is no previous child and thus no limitation to birth
            return true;
        }
    }

    private boolean eligible(final IPerson man, final IPerson woman, final int childrenInPregnancy) {

        population.getPopulationCounts().incEligibilityCheck();

        final boolean eligible = maleAvailable(man, childrenInPregnancy) && legallyEligibleToMarry(man, woman);

        if (!eligible) {
            population.getPopulationCounts().incFailedEligibilityCheck();
        }

        return eligible;
    }

    private void addChildrenToCurrentPartnership(final IPerson mother, final int numberOfChildren) {

        population.getLivingPeople().removePerson(mother);

        final IPerson lastChild = PopulationNavigation.getLastChild(mother);
        final IPartnership last = lastChild.getParents();

        ValipopDate birthDate = null;

        final IPerson man = last.getMalePartner();

        for (int i = 0; i < numberOfChildren; i++) {

            IPerson child;

            if (birthDate == null) {

                child = makePersonWithRandomBirthDate(currentTime, last, false);
                birthDate = child.getBirthDate();

            } else {

                child = makePerson(currentTime, last, false);
            }

            last.addChildren(Collections.singleton(child));
            population.getLivingPeople().addPerson(child);
        }

        // record that child is legitimate
        final IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(ageOnDate(man, birthDate), numberOfChildren, config.getSimulationTimeStep(), birthDate);
        final SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desired.getDeterminedCount(illegitimateKey, config);
        illegitimateCounts.setFulfilledCount(0);
        desired.returnAchievedCount(illegitimateCounts);

        // decide if to cause marriage
        // Decide on marriage
        final MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, birthDate), numberOfChildren, config.getSimulationTimeStep(), birthDate);
        final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desired.getDeterminedCount(marriageKey, config);

        if (last.getMarriageDate() != null) {
            // is already married - so return as married
            marriageCounts.setFulfilledCount(numberOfChildren);

        } else {
            final boolean toBeMarriedBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numberOfChildren) == 1;

            if (toBeMarriedBirth) {
                marriageCounts.setFulfilledCount(numberOfChildren);
                last.setMarriageDate(marriageDateSelector.selectDate(lastChild.getBirthDate(), birthDate, desired.getRandomGenerator()));
            } else {
                marriageCounts.setFulfilledCount(0);
            }
        }
        desired.returnAchievedCount(marriageCounts);

        population.getLivingPeople().addPerson(mother);
    }

    private void addMotherToMap(final Map<Integer, List<IPerson>> partneredFemalesByChildren, final IPerson mother, final int numChildrenInPartnership) {

        if (partneredFemalesByChildren.containsKey(numChildrenInPartnership)) {

            partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
        } else {
            partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>(Collections.singleton(mother)));
        }
    }

    private int getCancelledChildren(final Population population, final List<NewMother> women) {

        int cancelledChildren = 0;

        if (!women.isEmpty()) {
            for (final NewMother m : women) {

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

    private IntegerRange resolveAgeToIntegerRange(final IPerson male, final Set<IntegerRange> labels, final ValipopDate currentDate) {

        final int age = ageOnDate(male, currentDate);

        for (IntegerRange range : labels) {
            if (range.contains(age)) {
                return range;
            }
        }

        throw new InvalidRangeException("Male does not fit in expected ranges...");
    }

    private boolean inPartnerships(final IPerson person, final List<ProposedPartnership> partnerships) {

        for (final ProposedPartnership partnership : partnerships) {
            if (partnership.male == person || partnership.female == person) {
                return true;
            }
        }
        return false;
    }

    private boolean maleAvailable(final IPerson man, final int childrenInPregnancy) {

        // in the init period any partnering is allowed
        if (DateUtils.dateBeforeOrEqual(currentTime, new YearDate(1791))) {
            return true;
        }

        // Get access to illegitimacy rates
        final IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(ageOnDate(man, currentTime), childrenInPregnancy, config.getSimulationTimeStep(), currentTime);
        final SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desired.getDeterminedCount(illegitimateKey, config);
        final int permitted = (int) Math.round(illegitimateCounts.getDeterminedCount() / (double) childrenInPregnancy);

        if (needsNewPartner(man, currentTime)) {
            // record the legitimate birth
            illegitimateCounts.setFulfilledCount(0);
            desired.returnAchievedCount(illegitimateCounts);
            return true;
        }

        if (permitted == 1) {
            // record the illegitimate birth
            illegitimateCounts.setFulfilledCount(childrenInPregnancy);
            desired.returnAchievedCount(illegitimateCounts);
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

    private void exclude(final Collection<IPerson> collection, final IPerson person) {
        if (collection.contains(person)) throw new RuntimeException();
    }

    private CompoundTimeUnit getRangeLength(final IntegerRange range) {

        return new CompoundTimeUnit(range.getMax() - range.getMin() + 1, TimeUnit.YEAR);
    }

    private AdvanceableDate getYobOfOlderEndOfIR(final IntegerRange range, final ValipopDate currentDate) {

        return new YearDate(currentDate.getYear() - range.getMax() - 1);
    }

    private void separationEvent(final Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren) {

        // Consideration of separation is based on number of children in females current partnerships
        for (final Map.Entry<Integer, List<IPerson>> entry : continuingPartneredFemalesByChildren.entrySet()) {

            final Integer numberOfChildren = entry.getKey();
            int ageOfMothers = 0;

            // Get mothers with given number of children in current partnership
            final List<IPerson> mothers = entry.getValue();

            if (mothers.size() != 0) {
                ageOfMothers = ageOnDate(mothers.get(0), currentTime);
            }

            // Get determined count for separations for this group of mothers
            final SeparationStatsKey key = new SeparationStatsKey(numberOfChildren, ageOfMothers, mothers.size(), config.getSimulationTimeStep(), currentTime);
            final SingleDeterminedCount dC = (SingleDeterminedCount) desired.getDeterminedCount(key, config);

            int count = 0;

            // For each mother in this group
            for (final IPerson mother : mothers) {

                // If enough mothers have been separated then break
                if (count >= dC.getDeterminedCount()) {
                    break;
                }

                // else mark partnership for separation
                separate(getLastPartnership(mother), getLastChild(mother).getBirthDate());

                count++;
            }

            // Return achieved statistics to the statistics handler
            dC.setFulfilledCount(count);
            desired.returnAchievedCount(dC);
        }
    }

    private int killPeople(final Collection<IPerson> people) {

        int killed = 0;

        for (final IPerson person : people) {

            // choose date of death
            final ValipopDate deathDate = deathDateSelector.selectDate(person, desired, currentTime, config.getSimulationTimeStep());

            // execute death
            person.recordDeath(deathDate, desired);
            killed++;

            // move person to correct place in data structure
            population.getDeadPeople().addPerson(person);
        }

        return killed;
    }

    private PersonCollection getLivingPeopleOfSex(final SexOption sex) {

        final PeopleCollection livingPeople = population.getLivingPeople();
        return sex == SexOption.MALE ? livingPeople.getMales() : livingPeople.getFemales();
    }

    private boolean needsNewPartner(final IPerson person, final ValipopDate currentDate) {

        return person.getPartnerships().size() == 0 || partnersToSeparate.contains(person) || lastPartnerDied(person, currentDate);
    }

    private void separate(final IPartnership partnership, final ValipopDate currentDate) {

        partnership.setEarliestPossibleSeparationDate(currentDate);

        partnersToSeparate.add(partnership.getFemalePartner());
        partnersToSeparate.add(partnership.getMalePartner());
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

        log.info("TKilled\t" + deathCount);
        log.info("TBorn\t" + birthsCount);
        log.info("Ratio\t" + deathCount / (double) birthsCount);
    }

    private int calculateNumberToHaveEvent(final double eventRate) {

        double toHaveEvent = currentHypotheticalPopulationSize * eventRate;
        int flooredToHaveEvent = (int) toHaveEvent;
        toHaveEvent -= flooredToHaveEvent;

        // this is a random dice roll to see if the fraction of a has the event or not

        if (randomNumberGenerator.nextInt(100) < toHaveEvent * 100) {
            flooredToHaveEvent++;
        }

        return flooredToHaveEvent;
    }

    private class ProposedPartnership {

        private final IPerson male;
        private final IPerson female;
        private final int numberOfChildren;

        ProposedPartnership(final IPerson male, final IPerson female, final int numberOfChildren) {

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

        NewMother(final IPerson newMother, final int numberOfChildrenInMaternity) {

            this.newMother = newMother;
            this.numberOfChildrenInMaternity = numberOfChildrenInMaternity;
        }
    }

    private class MotherSet {

        private Collection<NewMother> needPartners;

        // This includes those added to existing partnerships and those marked for creation in the imminent partnering step
        private int newlyProducedChildren;

        MotherSet() {
            this(new ArrayList<>());
        }

        MotherSet(final Collection<NewMother> needPartners) {
            this.needPartners = needPartners;
        }

        MotherSet(final Collection<NewMother> needPartners, final int newlyProducedChildren) {
            this(needPartners);
            this.newlyProducedChildren = newlyProducedChildren;
        }
    }
}
