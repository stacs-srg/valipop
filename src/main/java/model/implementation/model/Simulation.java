package model.implementation.model;

import datastructure.PeopleCollection;
import model.Partnership;
import model.Person;
import model.implementation.analysis.PopulationComposition;
import model.implementation.analysis.statistics.ComparativeAnalysis;
import model.implementation.analysis.GeneratedPopulationCompositionFactory;
import model.implementation.config.Config;
import model.implementation.populationStatistics.*;
import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPopulation;

import model.time.*;
import model.time.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.CollectionUtils;
import utils.MapUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    private final static Path PATH_TO_CONFIG_FILE = Paths.get("/Users/tsd4/OneDrive/cs/PhD/population_model/src/main/resources/config/config.txt");
    private final static Config config = new Config(PATH_TO_CONFIG_FILE);
    public static Logger log = LogManager.getLogger(Simulation.class);
    PopulationStatistics desired;
    private int currentHypotheticalPopulationSize;

    private CompoundTimeUnit orphanTimeStep = new CompoundTimeUnit(1, TimeUnit.YEAR);
    private Date endOfOrphanPeriod;

    private PeopleCollection people;
    private PeopleCollection deadPeople;

    private int birthCount = 0;
    private int deathCount = 0;


    private DateClock currentTime;

    private Random randomNumberGenerator = new Random();


    public Simulation() {
        currentTime = config.gettS();
        people = new PeopleCollection(config.gettS(), config.gettE());
        deadPeople = new PeopleCollection(config.gettS(), config.gettE());


        // get desired population info
        desired = setUpSimData();

        setUpSeedCreationParameters();

    }

    public static void main(String[] args) {

        Logger log = LogManager.getLogger("main");

        log.info("Program begins");

        Simulation sim = new Simulation();

        // run model
        IPopulation population = sim.makeSimulatedPopulation();

        // perform comparisons
        ComparativeAnalysis comparisonOfDesiredAndGenerated = sim.analyseGeneratedPopulation(population);

        // Check for statistical significant similarity between desired and generated population
        if (comparisonOfDesiredAndGenerated.passed()) {
            System.out.println("Generated population similarity to desired population is statistically significant");
        }


    }

    private static PopulationStatistics setUpSimData() {

        PopulationStatistics desiredStatistics = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        // interpolate
        // for each data type
        //      smooth value changes in gaps between years for which data is given
        // end for


        return desiredStatistics;

    }

    private void setUpSeedCreationParameters() {

        currentHypotheticalPopulationSize = (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.gettS(), config.getT0()).getCount()));
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfOrphanPeriod = config.gettS().advanceTime(new CompoundTimeUnit(desired.getOrderedBirthRates(config.gettS().getYearDate()).getMaxRowLabelValue().getValue() , TimeUnit.YEAR));
        log.info("End of Orphan Period set: " + endOfOrphanPeriod.toString());

        // ALTERNATIVE APPROACH
        // calculate desired birth rate to achieve seed population at Time 0, to do this:
        // for each population growth rates before Time 0 working backwards to Time start
        // apply the compound negative of the growth rate since the previous growth rate to the seed population desired size
        // GROWTH_RATES = intended growth rates for times before Time 0
        // end for

        // store the calculated start population as PRESENT_POPULATION_COUNT

    }

    public ComparativeAnalysis analyseGeneratedPopulation(IPopulation generatedPopulation) {
        // get comparable statistics for generate population
        PopulationComposition generatedPopulationComposition = GeneratedPopulationCompositionFactory.createGeneratedPopulationComposition(generatedPopulation);

        // compare desired and generated population
        ComparativeAnalysis comparisonOfDesiredAndGenerated = new ComparativeAnalysis(desired, generatedPopulationComposition);

        return comparisonOfDesiredAndGenerated;

    }

    private IPopulation makeSimulatedPopulation() {

        // INFO: at this point all the desired population statistics have been made available
        log.info("Simulation begins");

        // start time progression
        // for each time step from T Start to T End
        while(DateUtils.dateBefore(currentTime, config.gettE())) {

            // at every min timestep
            // clear out dead people

            // if deaths timestep
            if(DateUtils.matchesInterval(currentTime, config.getDeathTimeStep())) {
                handleDeaths();
                log.info("Deaths handled: " + currentTime.toString() + " - " + deathCount);
                deathCount = 0;
            }

            // if births timestep
            if(DateUtils.matchesInterval(currentTime, config.getBirthTimeStep())) {
                handleBirths();
                log.info("Births handled: " + currentTime.toString() + " - " + birthCount);
            }

            if(DateUtils.dateBefore(currentTime, endOfOrphanPeriod) && DateUtils.matchesInterval(currentTime, orphanTimeStep)) {
                handleOrphanChildren();
                log.info("Orphan children handled: " + currentTime.toString());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());
            log.info("Current Date: " + currentTime.toString() + "    Population: " + people.getNumberOfPersons());

        }

        return people;
    }

    private void handleOrphanChildren() {

        // calculate yearly birth target, using:

        // deaths
        int hypotheticalDeaths = calculateNumberToDie(currentHypotheticalPopulationSize, config.getSetUpDR());

        // births
        int hypotheticalBirths = calculateNumberToBeBorn(currentHypotheticalPopulationSize, config.getSetUpBR());
        int shortFallInBirths = hypotheticalBirths - birthCount;
        birthCount = 0;

        log.info("Current Date: " + currentTime.toString() + "    Short fall in births: " + shortFallInBirths);

        // update hypothetical population
        currentHypotheticalPopulationSize = currentHypotheticalPopulationSize + hypotheticalBirths - hypotheticalDeaths;

        // add Orphan Children to the population
        for(int i = 0; i < shortFallInBirths; i++) {
            Person child = new Person(getSex(), currentTime, null);
            people.addPerson(child);
        }


    }



    private void handleDeaths() {

        // handle deaths for the next year
        // for each year of birth since tS
        for(DateClock d = config.gettS(); DateUtils.dateBefore(d, currentTime); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // get count of people of given age
            int males = people.getMales().getByYear(d).size();
            int females = people.getFemales().getByYear(d).size();

            // DATA - get rate of death by age and gender
            Double maleDeathRate = desired.getDeathRates(currentTime.getYearDate(), 'm').getData(currentTime.getYear() - d.getYear());
            Double femaleDeathRate = desired.getDeathRates(currentTime.getYearDate(), 'f').getData(currentTime.getYear() - d.getYear());

            // use data to calculate who to kill off
            int malesToDie = calculateNumberToDie(males, maleDeathRate);
            int femalesToDie = calculateNumberToDie(females, femaleDeathRate);

            deathCount += malesToDie;
            deathCount += femalesToDie;

            Collection<Person> deadMales = people.getMales().removeRandomPersons(malesToDie, d.getYearDate());
            Collection<Person> deadFemales = people.getFemales().removeRandomPersons(femalesToDie, d.getYearDate());

            // for each to be killed
            for(Person m : deadMales) {
                // TODO execute death at a time in the next year
                m.recordDeath(currentTime);
                deadPeople.addPerson(m);
            }

            for(Person f : deadFemales) {
                // TODO execute death at a time in the next year
                f.recordDeath(currentTime);
                deadPeople.addPerson(f);
            }

        }

    }

    private int calculateNumberToDie(int people, Double deathRate) {

        double toDie = people * deathRate;
        int flooredToDie = (int) toDie;
        toDie -= flooredToDie;

        // this is a random dice roll to see if the fraction of a person dies or not
        if(randomNumberGenerator.nextDouble() < toDie) {
            flooredToDie++;
        }

        return flooredToDie;

    }

    private int calculateNumberToBeBorn(int currentHypotheticalPopulationSize, double setUpBR) {

        return calculateNumberToDie(currentHypotheticalPopulationSize, setUpBR);

    }

    private void handleBirths() {

        // create set of MOTHERS_NEEDING_FATHERS
        // create set of MOTHERS_WITH_FATHERS
        // create set of NEW_FATHERS

        // make children/decide on mothers

        // for each age of mothers of childbearing age (AGE OF MOTHER)
        int minAge = desired.getOrderedBirthRates(currentTime.getYearDate()).getMinRowLabelValue();
        int maxAge = desired.getOrderedBirthRates(currentTime.getYearDate()).getMaxRowLabelValue().getMax();


        for(int age = minAge; age < maxAge; age++) {

            YearDate yearOfBirthInConsideration = new YearDate(currentTime.getYear() - age);

            Map<Integer, Collection<Person>> womenOfThisAge = people.getFemales().getMapByYear(yearOfBirthInConsideration);



            // DATA - get rate of births by mothers age
            OneDimensionDataDistribution orderedBirthRatesForMothersOfThisAge = desired.getOrderedBirthRates(currentTime.getYearDate()).getData(age);
            OneDimensionDataDistribution taperedOrderedBirthRatesForMothersOfThisAge = transformOrderedBirthRatesToTaperByOrderCount(orderedBirthRatesForMothersOfThisAge, womenOfThisAge.keySet());

            // DATA 2 - get rate of multiple births in a maternity (by order)
            OneDimensionDataDistribution multipleBirthDataForMothersOfThisAgeByMaternity = desired.getMultipleBirthRates(currentTime.getYearDate()).getData(age);
            OneDimensionDataDistribution proportionOfChildrenBornToEachSizeOfMaternity = transformMaternityProportionsToChildrenProportions(multipleBirthDataForMothersOfThisAgeByMaternity);

            int maxBirthOrderInCohort = MapUtils.getMax(womenOfThisAge.keySet());
            int sizeOfCohort = MapUtils.countPeopleInMap(womenOfThisAge);


            // for each number of children already birthed to mothers (BIRTH ORDER)
            for(int order = 0; order <= maxBirthOrderInCohort; order++) {

                // women of this age and birth order
                Collection<Person> women = womenOfThisAge.get(order);

                // DATA 1 - get rate of births by mothers age and birth order
                Double birthRate = taperedOrderedBirthRatesForMothersOfThisAge.getData(order) * config.getBirthTimeStep().toDecimalRepresentation();


                // use DATA 1 to see how many many children need to be born
                int numberOfChildrenToBirth = calculateChildrenToBeBorn(sizeOfCohort, birthRate);
                birthCount += numberOfChildrenToBirth;

//                System.out.println("Age " + age + " | Order " + order + " | BR: " + birthRate + " | Cohort Size: " + sizeOfCohort + " | Number of Children: " + numberOfChildrenToBirth);

                // calculate numbers of mothers to give birth (and which will bear twins, etc.)
                // use DATA 2 to decide how many mothers needed to birth children
                Map<Integer, Integer> motherCountsByMaternitySize = calculateMotherCountsByMaternitySize(numberOfChildrenToBirth, proportionOfChildrenBornToEachSizeOfMaternity);

//                ArrayList<Integer> l = new ArrayList(motherCountsByMaternitySize.keySet());
//                Collections.sort(l);
//
//                for(Integer i : l) {
//                    System.out.print(i + " : " + motherCountsByMaternitySize.get(i) + " | ");
//                }
//                System.out.println();

                // check the mother counts are possible to meet with the current cohort
                int totalNumberOfMothers = CollectionUtils.sumIntegerCollection(motherCountsByMaternitySize.values());

                if(women.size() < totalNumberOfMothers) {
                    log.fatal("Current Date: " + currentTime.toString() + " - Insufficient number of mothers: Eligible women " + women.size() + " | Mothers Required " + totalNumberOfMothers + " | Age " + age + " | Order " + order);
                    System.exit(451);
                }

                // select the mothers
                for(Integer childrenInMaternity : motherCountsByMaternitySize.keySet()) {

                    ArrayList<Person> mothersToBe = new ArrayList<>(people.getFemales().removeRandomPersons(motherCountsByMaternitySize.get(childrenInMaternity), yearOfBirthInConsideration, order));
                    for(int n = 0; n < motherCountsByMaternitySize.get(childrenInMaternity); n++) {
                        Person mother = mothersToBe.get(n);

                        // make and assign the specified number of children - assign to correct place in population
                        for(int c = 0; c < childrenInMaternity; c++) {
                            mother.recordPartnership(formNewChildInPartnership(mother));
                        }

                        people.addPerson(mother);

                        // TODO implement partnering - part 1
                        // if birth order 0
                            // add mothers to MOTHERS_NEEDING_FATHERS
                        // else
                            // DATA - get rate of separation by number of children had
                            // select mothers to separate with fathers and add to MOTHERS_NEEDING_FATHERS

                        // add the rest to MOTHERS_WITH_FATHERS

                    }

                }


            }

            // TODO implement partnering - part 2

            // decide on new fathers
            // NUMBER_OF_FATHERS_NEEDED = MOTHERS_NEEEDING_FATHERS.size()
            // DATA - get age difference of parents at childs birth distribution (this is a subset/row of an ages in combination table)
            // Turn distribution into solid values based on the number of fathers required
            // select fathers and add to NEW_FATHERS

            // pair up MOTHERS_NEEDING_FATHERS with NEW_FATHERS

            // find appropriate birth date for child

            // update new children info to give fathers
            // keep count of children born this quarter as BIRTH_COUNT



        }

    }

    private OneDimensionDataDistribution transformOrderedBirthRatesToTaperByOrderCount(OneDimensionDataDistribution orderedBirthRatesForMothersOfThisAge, Set<Integer> orders) {
        IntegerRange largestExplicitRange = orderedBirthRatesForMothersOfThisAge.getMaxRowLabelValue();

        if(largestExplicitRange.isPlus()) {

            int largestExplicitValue = largestExplicitRange.getValue();
            Double birthRateToShare = orderedBirthRatesForMothersOfThisAge.getData(largestExplicitValue);

            int toShareAmong = 0;
            int denominator = 0;

            for(Integer i : orders) {
                if(i <= largestExplicitValue) {
                    toShareAmong++;
                    denominator += Math.pow(toShareAmong, 2);
                }
            }

            Map<IntegerRange, Double> temp = new HashMap<IntegerRange, Double>();
            ArrayList<Integer> orderedOrders = new ArrayList<>(orders);
            Collections.sort(orderedOrders);

            int tally = 0;

            for(Integer i : orderedOrders) {

                while(tally < i) {
                    temp.put(new IntegerRange(tally), 0.0);
                    tally++;
                }

                if(i < largestExplicitValue) {
                    temp.put(new IntegerRange(i), orderedBirthRatesForMothersOfThisAge.getData(i));
                } else {
                    double fraction = Math.pow(toShareAmong, 2) / denominator;
                    toShareAmong--;

                    temp.put(new IntegerRange(i), fraction * birthRateToShare);
                }

                tally++;

            }

            temp.put(new IntegerRange(tally), 0.0);

            return new OneDimensionDataDistribution(
                    orderedBirthRatesForMothersOfThisAge.getYear(),
                    orderedBirthRatesForMothersOfThisAge.getSourcePopulation(),
                    orderedBirthRatesForMothersOfThisAge.getSourceOrganisation(),
                    temp);


        } else {
            return orderedBirthRatesForMothersOfThisAge;
        }

    }

    private IPartnership formNewChildInPartnership(Person mother) {
        Partnership partnership = new Partnership(null, mother);

        // TODO vary birth date in quarter
        Person child = new Person(getSex(), currentTime, partnership);
        partnership.addChildren(Collections.singletonList(child));
        people.addPerson(child);
        return partnership;
    }

    private char getSex() {

        // TODO move over to a specified m to f ratio

        if(randomNumberGenerator.nextBoolean()) {
            return 'm';
        } else {
            return 'f';
        }

    }

    private Map<Integer,Integer> calculateMotherCountsByMaternitySize(int numberOfChildrenToBirth, OneDimensionDataDistribution proportionOfChildrenBornToEachSizeOfMaternity) {

        // In the comments 'maternity type' is used to term how many children are born from the maternity
        // i.e. a single child maternity or a two child maternity would be an example of two maternity types

        // calculate numbers of children to be born from each maternity type
        Map<IntegerRange, Double> temp = proportionOfChildrenBornToEachSizeOfMaternity.cloneData();

        for(IntegerRange iR : temp.keySet()) {
            double exactNumberOfChildren = temp.get(iR) * numberOfChildrenToBirth;
            temp.replace(iR, exactNumberOfChildren);
        }

        double sumOfRemainders = 0;

        int numberOfChildrenShort = MapUtils.sumOfFlooredValues(temp);

        // therefore calculate the resulting number of mothers for each maternity type
        for(IntegerRange iR : temp.keySet()) {
            double exactNumberOfMothers = temp.get(iR) / iR.getMin();
            sumOfRemainders += exactNumberOfMothers - (int) exactNumberOfMothers;
            temp.replace(iR, exactNumberOfMothers);
        }

        // handle rounding
        // check if total number of children resulting from this is correct
        while(numberOfChildrenShort > 0) {
            // first by split number line dice roll
            try {
                numberOfChildrenShort -= performBalancingIterationOnNumbersOfMothers(temp, sumOfRemainders);
            } catch (StatisticalManipulationCalculationError e) {
                log.fatal(e.getMessage());
                System.exit(401);
            }
        }

        // if the number line dice roll caused an additional set of twins or triplets or etc.
        while(numberOfChildrenShort < 0) {
            // reduce the mother counts for lower maternity types
            try {
                numberOfChildrenShort += removeLowerTypeMaternities(temp, numberOfChildrenShort);
            } catch (StatisticalManipulationCalculationError e) {
                log.fatal(e.getMessage());
                System.exit(402);
            }

            // repeat until total number of children is equal to the input given of numberOfChildrenToBirth
        }

        return MapUtils.floorAllValuesInMap(temp);


    }

    private int removeLowerTypeMaternities(Map<IntegerRange, Double> temp, int numberOfChildrenShort) throws StatisticalManipulationCalculationError {

        // this should be done proportionally by their rounding error
        double sumOfQualifyingRemainders = 0;

        for(IntegerRange iR : temp.keySet()) {

            if(iR.getValue() <= Math.abs(numberOfChildrenShort)) {
                double motherCount = temp.get(iR);
                double remainder = motherCount - (int) motherCount;
                sumOfQualifyingRemainders += remainder;
            }

        }

        double random = randomNumberGenerator.nextDouble();

        double uptoOnNumberLine = 0;

        for(IntegerRange iR : temp.keySet()) {

            if(iR.getValue() <= Math.abs(numberOfChildrenShort)) {
                double motherCount = temp.get(iR);
                double remainder = motherCount - (int) motherCount;

                uptoOnNumberLine += remainder / sumOfQualifyingRemainders;
                if (random < uptoOnNumberLine) {
                    temp.replace(iR, motherCount - 1);
                    return iR.getValue();
                }
            }
        }

        throw new StatisticalManipulationCalculationError("Fatal balancing error has occurred in the method: Simulation.removeLowerTypeMaternities(...)");

    }


    private int performBalancingIterationOnNumbersOfMothers(Map<IntegerRange, Double> temp, double sumOfRemainders) throws StatisticalManipulationCalculationError {

        double random = randomNumberGenerator.nextDouble();

        double uptoOnNumberLine = 0;

        for(IntegerRange iR : temp.keySet()) {

            double motherCount = temp.get(iR);
            double remainder = motherCount - (int) motherCount;

            uptoOnNumberLine += remainder / sumOfRemainders;
            if(random < uptoOnNumberLine) {
                temp.replace(iR, motherCount + 1);
                return iR.getValue();
            }
        }

        throw new StatisticalManipulationCalculationError("Fatal balancing error has occurred in the method: Simulation.performBalancingIterationOnNumbersOfMothers(...)");

    }

    private OneDimensionDataDistribution transformMaternityProportionsToChildrenProportions(OneDimensionDataDistribution multipleBirthDataForMothersOfThisAgeByMaternity) {

        OneDimensionDataDistribution maternities = multipleBirthDataForMothersOfThisAgeByMaternity;

        Map<IntegerRange, Double> temp = maternities.cloneData();

        double sumOfScaledValues = 0;

        for(IntegerRange iR : temp.keySet()) {
            double scaledValue = iR.getMin() * temp.get(iR);
            sumOfScaledValues += scaledValue;
            temp.replace(iR, scaledValue);
        }

        for(IntegerRange iR : temp.keySet()) {
            double proportionalValue = temp.get(iR) / sumOfScaledValues;
            temp.replace(iR, proportionalValue);
        }

        return new OneDimensionDataDistribution(maternities.getYear(), maternities.getSourcePopulation(), maternities.getSourceOrganisation(), temp);

    }

    private int calculateChildrenToBeBorn(int sizeOfCohort, Double birthRate) {

        double absoluteNumberOfChildrenToBeBorn = sizeOfCohort * birthRate;

        int childrenToBeBorn = (int) absoluteNumberOfChildrenToBeBorn;

        double leftOverBitOfChild = absoluteNumberOfChildrenToBeBorn - childrenToBeBorn;

        // this is a random dice roll to see if the leftOverBitOfChildren gets made up to a full child or not
        if(randomNumberGenerator.nextDouble() < leftOverBitOfChild) {
            childrenToBeBorn++;
        }

        return childrenToBeBorn;
    }


}
