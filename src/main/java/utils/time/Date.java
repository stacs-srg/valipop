package utils.time;

import datastructure.summativeStatistics.structure.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Date extends Comparable<Date> {


    int getYear();

    int getMonth();

    int getDay();

    String toString();

    java.util.Date getDate();

    DateInstant getInstant();

    YearDate getYearDate();

    DateClock getDateClock() throws UnsupportedDateConversion;

    String toOrderableString();
}