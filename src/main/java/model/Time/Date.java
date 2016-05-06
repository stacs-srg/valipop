package model.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Date {


    public int getYear();

    public int getMonth();

    public int getDay();

    public String toString();

    public java.util.Date getDate();

    public DateInstant getInstant();

    public YearDate getYearDate();
}
