package model.simulationLogic;

import config.Config;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import model.PersonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.CollectionUtils;
import utils.MapUtils;
import utils.time.Date;
import utils.time.DateClock;
import utils.time.YearDate;

import java.util.*;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthLogic {

    public static Logger log = LogManager.getLogger(BirthLogic.class);
    private static Random randomNumberGenerator = new Random();


    public static int handleBirths(Config config, DateClock currentTime, PopulationStatistics desiredPopulationStatistics,
                                   PeopleCollection people) throws InsufficientNumberOfPeopleException {

        int birthCount = 0;

        int minAge = getMinimumAgeForChildbearing(currentTime, desiredPopulationStatistics);
        int maxAge = getMaxAgeForChildBearing(currentTime, desiredPopulationStatistics);

        // for each age of mothers of childbearing age (AGE OF MOTHER)
        for (int age = minAge; age < maxAge; age++) {

            YearDate yearOfBirthInConsideration = new YearDate(currentTime.getYear() - age);

            // DATA 2 - get rate of multiple births in a maternity by mothers age
            OneDimensionDataDistribution multipleBirthDataForMothersOfThisAgeByMaternity = desiredPopulationStatistics.getMultipleBirthRates(currentTime).getData(age);
            OneDimensionDataDistribution proportionOfChildrenBornToEachSizeOfMaternity = transformMaternityProportionsToChildrenProportions(multipleBirthDataForMothersOfThisAgeByMaternity);

            int maxBirthOrderInCohort = people.getFemales().getHighestBirthOrder(yearOfBirthInConsideration);
            int sizeOfCohort = people.getFemales().getNumberOfPersons(yearOfBirthInConsideration);

            // for each number of children already birthed to mothers (FIRST_BIRTH ORDER)
            for (int order = 0; order <= maxBirthOrderInCohort; order++) {

                // women of this age and birth order - L
                Collection<IPerson> women = people.getFemales().getByYearAndBirthOrder(yearOfBirthInConsideration, order);

                if (women == null || women.size() == 0) {
                    continue;
                } else {
                    // DATA 1 - get rate of births by mothers age and birth order
                    DataKey key = new DataKey(age, order, maxBirthOrderInCohort, women.size());

                    double birthRate = desiredPopulationStatistics.getOrderedBirthRates(currentTime).getCorrectingData(key) * config.getBirthTimeStep().toDecimalRepresentation();
                    //taperedOrderedBirthRatesForMothersOfThisAge.getData(order) * config.getBirthTimeStep().toDecimalRepresentation();

                    int numberOfChildrenToBirth;
                    int totalNumberOfMothers;
                    Map<Integer, Integer> motherCountsByMaternitySize;
                    int eligableWomen;

                    do {
                        // use DATA 1 to see how many many children need to be born
                        numberOfChildrenToBirth = calculateChildrenToBeBorn(sizeOfCohort, birthRate);


                        // calculate numbers of mothers to give birth (and which will bear twins, etc.)
                        // use DATA 2 to decide how many mothers needed to birth children
                        motherCountsByMaternitySize = calculateMotherCountsByMaternitySize(numberOfChildrenToBirth, proportionOfChildrenBornToEachSizeOfMaternity);

                        // check the mother counts are possible to meet with the current cohort
                        totalNumberOfMothers = CollectionUtils.sumIntegerCollection(motherCountsByMaternitySize.values());

                        eligableWomen = eligableMothers(women, currentTime);

                        if (eligableWomen < totalNumberOfMothers || eligableWomen == 0) {
                            if (eligableWomen == 0) {
                                birthRate = 0.0;
                            } else {
                                double scalingFactor = eligableWomen / (double) totalNumberOfMothers;
                                birthRate = scalingFactor * birthRate;
                            }
                            log.info("Rescaling Birth Rate | Current Date: " + currentTime.toString() + " - Insufficient number of mothers: Eligible women " + women.size() + " | Mothers Required " + totalNumberOfMothers + " | Age " + age + " | Order " + order);
                        }

                    } while (eligableWomen < totalNumberOfMothers);


                    birthCount += numberOfChildrenToBirth;
                    desiredPopulationStatistics.getOrderedBirthRates(currentTime).returnAppliedData(key, birthRate / config.getBirthTimeStep().toDecimalRepresentation());

                    // select the mothers
                    for (Integer childrenInMaternity : motherCountsByMaternitySize.keySet()) {

                        ArrayList<IPerson> mothersToBe = null;

                        try {
                            mothersToBe = new ArrayList<>(people.getFemales().removeNPersons(motherCountsByMaternitySize.get(childrenInMaternity), yearOfBirthInConsideration, order, currentTime));
                        } catch (InsufficientNumberOfPeopleException e) {
                            log.fatal(e.getMessage() + " for allocation of mothers");
                            throw e;
                        }


                        for (int n = 0; n < motherCountsByMaternitySize.get(childrenInMaternity); n++) {
                            IPerson mother = mothersToBe.get(n);

                            // make and assign the specified number of children - assign to correct place in population
                            for (int c = 0; c < childrenInMaternity; c++) {
                                // TODO vary birth date in time period
                                try {

                                    mother.recordPartnership(PersonFactory.formNewChildInPartnership(getRandomFather(people, mother.getBirthDate()), mother, currentTime, config.getBirthTimeStep(), people));
                                } catch (InsufficientNumberOfPeopleException e) {
                                    throw e;
                                }
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

        log.info("Births handled: " + currentTime.toString() + " - " + birthCount);
        System.out.println("Births handled: " + currentTime.toString() + " - " + birthCount);
        return birthCount;

    }

    private static int eligableMothers(Collection<IPerson> women, DateClock currentTime) {

        int count = 0;

        for (IPerson w : women) {
            if (w.noRecentChildren(currentTime)) {
                count++;
            }
        }

        return count;

    }

    public static int getMaxAgeForChildBearing(DateClock currentTime, PopulationStatistics desiredPopulationStatistics) {
        return desiredPopulationStatistics.getOrderedBirthRates(currentTime).getLargestLabel().getMax();
    }

    private static int getMinimumAgeForChildbearing(DateClock currentTime, PopulationStatistics desiredPopulationStatistics) {
        return desiredPopulationStatistics.getOrderedBirthRates(currentTime).getSmallestLabel();
    }

    private static OneDimensionDataDistribution transformMaternityProportionsToChildrenProportions(OneDimensionDataDistribution multipleBirthDataForMothersOfThisAgeByMaternity) {

        Map<IntegerRange, Double> temp = multipleBirthDataForMothersOfThisAgeByMaternity.cloneData();

        double sumOfScaledValues = 0;

        for (IntegerRange iR : temp.keySet()) {
            double scaledValue = iR.getMin() * temp.get(iR);
            sumOfScaledValues += scaledValue;
            temp.replace(iR, scaledValue);
        }

        for (IntegerRange iR : temp.keySet()) {
            double proportionalValue = temp.get(iR) / sumOfScaledValues;
            temp.replace(iR, proportionalValue);
        }

        return new OneDimensionDataDistribution(multipleBirthDataForMothersOfThisAgeByMaternity.getYear(),
                multipleBirthDataForMothersOfThisAgeByMaternity.getSourcePopulation(),
                multipleBirthDataForMothersOfThisAgeByMaternity.getSourceOrganisation(),
                temp);

    }

    public static int calculateChildrenToBeBorn(int sizeOfCohort, Double birthRate) {
        return SharedLogic.calculateNumberToHaveEvent(sizeOfCohort, birthRate);
    }

    private static Map<Integer, Integer> calculateMotherCountsByMaternitySize(int numberOfChildrenToBirth, OneDimensionDataDistribution proportionOfChildrenBornToEachSizeOfMaternity) {

        // In the comments 'maternity type' is used to term how many children are born from the maternity
        // i.e. a single child maternity or a two child maternity would be an example of two maternity types

        // calculate numbers of children to be born from each maternity type
        Map<IntegerRange, Double> temp = proportionOfChildrenBornToEachSizeOfMaternity.cloneData();

        for (IntegerRange iR : temp.keySet()) {
            double exactNumberOfChildren = temp.get(iR) * numberOfChildrenToBirth;
            temp.replace(iR, exactNumberOfChildren);
        }

        double sumOfRemainders = 0;

        int numberOfChildrenShort = numberOfChildrenToBirth - MapUtils.sumOfFlooredValues(temp);

        // therefore calculate the resulting number of mothers for each maternity type
        for (IntegerRange iR : temp.keySet()) {
            double exactNumberOfMothers = temp.get(iR) / iR.getMin();
            sumOfRemainders += exactNumberOfMothers - (int) exactNumberOfMothers;
            temp.replace(iR, exactNumberOfMothers);
        }

        // handle rounding
        // check if total number of children resulting from this is correct
        while (numberOfChildrenShort > 0) {
            // first by split number line dice roll
            try {
                numberOfChildrenShort -= performBalancingIterationOnNumbersOfMothers(temp, sumOfRemainders);
            } catch (StatisticalManipulationCalculationError e) {
                log.fatal(e.getMessage());
                System.exit(401);
            }
        }

        // if the number line dice roll caused an additional set of twins or triplets or etc.
        while (numberOfChildrenShort < 0) {
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

    private static int removeLowerTypeMaternities(Map<IntegerRange, Double> temp, int numberOfChildrenShort) throws StatisticalManipulationCalculationError {

        // this should be done proportionally by their rounding error
        double sumOfQualifyingRemainders = 0;

        for (IntegerRange iR : temp.keySet()) {

            if (iR.getValue() <= Math.abs(numberOfChildrenShort)) {
                double motherCount = temp.get(iR);
                double remainder = motherCount - (int) motherCount;
                sumOfQualifyingRemainders += remainder;
            }

        }

        double random = randomNumberGenerator.nextDouble();

        double uptoOnNumberLine = 0;

        for (IntegerRange iR : temp.keySet()) {

            if (iR.getValue() <= Math.abs(numberOfChildrenShort)) {
                double motherCount = temp.get(iR);
                double remainder = motherCount - (int) motherCount;

                uptoOnNumberLine += remainder / sumOfQualifyingRemainders;
                if (random < uptoOnNumberLine) {
                    if (motherCount - 1 >= 0) {
                        temp.replace(iR, motherCount - 1);
                    }
                    return iR.getValue();
                }
            }
        }

        throw new StatisticalManipulationCalculationError("Fatal balancing error has occurred in the method: Simulation.removeLowerTypeMaternities(...)");

    }

    private static int performBalancingIterationOnNumbersOfMothers(Map<IntegerRange, Double> temp, double sumOfRemainders) throws StatisticalManipulationCalculationError {

        double random = randomNumberGenerator.nextDouble();

        double uptoOnNumberLine = 0;

        for (IntegerRange iR : temp.keySet()) {

            double motherCount = temp.get(iR);
            double remainder = motherCount - (int) motherCount;

            uptoOnNumberLine += remainder / sumOfRemainders;
            if (random < uptoOnNumberLine) {
                temp.replace(iR, motherCount + 1);
                return iR.getValue();
            }
        }

        throw new StatisticalManipulationCalculationError("Fatal balancing error has occurred in the method: Simulation.performBalancingIterationOnNumbersOfMothers(...)");

    }


    public static IPerson getRandomFather(PeopleCollection population, Date fathersYearOfBirth) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> males = population.getMales().getByYear(fathersYearOfBirth);

        if(males.size() == 0) {
            throw new InsufficientNumberOfPeopleException("No males alive in simulation");
        }

        int r = randomNumberGenerator.nextInt(males.size());
        return males.toArray(new IPerson[males.size()])[r];

    }
}
