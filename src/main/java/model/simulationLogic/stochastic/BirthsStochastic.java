package model.simulationLogic.stochastic;

import datastructure.population.FemaleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.simulationEntities.IPerson;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthsStochastic {

    // The purpose of this class is to:
        // take in a population
        // select mothers of the correct age and order to give birth
        // create new children for each birth (these should have no fathers)
        // If a shortage of females exist to mother children then these should be kept over to the next cohort

    public static List<IPerson> handleBirths(FemaleCollection females, PopulationStatistics desired, Date currentDate) {

        ArrayList<IPerson> selectedForEvent = new ArrayList<>();

        // for females of each age bound
        SelfCorrectingTwoDimensionDataDistribution ratesTable = desired.getOrderedBirthRates(currentDate);
        ArrayList<IntegerRange> ageRanges = getIntegerRangesInOrder(ratesTable);

        for(IntegerRange ageRange : ageRanges) {

            // for each order
            SelfCorrectingOneDimensionDataDistribution tableRow = ratesTable.getData(ageRange.getValue());
            ArrayList<IntegerRange> orders = getIntegerRangesInOrder(tableRow);

            for(IntegerRange order : orders) {

                // get females to be mothers by rate for order by in age range
                ArrayList<IPerson> femalesofAgeAndOrder = getFemales(females, ageRange, order, currentDate);

                // TODO next apply statistics here - go stochatic???
                tableRow.getData(order.getValue());

            }
        }

        return selectedForEvent;

    }

    private static ArrayList<IPerson> getFemales(FemaleCollection females, IntegerRange ageRange, IntegerRange order, Date currentDate) {

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
