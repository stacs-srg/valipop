package dateModel;

import dateModel.dateImplementations.ExactDate;
import dateModel.dateImplementations.MonthDate;
import dateModel.dateImplementations.YearDate;
import dateModel.exceptions.UnsupportedDateConversion;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Date extends Comparable<Date> {


    int getYear();

    /**
     * Months are indexed from 1 to 12 (i.e. Jan to Dec)
     * @return the month number in the year - where 1 is January
     */
    int getMonth();

    /**
     * Days are indexed from 1 to the number of days in the given month.
     * @return the day number in the month - where 1 is the 1st of the month
     */
    int getDay();

    String toString();

    java.util.Date getDate();

    ExactDate getExactDate();

    YearDate getYearDate();

//    MonthDate getDateClock() throws UnsupportedDateConversion;
//
//    /**
//     * If conversion would result in an UnsupportedDateConversion then day is manipulated to allow conversion. Obviously
//     * this removes the ability of exact date transforms and gives a many to one results mapping e.g. when converting a
//     * ExactDate any date in a month will return the first of that month
//     *
//     * @param force whether to force the date conversion - if true then exception will never be thrown
//     * @return The equivalent MonthDate
//     */
//    MonthDate getDateClock(boolean force) throws UnsupportedDateConversion;

    String toOrderableString();

    MonthDate getMonthDate();


}