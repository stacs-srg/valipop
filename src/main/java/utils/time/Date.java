package utils.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Date {


    int getYear();

    int getMonth();

    int getDay();

    String toString();

    java.util.Date getDate();

    DateInstant getInstant();

    YearDate getYearDate();

}