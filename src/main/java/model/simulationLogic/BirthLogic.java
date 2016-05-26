package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.Person;
import model.PersonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.CollectionUtils;
import utils.MapUtils;
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
                              PeopleCollection people) {

        int birthCount = 0;

        // make children/decide on mothers

        // for each age of mothers of childbearing age (AGE OF MOTHER)
        int minAge = desiredPopulationStatistics.getOrderedBirthRates(currentTime.getYearDate()).getMinRowLabelValue();
        int maxAge = desiredPopulationStatistics.getOrderedBirthRates(currentTime.getYearDate()).getMaxRowLabelValue().getMax();


        for (int age = minAge; age < maxAge; age++) {

            YearDate yearOfBirthInConsideration = new YearDate(currentTime.getYear() - age);

            Map<Integer, Collection<Person>> womenOfThisAge = people.getFemales().getMapByYear(yearOfBirthInConsideration);

            // DATA - get rate of births by mothers age
            OneDimensionDataDistribution orderedBirthRatesForMothersOfThisAge = desiredPopulationStatistics.getOrderedBirthRates(currentTime.getYearDate()).getData(age);
            OneDimensionDataDistribution taperedOrderedBirthRatesForMothersOfThisAge = transformOrderedBirthRatesToTaperByOrderCount(orderedBirthRatesForMothersOfThisAge, womenOfThisAge.keySet());

            // DATA 2 - get rate of multiple births in a maternity by mothers age
            OneDimensionDataDistribution multipleBirthDataForMothersOfThisAgeByMaternity = desiredPopulationStatistics.getMultipleBirthRates(currentTime.getYearDate()).getData(age);
            OneDimensionDataDistribution proportionOfChildrenBornToEachSizeOfMaternity = transformMaternityProportionsToChildrenProportions(multipleBirthDataForMothersOfThisAgeByMaternity);

            int maxBirthOrderInCohort = MapUtils.getMax(womenOfThisAge.keySet());
            int sizeOfCohort = MapUtils.countPeopleInMap(womenOfThisAge);


            // for each number of children already birthed to mothers (BIRTH ORDER)
            for (int order = 0; order <= maxBirthOrderInCohort; order++) {

                // women of this age and birth order
                Collection<Person> women = womenOfThisAge.get(order);

                if (women == null) {
                    women = new ArrayList<Person>();
                }


                // DATA 1 - get rate of births by mothers age and birth order
                double birthRate = taperedOrderedBirthRatesForMothersOfThisAge.getData(order) * config.getBirthTimeStep().toDecimalRepresentation();

                System.out.println("Age " + age + " | Order " + order + " | BR " + birthRate);

                // use DATA 1 to see how many many children need to be born
                int numberOfChildrenToBirth = calculateChildrenToBeBorn(sizeOfCohort, birthRate);

                birthCount += numberOfChildrenToBirth;

                // calculate numbers of mothers to give birth (and which will bear twins, etc.)
                // use DATA 2 to decide how many mothers needed to birth children
                Map<Integer, Integer> motherCountsByMaternitySize = calculateMotherCountsByMaternitySize(numberOfChildrenToBirth, proportionOfChildrenBornToEachSizeOfMaternity);

                // check the mother counts are possible to meet with the current cohort
                int totalNumberOfMothers = CollectionUtils.sumIntegerCollection(motherCountsByMaternitySize.values());

                System.out.println("Cohort Size " + sizeOfCohort + " | Number of Children " + numberOfChildrenToBirth + " | Number Of Mothers " + totalNumberOfMothers);

                if (women.size() < totalNumberOfMothers) {
                    log.fatal("Current Date: " + currentTime.toString() + " - Insufficient number of mothers: Eligible women " + women.size() + " | Mothers Required " + totalNumberOfMothers + " | Age " + age + " | Order " + order);
//                    totalNumberOfMothers = women.size();
                    System.exit(451);
                }

                // select the mothers
                for (Integer childrenInMaternity : motherCountsByMaternitySize.keySet()) {

                    ArrayList<Person> mothersToBe = new ArrayList<>(people.getFemales().removeNPersons(motherCountsByMaternitySize.get(childrenInMaternity), yearOfBirthInConsideration, order));
                    for (int n = 0; n < motherCountsByMaternitySize.get(childrenInMaternity); n++) {
                        Person mother = mothersToBe.get(n);

                        // make and assign the specified number of children - assign to correct place in population
                        for (int c = 0; c < childrenInMaternity; c++) {
                            // TODO vary birth date in time period
                            mother.recordPartnership(PersonFactory.formNewChildInPartnership(mother, currentTime, people));
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

        log.info("Births handled: " + currentTime.toString() + " - " + birthCount);
        return birthCount;

    }

    private static OneDimensionDataDistribution transformOrderedBirthRatesToTaperByOrderCount(OneDimensionDataDistribution orderedBirthRatesForMothersOfThisAge, Set<Integer> orders) {

        IntegerRange largestExplicitRange = orderedBirthRatesForMothersOfThisAge.getMaxRowLabelValue();

        if (largestExplicitRange.isPlus()) {

            int largestExplicitValue = largestExplicitRange.getValue();
            Double birthRateToShare = orderedBirthRatesForMothersOfThisAge.getData(largestExplicitValue);

            int toShareAmong = 0;
            int denominator = 0;

            for (Integer i : orders) {
                if (i <= largestExplicitValue) {
                    toShareAmong++;
                    denominator += Math.pow(toShareAmong, 3);
                }
            }

            Map<IntegerRange, Double> temp = new HashMap<IntegerRange, Double>();
            ArrayList<Integer> orderedOrders = new ArrayList<>(orders);
            Collections.sort(orderedOrders);

            int tally = 0;

            for (Integer i : orderedOrders) {

                while (tally < i) {
                    temp.put(new IntegerRange(tally), 0.0);
                    tally++;
                }

                if (i < largestExplicitValue) {
                    temp.put(new IntegerRange(i), orderedBirthRatesForMothersOfThisAge.getData(i));
                } else {
                    double fraction = Math.pow(toShareAmong, 3) / denominator;
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


        MapUtils.print("A", temp, 1, 1, 4);

        for (IntegerRange iR : temp.keySet()) {
            double exactNumberOfChildren = temp.get(iR) * numberOfChildrenToBirth;
            temp.replace(iR, exactNumberOfChildren);
        }


        MapUtils.print("B", temp, 1, 1, 4);

        double sumOfRemainders = 0;

        int numberOfChildrenShort = numberOfChildrenToBirth - MapUtils.sumOfFlooredValues(temp);

        // therefore calculate the resulting number of mothers for each maternity type
        for (IntegerRange iR : temp.keySet()) {
            double exactNumberOfMothers = temp.get(iR) / iR.getMin();
            sumOfRemainders += exactNumberOfMothers - (int) exactNumberOfMothers;
            temp.replace(iR, exactNumberOfMothers);
        }

        MapUtils.print("C", temp, 1, 1, 4);

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


        MapUtils.print("D", temp, 1, 1, 4);

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

        MapUtils.print("E", temp, 1, 1, 4);

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
                    temp.replace(iR, motherCount - 1);
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



}
