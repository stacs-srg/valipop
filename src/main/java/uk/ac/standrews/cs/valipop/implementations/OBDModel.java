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
import uk.ac.standrews.cs.valipop.simulationEntities.*;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.populationAnalytics.AnalyticsRunner;
import uk.ac.standrews.cs.valipop.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.ContingencyTableFactory;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.MarriageStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCountByIR;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.*;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.addressLookup.*;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordGenerationFactory;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DeathDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.MarriageDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.*;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.*;

import static uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    // TODO use more informative class name

    private static final String CODE_VERSION = "dev-bf";
    private static final int MINIMUM_POPULATION_SIZE = 100;
    private static final int EARLIEST_AGE_OF_MARRIAGE = 16;
    private static final int MAX_ATTEMPTS = 1;
    public static final Period MAX_AGE = Period.ofYears(110);

    private static Logger log;

    static {
//        int limit = 1000 * 1000; // 1 Mb
//        int numLogFiles = 2;
//        FileHandler fh = null;
//        try {
//            fh = new FileHandler("trace.txt", limit, numLogFiles);
//        } catch (IOException e) {
//            throw new RuntimeException("Logger failed to initialise");
//        }

        log = Logger.getLogger(OBDModel.class.getName());
//        log.addHandler(fh);
        log.setLevel(Level.INFO);



    }

    private final Geography geography;
    private static final int BIRTH_ADJUSTMENT_BOUND = 1000000;

    private final Config config;
    private final SummaryRow summary;
    private final PopulationStatistics desired;
    private final Population population;
    private final RandomGenerator randomNumberGenerator;
    private final LocalDate endOfInitPeriod;
    private final Collection<IPerson> partnersToSeparate;

    private final DeathDateSelector deathDateSelector;
    private final DateSelector marriageDateSelector;

    private final DistanceSelector moveDistanceSelector;

    private LocalDate currentTime;
    private ProgramTimer simTimer;
    private int currentHypotheticalPopulationSize;

    private int birthsCount = 0;
    private int deathCount = 0;
    private int numberOfBirthsInThisTimestep = 0;

    private PrintWriter birthOrders;

    private final PersonFactory personFactory;
    private final BalancedMigrationModel migrationModel;
    private final OccupationChangeModel occupationChangeModel;

    public OBDModel(final Config config) {

        try {
            this.config = config;

            currentTime = config.getTS();

            partnersToSeparate = new HashSet<>();
            population = new Population(config);
            desired = new PopulationStatistics(config);

            try {
                geography = new Geography(Cache.readFromFile(config.getGeographyFilePath().toString()), desired.getRandomGenerator());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error initialising geography", e);
            }

            birthOrders = new PrintWriter(config.getBirthOrdersPath().toFile());
            randomNumberGenerator = desired.getRandomGenerator();
            currentHypotheticalPopulationSize = calculateStartingPopulationSize();

            deathDateSelector = new DeathDateSelector(randomNumberGenerator);
            marriageDateSelector = new MarriageDateSelector(randomNumberGenerator);

            moveDistanceSelector = new DistanceSelector(randomNumberGenerator);

            personFactory = new PersonFactory(population, desired, config.getSimulationTimeStep(), randomNumberGenerator);
            migrationModel = new BalancedMigrationModel(population, randomNumberGenerator, geography, personFactory, desired);
            occupationChangeModel = new OccupationChangeModel(population, desired, config);

            log.info("Random seed: " + config.getSeed());
            log.info("Population seed size: " + config.getT0PopulationSize());
            log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

            Period timeStep = Period.ofYears(desired.getOrderedBirthRates(Year.of(currentTime.getYear())).getLargestLabel().getValue());
            endOfInitPeriod = currentTime.plus(timeStep);

            log.info("End of Initialisation Period set: " + endOfInitPeriod);

            summary = new SummaryRow(config, CODE_VERSION, JobQueueRunner.execCmd("hostname").trim());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void analyseAndOutputPopulation(final boolean outputSummaryRow, final int stepBack) {

        if (config.getOutputTables()) {
            // the 5 year step back is to combat the kick in the early stages of the CTtables for STAT - run in RStudio with no cleaning to see - potential bug in CTtree?
            ContingencyTableFactory.generateContingencyTables(population.getPeople(), desired, config, summary);
        }

        final ProgramTimer recordTimer = new ProgramTimer();

        if (config.getOutputRecordFormat() != RecordFormat.NONE) {
            RecordGenerationFactory.outputRecords(config.getOutputRecordFormat(), config.getRecordsDirPath(), population.getPeople(), config.getT0());
        }

        summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());

        try (PrintStream resultsOutput = new PrintStream(config.getDetailedResultsPath().toFile(), "UTF-8")) {

            AnalyticsRunner.runAnalytics(population.getPeople(config.getT0(), config.getTE(), MAX_AGE), resultsOutput);

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

        finalisePartnerships();

        logResults();
        recordSummary();

        closeLogFile();
        birthOrders.close(); // QUESTION why closed here when might be used in another simulation attempt? - TD: Sepertate file should be produced for each simulation
    }

    private void closeLogFile() {
        for(Handler h : log.getHandlers()) {
            h.close();
        }
        LogManager.getLogManager().reset();

        log = Logger.getLogger(OBDModel.class.getName());
        log.setLevel(Level.INFO);
    }

    private void finalisePartnerships() {
        for(IPerson person : population.getPeople()) {
            for(IPartnership partnership : person.getPartnerships()) {
                if(!partnership.isFinalised()) {
                    handleSeperationMoves(partnership, person);
                }
            }
        }
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
        birthsCount += numberBorn;
        numberOfBirthsInThisTimestep += numberBorn;

        logEntry.append(numberBorn).append("\t");

        if (!initialisationFinished() && timeFromInitialisationStartIsWholeTimeUnit()) {

            final int shortFallInBirths = adjustPopulationNumbers();
            logEntry.append(shortFallInBirths).append("\t");

        } else if (initialisationFinished()) {
            logEntry.append(0 + "\t");
        }

        final int numberDying = createDeaths(SexOption.MALE) + createDeaths(SexOption.FEMALE);
        deathCount += numberDying;
        logEntry.append(numberDying).append("\t");

        migrationModel.performMigration(currentTime, this);

        occupationChangeModel.performOccupationChange(currentTime);

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

        closeLogFile();
        birthOrders.close();
    }

    private int calculateStartingPopulationSize() {

        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, Period.between(config.getTS(), config.getT0()).getYears()));
    }

    private void recordPeopleCounts(final StringBuilder logEntry) {

        logEntry.append(population.getLivingPeople().getNumberOfPeople()).append("\t");
        logEntry.append(population.getDeadPeople().getNumberOfPeople());
    }

    private void advanceSimulationTime() {

        currentTime = currentTime.plus(config.getSimulationTimeStep());
    }

    private void resetSimulation(final ProgramTimer simTimer) {

        summary.setCompleted(false);
        summary.setSimRunTime(simTimer.getRunTimeSeconds());
        summary.outputSummaryRowToFile();

        deathCount = 0;
        birthsCount = 0;
    }

    private double toDecimalRepresentation(Period period) {

        return period.toTotalMonths() / (double) DateUtils.MONTHS_IN_YEAR;
    }

    private int adjustPopulationNumbers() {

        // calculate hypothetical number of expected births
        final Period initTimeStep = config.getSimulationTimeStep();

        final int hypotheticalBirths = calculateNumberToHaveEvent(config.getSetUpBR() * toDecimalRepresentation(initTimeStep));

        final int shortFallInBirths = hypotheticalBirths - numberOfBirthsInThisTimestep;
        numberOfBirthsInThisTimestep = 0;

        // calculate hypothetical number of expected deaths
        final int hypotheticalDeaths = calculateNumberToHaveEvent(config.getSetUpDR() * toDecimalRepresentation(initTimeStep));

        // update hypothetical population
        currentHypotheticalPopulationSize += hypotheticalBirths - hypotheticalDeaths;

        adjustPopulationNumbers(shortFallInBirths);

        return shortFallInBirths;
    }

    private void adjustPopulationNumbers(final int shortFallInBirths) {

        if (shortFallInBirths >= 0) {
            createOrphanChildren(shortFallInBirths);
        } else {
            // REMOVED BY TOM - CHECKING THIS - but think this should rather be later handled by self correction mechanisms
            removePeople(-shortFallInBirths);
        }

    }

    private void removePeople(final int excessBirths) {

        final int numberOfFemalesToRemove = excessBirths / 2;
        final int numberOfMalesToRemove = excessBirths - numberOfFemalesToRemove;

        final Period timeStep = config.getSimulationTimeStep();

        // TODO why is this a loop? Seems to try to remove n people n times...
        for (int i = 0; i < numberOfMalesToRemove; i++) {
            population.getLivingPeople().removeMales(numberOfMalesToRemove, currentTime, timeStep, true, geography, moveDistanceSelector, config);
//            population.getLivingPeople().getMales().removeNPersons(numberOfMalesToRemove, currentTime, timeStep, true);
        }

        for (int i = 0; i < numberOfFemalesToRemove; i++) {
            population.getLivingPeople().removeFemales(numberOfFemalesToRemove, currentTime, timeStep, true, geography, moveDistanceSelector, config);
//            population.getLivingPeople().getFemales().removeNPersons(numberOfFemalesToRemove, currentTime, timeStep, true);
        }
    }

    private void createOrphanChildren(final int shortFallInBirths) {

        for (int i = 0; i < shortFallInBirths; i++) {

            final IPerson person = personFactory.makePersonWithRandomBirthDate(currentTime, null, false);
            population.getLivingPeople().add(person);
        }
    }

    private int createBirths() {

        final FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        final Set<LocalDate> divisionDates = femalesLiving.getDivisionDates(config.getSimulationTimeStep());

        int count = 0;

        // For each division in the population data store up to the current date
        for (final LocalDate divisionDate : divisionDates) {

            if (divisionDate.isAfter(currentTime)) break;
            count += getBornAtTS(femalesLiving, divisionDate);
        }

        return count;
    }

    private int getBornAtTS(final FemaleCollection femalesLiving, final LocalDate divisionDate) {

        final Period consideredTimePeriod = config.getSimulationTimeStep();

        final int age = Period.between(divisionDate.plus(consideredTimePeriod), currentTime).getYears();
        final int cohortSize = femalesLiving.getPeopleBornInTimePeriod(divisionDate, consideredTimePeriod).size();

        final Set<IntegerRange> birthOrders = desired.getOrderedBirthRates(Year.of(currentTime.getYear())).getColumnLabels();

        int count = 0;

        for (final IntegerRange birthOrder : birthOrders) {
            count += getBornInRange(femalesLiving, divisionDate, age, cohortSize, birthOrder);
        }

        return count;
    }

    private int getBornInRange(final FemaleCollection femalesLiving, final LocalDate divisionDate, final int age, final int cohortSize, final IntegerRange birthOrder) {

        final Period consideredTimePeriod = config.getSimulationTimeStep();

        final List<IPerson> people = new ArrayList<>(femalesLiving.getByDatePeriodAndBirthOrder(divisionDate, consideredTimePeriod, birthOrder));

        final BirthStatsKey key = new BirthStatsKey(age, birthOrder.getValue(), cohortSize, consideredTimePeriod, currentTime);
        final SingleDeterminedCount determinedCount = (SingleDeterminedCount) desired.getDeterminedCount(key, config);

        final int birthAdjustment = getBirthAdjustment(determinedCount);
        final int numberOfChildren = determinedCount.getDeterminedCount() + birthAdjustment;

        // Make women into mothers

        final MothersNeedingPartners mothersNeedingPartners = selectMothers(people, numberOfChildren);

        // Partner females of age who don't have partners
        final int cancelledChildren = createPartnerships(mothersNeedingPartners.mothers);
        final int childrenMade = mothersNeedingPartners.newlyProducedChildren - cancelledChildren;
        final int fulfilled = childrenMade > birthAdjustment ? childrenMade - birthAdjustment : 0;

        determinedCount.setFulfilledCount(fulfilled);

        // TODO Does this output get used? TD: If I remember correctly it allows us to explain why it takes longer to generate small populations - may be useful to talk about in thesis...
        birthOrders.println(currentTime.getYear() + "," + age + "," + birthOrder + "," + fulfilled + "," + numberOfChildren);

        desired.returnAchievedCount(determinedCount);
        return childrenMade;
    }

    private int getBirthAdjustment(final SingleDeterminedCount determinedCount) {

        if (determinedCount.getDeterminedCount() != 0) {

            final double birthFactor = config.getBirthFactor();
            final int adjuster = (int) Math.ceil(birthFactor);

            if (desired.getRandomGenerator().nextInt(BIRTH_ADJUSTMENT_BOUND) < Math.abs(birthFactor / adjuster) * BIRTH_ADJUSTMENT_BOUND) {
                return (birthFactor < 0) ? adjuster : -adjuster;
            }
        }
        return 0;
    }

    private int createDeaths(final SexOption sex) {

        int killedAtTS = 0;

        final PersonCollection ofSexLiving = getLivingPeopleOfSex(sex);
        final Set<LocalDate> divisionDates = ofSexLiving.getDivisionDates(config.getSimulationTimeStep());

        // For each division in the population data store up to the current date
        for (final LocalDate divisionDate : divisionDates) {

            if (divisionDate.isAfter(currentTime)) break;
            killedAtTS += getKilledAtTS(sex, ofSexLiving, divisionDate);
        }

        return killedAtTS;
    }

    private int getKilledAtTS(final SexOption sex, final PersonCollection ofSexLiving, final LocalDate divisionDate) {

        final Period consideredTimePeriod = config.getSimulationTimeStep();

        final int age = Period.between(divisionDate, currentTime).getYears();
        final int peopleOfAge = ofSexLiving.getNumberOfPeople(divisionDate, consideredTimePeriod);

        // gets death rate for people of age at the current date
        final StatsKey key = new DeathStatsKey(age, peopleOfAge, consideredTimePeriod, currentTime, sex);
        final DeterminedCount determinedCount = desired.getDeterminedCount(key, config);

        // Calculate the appropriate number to kill
        final Integer numberToKill = ((SingleDeterminedCount) determinedCount).getDeterminedCount();

        int killAdjust = 0;
        if (numberToKill != 0) {

            // TODO fix magic number

            int bound = 10000;
            if (desired.getRandomGenerator().nextInt(bound) < config.getDeathFactor() * bound) {
                killAdjust = -1;
            }
        }

        final Collection<IPerson> peopleToKill = ofSexLiving.removeNPersons(numberToKill - killAdjust, divisionDate, consideredTimePeriod, true);

        final int killed = killPeople(peopleToKill);

        // Returns the number killed to the distribution manager
        determinedCount.setFulfilledCount(killed);
        desired.returnAchievedCount(determinedCount);

        return killed + killAdjust;
    }

    private int createPartnerships(final Collection<NewMother> mothersNeedingPartners) {

        if (mothersNeedingPartners.size() == 0) return 0;

        final LinkedList<NewMother> women = new LinkedList<>(mothersNeedingPartners);

        final int age = ageOnDate(women.getFirst().newMother, currentTime);

        final PartneringStatsKey key = new PartneringStatsKey(age, mothersNeedingPartners.size(), config.getSimulationTimeStep(), currentTime);

        final MultipleDeterminedCountByIR determinedCounts = (MultipleDeterminedCountByIR) desired.getDeterminedCount(key, config);

        final OperableLabelledValueSet<IntegerRange, Integer> partnerCounts = new IntegerRangeToIntegerSet(determinedCounts.getDeterminedCount(), randomNumberGenerator);
        final LabelledValueSet<IntegerRange, Integer> achievedPartnerCounts = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0, randomNumberGenerator);
        final LabelledValueSet<IntegerRange, Integer> availableMen = new IntegerRangeToIntegerSet(partnerCounts.getLabels(), 0, randomNumberGenerator);

        final Map<IntegerRange, LinkedList<IPerson>> menMap = getAllMen(partnerCounts, availableMen);
        final OperableLabelledValueSet<IntegerRange, Integer> redistributedPartnerCounts = redistributePartnerCounts(partnerCounts, availableMen);

        // TODO - upto - question: does infids affect NPA?

        final List<ProposedPartnership> proposedPartnerships = getProposedPartnerships(women, menMap, redistributedPartnerCounts, achievedPartnerCounts);

        findPartners(women, menMap, redistributedPartnerCounts, proposedPartnerships);

        final int cancelledChildren = removeLastPartners(population, women);

        separationEvent(getPartneredFemalesByChildren(determinedCounts, proposedPartnerships));

        return cancelledChildren;
    }

    private Map<Integer, List<IPerson>> getPartneredFemalesByChildren(final MultipleDeterminedCountByIR determinedCounts, final List<ProposedPartnership> proposedPartnerships) {

        final LabelledValueSet<IntegerRange, Integer> returnPartnerCounts = determinedCounts.getZeroedCountsTemplate(randomNumberGenerator);
        final Map<Integer, List<IPerson>> partneredFemalesByChildren = new HashMap<>();

        for (final ProposedPartnership partnership : proposedPartnerships) {

            setUpPartnership(partnership, returnPartnerCounts);
            addMotherToMap(partnership, partneredFemalesByChildren);
        }

        determinedCounts.setFulfilledCount(returnPartnerCounts);
        desired.returnAchievedCount(determinedCounts);
        return partneredFemalesByChildren;
    }

    private void setUpPartnership(final ProposedPartnership partnership, final LabelledValueSet<IntegerRange, Integer> partnerCounts) {

        final IPerson mother = partnership.female;
        final IPerson father = partnership.male;

        final int numChildrenInPartnership = partnership.numberOfChildren;

        // Decide on marriage
        final MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, currentTime), numChildrenInPartnership, config.getSimulationTimeStep(), currentTime);
        final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desired.getDeterminedCount(marriageKey, config);

        final boolean isIllegitimate = !needsNewPartner(father, currentTime);
        final boolean marriedAtBirth = !isIllegitimate && (int) Math.round(marriageCounts.getDeterminedCount() / (double) numChildrenInPartnership) == 1;

        final IPartnership marriage = createNewPartnership(numChildrenInPartnership, father, mother, isIllegitimate, marriedAtBirth);

        marriageCounts.setFulfilledCount(marriage.getMarriageDate() != null ? numChildrenInPartnership : 0);
        desired.returnAchievedCount(marriageCounts);

        final IntegerRange maleAgeRange = resolveAgeToIntegerRange(father, partnerCounts.getLabels(), currentTime);
        partnerCounts.update(maleAgeRange, partnerCounts.getValue(maleAgeRange) + 1);
    }

    private void handleAddressChanges(IPartnership marriage) {

        final IPerson mother = marriage.getFemalePartner();
        final IPerson father = marriage.getMalePartner();

        Address lastMaleAddress = marriage.getMalePartner().getAddress(LocalDate.MAX);
        Address lastFemaleAddress = marriage.getFemalePartner().getAddress(LocalDate.MAX);

        LocalDate moveDate = marriage.getMarriageDate() != null ? marriage.getMarriageDate() : marriage.getPartnershipDate();
        double moveDistance = moveDistanceSelector.selectRandomDistance();

        Address newAddress;

        if(lastMaleAddress == null) {
            if(lastFemaleAddress == null) {
                newAddress = geography.getRandomEmptyAddress();
            } else {
                newAddress = geography.getNearestEmptyAddressAtDistance(lastFemaleAddress.getArea().getCentriod(), moveDistance);
            }
        } else {
            if(lastFemaleAddress == null) {
                newAddress = geography.getNearestEmptyAddressAtDistance(lastMaleAddress.getArea().getCentriod(), moveDistance);
            } else {
                // both already have address, so flip coin to decide who acts as origin for move
                if(randomNumberGenerator.nextBoolean()) {
                    newAddress = geography.getNearestEmptyAddressAtDistance(lastMaleAddress.getArea().getCentriod(), moveDistance);
                } else {
                    newAddress = geography.getNearestEmptyAddressAtDistance(lastFemaleAddress.getArea().getCentriod(), moveDistance);
                }

            }
        }

        father.setAddress(moveDate, newAddress);
        mother.setAddress(moveDate, newAddress);

        if(father.getPartnerships().size() > 1) // if this is the persons first partnership then they are a child at their own address - therefore they don't take the other inhabitants (i.e. parents and siblings) into their new marriage home
            while(lastMaleAddress != null && lastMaleAddress.getInhabitants().size() != 0) {
                IPerson individual = lastMaleAddress.getInhabitants().get(0);
                LocalDate individualMoveDate = moveDate.isBefore(individual.getBirthDate()) ? individual.getBirthDate() : moveDate;
                individual.setAddress(individualMoveDate, newAddress);
            }

        if(mother.getPartnerships().size() > 1) // if this is the persons first partnership then they are a child at their own address - therefore they don't take the other inhabitants (i.e. parents and siblings) into their new marriage home
            while(lastFemaleAddress != null && lastFemaleAddress.getInhabitants().size() != 0) {
                IPerson individual = lastFemaleAddress.getInhabitants().get(0);
                LocalDate individualMoveDate = moveDate.isBefore(individual.getBirthDate()) ? individual.getBirthDate() : moveDate;
                individual.setAddress(individualMoveDate, newAddress);
            }

    }

    private void findPartners(final List<NewMother> women, final Map<IntegerRange, LinkedList<IPerson>> menMap,
                              final LabelledValueSet<IntegerRange, Integer> partnerCounts, final List<ProposedPartnership> proposedPartnerships) {

        Iterator<NewMother> iterator = women.iterator();

        while (iterator.hasNext()) {

            final NewMother newMother = iterator.next();

            partnerSearchLoop:
            for (final IntegerRange range : partnerCounts.getLabels()) {
                for (final IPerson man : menMap.get(range)) {

                    if (eligible(man, newMother) && !inPartnerships(man, proposedPartnerships)) {

                        proposedPartnerships.add(new ProposedPartnership(man, newMother.newMother, newMother.numberOfChildrenInMaternity));
                        iterator.remove();

                        break partnerSearchLoop;
                    }
                }
            }
        }
    }

    private List<ProposedPartnership> getProposedPartnerships(final LinkedList<NewMother> women, final Map<IntegerRange, LinkedList<IPerson>> menMap,
                                                              final LabelledValueSet<IntegerRange, Integer> partnerCounts, final LabelledValueSet<IntegerRange, Integer> achievedPartnerCounts) {

        final List<ProposedPartnership> proposedPartnerships = new ArrayList<>();

        // for each age range of males
        for (IntegerRange range : partnerCounts.getLabels()) {

            final LinkedList<IPerson> men = menMap.get(range);
            final Collection<NewMother> unmatchedFemales = new ArrayList<>();

            final int determinedCount = addPartnerships(women, men, proposedPartnerships, unmatchedFemales, partnerCounts.get(range));

            women.addAll(unmatchedFemales);

            // note how many females have been partnered at this age range
            achievedPartnerCounts.add(range, partnerCounts.get(range) - determinedCount);
        }

        return proposedPartnerships;
    }

    private int addPartnerships(final LinkedList<NewMother> women, final LinkedList<IPerson> men, final List<ProposedPartnership> proposedPartnerships, final Collection<NewMother> unmatchedFemales, final int initialCount) {

        int determinedCount = initialCount;

        IPerson head = null; // keeps track of first man seen to prevent infinite loop

        // Keep going until enough females have been matched for this range
        while (determinedCount > 0 && !women.isEmpty()) {

            final IPerson man = men.pollFirst();
            NewMother woman = women.pollFirst();

            // if man is head of list - i.e. this is the second time round
            if (man == head) {
                // thus female has not been able to be matched
                unmatchedFemales.add(woman);
                head = null;

                // get next woman to check for partnering
                if (women.isEmpty()) break;
                woman = women.pollFirst();
            }

            // check if there is any reason why these people cannot lawfully be partnered...
            if (eligible(man, woman)) {
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
        return determinedCount;
    }

    private OperableLabelledValueSet<IntegerRange, Integer> redistributePartnerCounts(final OperableLabelledValueSet<IntegerRange, Integer> initialPartnerCounts, final LabelledValueSet<IntegerRange, Integer> availableMen) {

        OperableLabelledValueSet<IntegerRange, Integer> partnerCounts = initialPartnerCounts;
        OperableLabelledValueSet<IntegerRange, Double> shortfallCounts;

        // this section redistributes the determined partner counts based on the number of available men in each age range
        do {
            shortfallCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesSubtractValues(availableMen), randomNumberGenerator);

            final LabelledValueSet<IntegerRange, Double> zeroedNegShortfalls = shortfallCounts.zeroNegativeValues();
            final int numberOfRangesWithSpareMen = shortfallCounts.countNegativeValues();
            final double totalShortfall = zeroedNegShortfalls.getSumOfValues();
            final double shortfallToShare = totalShortfall / (double) numberOfRangesWithSpareMen;

            partnerCounts = new IntegerRangeToDoubleSet(partnerCounts.valuesAddNWhereCorrespondingLabelNegativeInLVS(shortfallToShare, shortfallCounts)
                    .valuesSubtractValues(zeroedNegShortfalls), randomNumberGenerator).controlledRoundingMaintainingSum();

        } while (shortfallCounts.countPositiveValues() != 0);

        return partnerCounts;
    }

    private Map<IntegerRange, LinkedList<IPerson>> getAllMen(final LabelledValueSet<IntegerRange, Integer> partnerCounts, final LabelledValueSet<IntegerRange, Integer> availableMen) {

        final Map<IntegerRange, LinkedList<IPerson>> allMen = new TreeMap<>();
        for (IntegerRange range : partnerCounts.getLabels()) {

            final LocalDate yobOfOlderEndOfIR = getYobOfOlderEndOfIR(range, currentTime);
            final Period rangeLength = getRangeLength(range);

            final LinkedList<IPerson> men = new LinkedList<>(population.getLivingPeople().getMales().getPeopleBornInTimePeriod(yobOfOlderEndOfIR, rangeLength));

            CollectionUtils.shuffle(men, desired.getRandomGenerator());

            allMen.put(range, men);
            availableMen.update(range, men.size());
        }
        return allMen;
    }

    // TODO rationalise next four methods

    private IPartnership createNewPartnership(final int numberOfChildren, final IPerson father, final IPerson mother, final boolean illegitimate, final boolean marriedAtBirth) throws PersonNotFoundException {

        population.getLivingPeople().remove(mother);
        population.getLivingPeople().remove(father);  // TODO why necessary to remove/add father, if only indexed by year of birth? TD: it probably isn't...

        final IPartnership partnership = new Partnership(father, mother);
        makeChildren(partnership, numberOfChildren, illegitimate, marriedAtBirth);

        if(illegitimate)
            partnership.setFinalised(true);

        population.getLivingPeople().add(partnership);

        IPartnership motherLastParntership = PopulationNavigation.getLastPartnership(mother);
        IPartnership fatherLastParntership = PopulationNavigation.getLastPartnership(father);

        mother.recordPartnership(partnership);
        father.recordPartnership(partnership);

        // these need to happen post recording of new partnership
        handleSeperationMoves(motherLastParntership, mother);

        if(!illegitimate) {
            handleSeperationMoves(fatherLastParntership, father);
            handleAddressChanges(partnership);
        } else if(motherLastParntership == null) {
            if(father.getAddress(LocalDate.MAX) != null)
                mother.setAddress(partnership.getPartnershipDate(), geography.getNearestEmptyAddressAtDistance(father.getAddress(LocalDate.MAX).getArea().getCentriod(), moveDistanceSelector.selectRandomDistance()));
            else
                mother.setAddress(partnership.getPartnershipDate(), geography.getRandomEmptyAddress());
        }

        for(IPerson child : partnership.getChildren()) {
            child.setAddress(child.getBirthDate(), partnership.getFemalePartner().getAddress(child.getBirthDate()));
        }

        if(partnership.getMarriageDate() != null && partnership.getMarriagePlace() == null) {
            partnership.setMarriagePlace(mother.getAddress(partnership.getMarriageDate()).toShortForm());
        }

        // re-insert parents into population, this allows their position in the data structure to be updated
        population.getLivingPeople().add(mother);
        population.getLivingPeople().add(father);

        return partnership;
    }

    protected void handleSeperationMoves(IPartnership lastPartnership, IPerson rePartneringPartner) {

        if(lastPartnership != null && !lastPartnership.isFinalised()) {

            // the getting process forces these to be set - they can only be set once the next partnership has been set up - i.e. now!
            LocalDate sepDate = lastPartnership.getSeparationDate(randomNumberGenerator);

            if (sepDate != null) {
                IPerson ex = lastPartnership.getPartnerOf(rePartneringPartner);

                if(!ex.hasEmigrated() && !rePartneringPartner.hasEmigrated()) { // if neither has emigrated then we need to make these separtaion decisions - otherwise the house and kids stay with who is still in the country (the sim will have already handled this)


                    // flip coin for who gets the house
                    boolean keepHouse = randomNumberGenerator.nextBoolean();

                    // flip coin for who gets the kids
                    boolean keepKids = randomNumberGenerator.nextBoolean();

                    Address oldFamilyAddress = rePartneringPartner.getAddress(sepDate);

                    if (keepHouse) {
                        // ex moves

                        Address exsNewAddress = geography.getNearestEmptyAddressAtDistance(ex.getAddress(sepDate).getArea().getCentriod(), moveDistanceSelector.selectRandomDistance());
                        ex.setAddress(sepDate, exsNewAddress);

                        if (!keepKids) {
                            // kids move to ex
                            for (IPerson c : lastPartnership.getChildren()) {
                                if (oldFamilyAddress.getInhabitants().contains(c))
                                    c.setAddress(sepDate, exsNewAddress);
                            }
                        }

                    } else {
                        Address newAddress = geography.getNearestEmptyAddressAtDistance(rePartneringPartner.getAddress(sepDate).getArea().getCentriod(), moveDistanceSelector.selectRandomDistance());
                        rePartneringPartner.setAddress(sepDate, newAddress);

                        if (keepKids) {
                            for (IPerson c : lastPartnership.getChildren()) {
                                if (oldFamilyAddress.getInhabitants().contains(c))
                                    c.setAddress(sepDate, newAddress);
                            }
                        }

                    }

                }

            }

            lastPartnership.setFinalised(true);
        }
    }

    private void addChildrenToCurrentPartnership(final IPerson mother, final int numberOfChildren) {

        population.getLivingPeople().remove(mother);

        final IPerson mostRecentPreviousChild = PopulationNavigation.getLastChild(mother);
        final IPartnership mostRecentPartnership = mostRecentPreviousChild.getParents();

        final LocalDate newChildBirthDate = addChildrenToPartnership(numberOfChildren, mostRecentPartnership, mostRecentPreviousChild.isIllegitimate());

        updateIllegitimateCounts(numberOfChildren, mostRecentPartnership, newChildBirthDate, mostRecentPreviousChild.isIllegitimate());
        updateMarriageCounts(mother, numberOfChildren, mostRecentPreviousChild, mostRecentPartnership, newChildBirthDate);

        population.getLivingPeople().add(mother);
    }

    private LocalDate addChildrenToPartnership(final int numberOfChildren, final IPartnership partnership, boolean isIllegitimate) {

        LocalDate birthDate = null;

        for (int i = 0; i < numberOfChildren; i++) {

            IPerson child;

            if (birthDate == null) {

                child = personFactory.makePersonWithRandomBirthDate(currentTime, partnership, isIllegitimate);
                birthDate = child.getBirthDate();

            } else {

                child = personFactory.makePerson(birthDate, partnership, false);
            }

            partnership.addChildren(Collections.singleton(child));

            child.setAddress(child.getBirthDate(), partnership.getFemalePartner().getAddress(child.getBirthDate()));

            population.getLivingPeople().add(child);
        }

        return birthDate;
    }

    private void makeChildren(final IPartnership partnership, final int numberOfChildren, final boolean illegitimate, final boolean marriedAtBirth) {

        final List<IPerson> children = new ArrayList<>();

        // This ensures twins are born on the same day
        LocalDate childrenBirthDate = null;

        // the loop here allows for the multiple children in pregnancies
        for (int i = 0; i < numberOfChildren; i++) {

            final IPerson child = childrenBirthDate == null ?
                    personFactory.makePersonWithRandomBirthDate(currentTime, partnership, illegitimate) :
                    personFactory.makePerson(childrenBirthDate, partnership, illegitimate);

            population.getLivingPeople().add(child);
            children.add(child);

            childrenBirthDate = child.getBirthDate();
        }

        setMarriageDate(partnership, marriedAtBirth, childrenBirthDate);
        partnership.setPartnershipDate(childrenBirthDate);
        partnership.addChildren(children);
    }

    private void setMarriageDate(final IPartnership partnership, final boolean marriedAtBirth, final LocalDate childrenBirthDate) {

        LocalDate marriageDate = null;
        if (marriedAtBirth) {

            final LocalDate motherLastPrevPartneringEvent = getDateOfLastLegitimatePartnershipEventBefore(partnership.getFemalePartner(), childrenBirthDate);
            final LocalDate fatherLastPrevPartneringEvent = getDateOfLastLegitimatePartnershipEventBefore(partnership.getMalePartner(), childrenBirthDate);

            final LocalDate earliestPossibleMarriageDate = DateUtils.laterOf(motherLastPrevPartneringEvent, fatherLastPrevPartneringEvent);

            if (earliestPossibleMarriageDate.isBefore(childrenBirthDate)) {
                marriageDate = marriageDateSelector.selectRandomDate(earliestPossibleMarriageDate, childrenBirthDate);
            }
        }
        partnership.setMarriageDate(marriageDate);
    }

    private LocalDate getDateOfLastLegitimatePartnershipEventBefore(final IPerson person, final LocalDate date) {

        LocalDate latestDate = getEarliestPossibleMarriageDate(person);

        for (final IPartnership partnership : person.getPartnerships()) {
            if (partnership.getPartnershipDate().isBefore(date)) {

                final List<IPerson> children = partnership.getChildren();

                if (children.isEmpty()) {
                    throw new UnsupportedOperationException("Childless marriages not supported");
                }

                if (hasLegitimateChildren(partnership)) {

                    final LocalDate separationDate = partnership.getEarliestPossibleSeparationDate();
                    if (separationDate != null && latestDate.isBefore(separationDate)) {
                        latestDate = separationDate;
                    }

                    final LocalDate partnerDeathDate = partnership.getPartnerOf(person).getDeathDate();
                    if (partnerDeathDate != null && latestDate.isBefore(partnerDeathDate)) {
                        latestDate = partnerDeathDate;
                    }
                }
            }
        }

        return latestDate;
    }

    private boolean hasLegitimateChildren(final IPartnership partnership) {

        for (IPerson child : partnership.getChildren()) {
            if (!child.isIllegitimate()) return true;
        }
        return false;
    }

    private LocalDate getEarliestPossibleMarriageDate(final IPerson person) {

        return person.getBirthDate().plus(EARLIEST_AGE_OF_MARRIAGE, ChronoUnit.YEARS);
    }





    private boolean populationTooSmall() {

        return population.getLivingPeople().getPeople().size() < MINIMUM_POPULATION_SIZE;
    }

    private boolean initialisationFinished() {

        return !inInitPeriod(currentTime);
    }

    private boolean inInitPeriod(final LocalDate currentTime) {

        return !currentTime.isAfter(endOfInitPeriod);
    }

    private boolean simulationStarted() {
        return currentTime.isAfter(config.getT0());
    }

    private boolean simulationFinished() {
        return currentTime.isAfter(config.getTE());
    }

    private boolean timeFromInitialisationStartIsWholeTimeUnit() {
        return DateUtils.matchesInterval(currentTime, config.getSimulationTimeStep(), config.getTS());
    }

    // TODO adjust this to also permit age variations
    private MothersNeedingPartners selectMothers(final List<IPerson> females, final int numberOfChildren) {

        if (females.size() == 0) return new MothersNeedingPartners();

        final int ageOfMothers = ageOnDate(females.get(0), currentTime);

        final MultipleDeterminedCountByIR requiredBirths = calcNumberOfPregnanciesOfMultipleBirth(ageOfMothers, numberOfChildren);
        final LabelledValueSet<IntegerRange, Integer> motherCountsByMaternities = new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().getLabels(), 0, randomNumberGenerator);
        final OperableLabelledValueSet<IntegerRange, Integer> remainingMothersToFind = new IntegerRangeToIntegerSet(requiredBirths.getDeterminedCount().clone(), randomNumberGenerator);

        try {
            return getMothersNeedingPartners(females, numberOfChildren, requiredBirths, motherCountsByMaternities, remainingMothersToFind);

        } catch (NoSuchElementException e) {
            return new MothersNeedingPartners();
        }
    }

    private MothersNeedingPartners getMothersNeedingPartners(final List<IPerson> females, final int numberOfChildren, final MultipleDeterminedCountByIR requiredBirths,
                                                             final LabelledValueSet<IntegerRange, Integer> motherCountsByMaternities, final OperableLabelledValueSet<IntegerRange, Integer> remainingMothersToFind) {

        CollectionUtils.shuffle(females, desired.getRandomGenerator());

        IntegerRange highestBirthOption = remainingMothersToFind.getLargestLabelOfNonZeroValue();

        int childrenMade = 0;
        final List<NewMother> newMothers = new ArrayList<>();

        final Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren = new HashMap<>();

        for (final IPerson female : females) {

            if (eligible(female)) {

                int numberOfChildrenForThisMother = highestBirthOption.getValue();
                childrenMade += numberOfChildrenForThisMother;

                addChildrenForMother(female, numberOfChildrenForThisMother, newMothers, continuingPartneredFemalesByChildren);

                // updates count of remaining mothers to find
                final int furtherMothersNeededForMaternitySize = remainingMothersToFind.get(highestBirthOption) - 1;
                remainingMothersToFind.update(highestBirthOption, furtherMothersNeededForMaternitySize);

                // updates count of mother found
                motherCountsByMaternities.update(highestBirthOption, motherCountsByMaternities.getValue(highestBirthOption) + 1);

                if (furtherMothersNeededForMaternitySize <= 0) {
                    try {
                        highestBirthOption = remainingMothersToFind.getLargestLabelOfNonZeroValue();

                    } catch (NoSuchElementException e) {
                        // In this case we have created all the new mothers and children required
                        break;
                    }
                }

                if (childrenMade >= numberOfChildren) break;
            }
        }

        separationEvent(continuingPartneredFemalesByChildren);

        requiredBirths.setFulfilledCount(motherCountsByMaternities);
        desired.returnAchievedCount(requiredBirths);

        return new MothersNeedingPartners(newMothers, childrenMade);
    }

    private void addChildrenForMother(final IPerson mother, final int numberOfChildrenForThisMother, final List<NewMother> newMothers, final Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren) {

        if (needsNewPartner(mother, currentTime)) {
            newMothers.add(new NewMother(mother, numberOfChildrenForThisMother));

        } else {

            addChildrenToCurrentPartnership(mother, numberOfChildrenForThisMother);
            final int numberOfChildrenInLatestPartnership = numberOfChildrenInLatestPartnership(mother);

            if (!continuingPartneredFemalesByChildren.containsKey(numberOfChildrenInLatestPartnership)) {
                continuingPartneredFemalesByChildren.put(numberOfChildrenInLatestPartnership, new ArrayList<>());
            }
            continuingPartneredFemalesByChildren.get(numberOfChildrenInLatestPartnership).add(mother);
        }
    }

    private MultipleDeterminedCountByIR calcNumberOfPregnanciesOfMultipleBirth(final int ageOfMothers, final int numberOfChildren) {

        final MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, config.getSimulationTimeStep(), currentTime);
        return (MultipleDeterminedCountByIR) desired.getDeterminedCount(key, config);
    }

    private boolean eligible(final IPerson potentialMother) {

        // No previus partners or children - thus eligible
        if(potentialMother.getPartnerships().size() == 0) return true;

        // if last partnership has not ended in separation and the spouse has emigrated then this women cannot produce a child (now or at any future point in the simulation)
        if(!needsNewPartner(potentialMother, currentTime) &&
                potentialMother.getLastPartnership().getMalePartner().hasEmigrated()) {
            return false;
        }

        final IPerson lastChild = PopulationNavigation.getLastChild(potentialMother);

        if (lastChild != null) {

            final LocalDate earliestDateOfNextChild = lastChild.getBirthDate().plus(desired.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentTime
            return earliestDateOfNextChild.isBefore(currentTime);
        }

        // i.e. there is no previous child and thus no limitation to birth
        return true;
    }

    private boolean eligible(final IPerson man, final NewMother newMother) {

        population.getPopulationCounts().incEligibilityCheck();

        final boolean eligible = maleAvailable(man, newMother.numberOfChildrenInMaternity) && legallyEligibleToMarry(man, newMother.newMother);

        if (!eligible) {
            population.getPopulationCounts().incFailedEligibilityCheck();
        }

        return eligible;
    }

    private void updateIllegitimateCounts(final int numberOfChildren, final IPartnership partnership, final LocalDate birthDate, final boolean isIllegitimate) {

        final IPerson man = partnership.getMalePartner();

        final IllegitimateBirthStatsKey illegitimateKey = new IllegitimateBirthStatsKey(ageOnDate(man, birthDate), numberOfChildren, config.getSimulationTimeStep(), birthDate);
        final SingleDeterminedCount illegitimateCounts = (SingleDeterminedCount) desired.getDeterminedCount(illegitimateKey, config);

        if(isIllegitimate)
            illegitimateCounts.setFulfilledCount(numberOfChildren);
        else
            illegitimateCounts.setFulfilledCount(0);

        desired.returnAchievedCount(illegitimateCounts);
    }

    private void updateMarriageCounts(final IPerson mother, final int numberOfChildren, final IPerson mostRecentPreviousChild,
                                      final IPartnership mostRecentPartnership, final LocalDate newChildBirthDate) {

        final MarriageStatsKey marriageKey = new MarriageStatsKey(ageOnDate(mother, newChildBirthDate), numberOfChildren, config.getSimulationTimeStep(), newChildBirthDate);
        final SingleDeterminedCount marriageCounts = (SingleDeterminedCount) desired.getDeterminedCount(marriageKey, config);

        if (mostRecentPartnership.getMarriageDate() != null) {
            // is already married - so return as married
            marriageCounts.setFulfilledCount(numberOfChildren);

        } else {
            final boolean marriedAtBirth = (int) Math.round(marriageCounts.getDeterminedCount() / (double) numberOfChildren) == 1;

            if (marriedAtBirth) {
                marriageCounts.setFulfilledCount(numberOfChildren);
                LocalDate mostRecentPreviousChildBirthDate = mostRecentPreviousChild.getBirthDate();
                LocalDate marriageDate = marriageDateSelector.selectRandomDate(mostRecentPreviousChildBirthDate, newChildBirthDate);
                mostRecentPartnership.setMarriageDate(marriageDate);

            } else {
                marriageCounts.setFulfilledCount(0);
            }
        }

        desired.returnAchievedCount(marriageCounts);
    }

    private void addMotherToMap(final ProposedPartnership partnership, final Map<Integer, List<IPerson>> partneredFemalesByChildren) {

        final IPerson mother = partnership.female;
        final int numChildrenInPartnership = partnership.numberOfChildren;

        if (!partneredFemalesByChildren.containsKey(numChildrenInPartnership)) {
            partneredFemalesByChildren.put(numChildrenInPartnership, new ArrayList<>());
        }
        partneredFemalesByChildren.get(numChildrenInPartnership).add(mother);
    }

    private int removeLastPartners(final Population population, final List<NewMother> women) {

        int cancelledChildren = 0;

        if (!women.isEmpty()) {
            for (final NewMother newMother : women) {

                // update position in data structures
                population.getLivingPeople().remove(newMother.newMother);

                cancelledChildren += newMother.numberOfChildrenInMaternity;
                // cancel birth(s) as no father can be found
                newMother.newMother.getPartnerships().remove(getLastPartnership(newMother.newMother));

                population.getLivingPeople().add(newMother.newMother);
            }
        }
        return cancelledChildren;
    }

    private IntegerRange resolveAgeToIntegerRange(final IPerson male, final Set<IntegerRange> labels, final LocalDate currentDate) {

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

        if(man.getImmigrationDate() != null) {
            // if the man has immigrated this year, was he present early enough in the year to be involved with a conception event?
            if(man.getImmigrationDate().plus(desired.getMinGestationPeriod()).isAfter(currentTime.plus(1, ChronoUnit.YEARS).minus(1, ChronoUnit.DAYS)))
                return false;
        }

        // in the init period any partnering is allowed
        // TODO shouldn't be hard wired
        if (inInitPeriod(currentTime)) {
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

    private Period getRangeLength(final IntegerRange range) {

        return Period.ofYears(range.getMax() - range.getMin() + 1);
    }

    private LocalDate getYobOfOlderEndOfIR(final IntegerRange range, final LocalDate currentDate) {

        return currentDate.minusYears(range.getMax() + 1);
    }

    private void separationEvent(final Map<Integer, List<IPerson>> continuingPartneredFemalesByChildren) {

        // Consideration of separation is based on number of children in females current partnerships
        for (final Map.Entry<Integer, List<IPerson>> entry : continuingPartneredFemalesByChildren.entrySet()) {

            final int numberOfChildren = entry.getKey();
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
            final LocalDate deathDate = deathDateSelector.selectDate(person, desired, currentTime, config.getSimulationTimeStep());

            int ageAtDeath = Period.between(person.getBirthDate(), deathDate).getYears();
            final String deathCause = desired.getDeathCauseRates(Year.of(deathDate.getYear()), person.getSex(), ageAtDeath).getSample();

            person.setDeathDate(deathDate);
            person.setDeathCause(deathCause);

            for(IPartnership partnership : person.getPartnerships()) {
                handleSeperationMoves(partnership, person);
            }

            Address lastAddress = person.getAddress(deathDate);
            if(lastAddress != null) {
                lastAddress.removeInhabitant(person);
            }

            killed++;

            // move person to correct place in data structure
            population.getDeadPeople().add(person);
        }

        return killed;
    }

    private PersonCollection getLivingPeopleOfSex(final SexOption sex) {

        final PeopleCollection livingPeople = population.getLivingPeople();
        return sex == SexOption.MALE ? livingPeople.getMales() : livingPeople.getFemales();
    }

    private boolean needsNewPartner(final IPerson person, final LocalDate currentDate) {

        return person.getPartnerships().size() == 0 || partnersToSeparate.contains(person) || lastPartnerDied(person, currentDate);
    }

    private void separate(final IPartnership partnership, final LocalDate earliestPossibleSeperationDate) {

        partnership.setEarliestPossibleSeparationDate(earliestPossibleSeperationDate);

        partnersToSeparate.add(partnership.getFemalePartner());
        partnersToSeparate.add(partnership.getMalePartner());
    }

    public void recordOutOfMemorySummary() {
        summary.setCompleted(false);
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());
        summary.setEligibilityChecks(population.getPopulationCounts().getEligibilityChecks());
        summary.setFailedEligibilityChecks(population.getPopulationCounts().getFailedEligibilityChecks());
        summary.setTotalPop(population.getPeople(config.getT0(), config.getTE(), MAX_AGE).getNumberOfPeople());
        summary.setSimRunTime(simTimer.getRunTimeSeconds());
        summary.setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
        MemoryUsageAnalysis.reset();

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

        summary.setTotalPop(population.getPeople(config.getT0(), config.getTE(), MAX_AGE).getNumberOfPeople());
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

    private class MothersNeedingPartners {

        private final List<NewMother> mothers;

        // This includes those added to existing partnerships and those marked for creation in the imminent partnering step
        private final int newlyProducedChildren;

        MothersNeedingPartners() {
            this(new ArrayList<>(), 0);
        }

        MothersNeedingPartners(final List<NewMother> mothers, final int newlyProducedChildren) {
            this.mothers = mothers;
            this.newlyProducedChildren = newlyProducedChildren;
        }
    }
}
