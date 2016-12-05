package model.simulationLogic.stochastic;

import datastructure.population.FemaleCollection;
import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.simulationEntities.PersonFactory;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthsStochastic {

    public static Random random = new Random();

    // The purpose of this class is to:
        // take in a population
        // select mothers of the correct age and order to give birth
        // create new children for each birth (these should have no fathers)
        // If a shortage of females exist to mother children then these should be kept over to the next cohort

    public static Collection<IPartnership> handleBirths(FemaleCollection females, PopulationStatistics desired,
                                                        DateClock currentDate, CompoundTimeUnit birthTimeStep,
                                                        PeopleCollection population)
                                                        throws InsufficientNumberOfPeopleException {

        Collection<IPartnership> selectedForEvent = new ArrayList<>();

        // for females of each age bound
        SelfCorrectingTwoDimensionDataDistribution ratesTable = desired.getOrderedBirthRates(currentDate);
        ArrayList<IntegerRange> ageRanges = getIntegerRangesInOrder(ratesTable);

        for(IntegerRange ageRange : ageRanges) {

            SelfCorrectingOneDimensionDataDistribution tableRow = ratesTable.getData(ageRange.getValue());
            Map<IntegerRange, Collection<IPerson>> femalesByOrders = new HashMap<>();
            int femalesOfAge = 0;

            // for each order
            ArrayList<IntegerRange> orders = getIntegerRangesInOrder(tableRow);

            for(IntegerRange order : orders) {
                // get females to be mothers by rate for order by in age range
                Collection<IPerson> femalesOfAgeAndOrder = getFemales(females, ageRange, order, currentDate);
                femalesOfAge += femalesOfAgeAndOrder.size();
                femalesByOrders.put(order, femalesOfAgeAndOrder);
            }

            for(IntegerRange order : orders) {
                // get females to be mothers by rate for order by in age range
                Collection<IPerson> femalesOfAgeAndOrder = femalesByOrders.get(order);

                double rate = tableRow.getData(order.getValue());

                int eventOccursNTimes = BernoulliApproach.chooseValue(femalesOfAge, rate, random);

                try {
                    Collection<IPerson> mothersToBe = removeNPeople(eventOccursNTimes, femalesOfAgeAndOrder);

                    for(IPerson mother : mothersToBe) {
                        selectedForEvent.add(PersonFactory.formNewPartnership(1, mother, currentDate, birthTimeStep, population));
                    }

                } catch (InsufficientNumberOfPeopleException e) {
                    throw new InsufficientNumberOfPeopleException(e.getMessage() + ": Current Date " +
                            currentDate.toString() + " Age Range " + ageRange.toString() + " Order " + order);
                }


            }
        }

        return selectedForEvent;

    }

    private static Collection<IPerson> removeNPeople(int nTimes, Collection<IPerson> people) throws InsufficientNumberOfPeopleException {

        ArrayList<IPerson> peopleAL = new ArrayList<>(people);

        Collection<IPerson> removed = new ArrayList<>();

        for(int i = 0; i < nTimes; i++) {

            if(peopleAL.size() == 0) {
                throw new InsufficientNumberOfPeopleException("Shortage of females to make into mothers");
            }

            removed.add(peopleAL.remove(random.nextInt(people.size())));
        }

        return removed;
    }

    private static Collection<IPerson> getFemales(FemaleCollection females, IntegerRange ageRange, IntegerRange order, Date currentDate) {

        ArrayList<IPerson> femalesOfAgeAndOrder = new ArrayList<>();

        // Birth Date bounds on age range for given date
        DateInstant earliestDOB = DateUtils.calculateDateInstant(currentDate, DateUtils.getDaysInTimePeriod(currentDate, new CompoundTimeUnit(ageRange.getMax() + 1, TimeUnit.YEAR).negative()));
        DateInstant latestDOB = DateUtils.calculateDateInstant(currentDate, DateUtils.getDaysInTimePeriod(currentDate, new CompoundTimeUnit(ageRange.getMin(), TimeUnit.YEAR).negative()) - 1);

        try {
            for(YearDate y = earliestDOB.getYearDate(); DateUtils.dateBefore(y, latestDOB); y = y.getDateClock().advanceTime(1, TimeUnit.YEAR).getYearDate()) {

                Collection<IPerson> femalesFromYearAndOrder = females.getByYearAndBirthOrder(y, order.getValue());

                if(y.getYear() == earliestDOB.getYear() || y.getYear() == latestDOB.getYear()) {

                        if(firstDayOfEarliestYear(y, earliestDOB) && lastDayOfLatestYear(y, latestDOB)) {
                            // All females from year can be used
                            femalesOfAgeAndOrder.addAll(femalesFromYearAndOrder);

                        } else if (y.getYear() == earliestDOB.getYear() && y.getYear() == latestDOB.getYear()) {
                            // Earliest and latest DOB in same year and fromprevious assertions we can tell that
                            // truncation of the year has occured, therefore find persons in data bound
                            Collection<IPerson> ofCorrectAge = getPersonsBornInDateBound(femalesFromYearAndOrder, earliestDOB, latestDOB);
                            femalesOfAgeAndOrder.addAll(ofCorrectAge);

                        } else if (firstDayOfEarliestYear(y, earliestDOB) || lastDayOfLatestYear(y, latestDOB)) {
                            // All females from year can be used
                            femalesOfAgeAndOrder.addAll(femalesFromYearAndOrder);

                        } else {
                            // If earliestDOB is not the 1/1/YYYY then we handle in here to make sure we do not get over aged individuals get
                            // Or if latestDOB is not the 31/12/YYYY then we handle in here to make sure we do not get under aged individuals get
                            Collection<IPerson> ofCorrectAge = getPersonsBornInDateBound(femalesFromYearAndOrder, earliestDOB, latestDOB);
                            femalesOfAgeAndOrder.addAll(ofCorrectAge);

                        }

                } else {
                    // This is a middle year and thus everyone in the year is okay to be returned
                    femalesOfAgeAndOrder.addAll(femalesFromYearAndOrder);
                }

            }
        } catch (UnsupportedDateConversion unsupportedDateConversion) {
            throw new Error("YearDate to DateClock conversion should not have resulted in an UnsupportedDateConversion", unsupportedDateConversion);
        }

        return femalesOfAgeAndOrder;
    }

    private static Collection<IPerson> getPersonsBornInDateBound(Collection<IPerson> femalesFromYear, DateInstant earliestDOB, DateInstant latestDOB) {

        Collection<IPerson> ofAgePersons = new ArrayList<>();

        for(IPerson p : femalesFromYear) {
            Date dob = p.getBirthDate();

            if(DateUtils.dateBefore(earliestDOB, dob) && DateUtils.dateBefore(dob, latestDOB)) {
                ofAgePersons.add(p);
            }

        }

        return ofAgePersons;
    }

    private static boolean firstDayOfEarliestYear(YearDate year, Date earliestDOB) {
        return year.getYear() == earliestDOB.getYear() && (earliestDOB.getMonth() == 1) && (earliestDOB.getDay() == 1);
    }

    private static boolean lastDayOfLatestYear(YearDate year, Date latestDOB) {
        return year.getYear() == latestDOB.getYear() && (latestDOB.getMonth() == 12) && (latestDOB.getDay() == 31);
    }

    private static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingOneDimensionDataDistribution tableRow) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(tableRow.getData().keySet());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    private static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingTwoDimensionDataDistribution table) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(table.getRowKeys());
        Collections.sort(integerRanges);
        return integerRanges;
    }

}
