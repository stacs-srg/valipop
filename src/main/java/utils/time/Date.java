package utils.time;

import datastructure.summativeStatistics.structure.IntegerRange;

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

    DateInstant getDateInstant();

    YearDate getYearDate();

    DateClock getDateClock() throws UnsupportedDateConversion;

    String toOrderableString();
}