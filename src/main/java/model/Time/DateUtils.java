package model.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtils {

    private static final int MONTHS_IN_YEAR = 12;

    public static CompoundTimeUnit differenceInYears(Date a, Date b) {

        return new CompoundTimeUnit(Math.abs(a.getYear() - b.getYear()), TimeUnit.YEAR);


    }

    public static CompoundTimeUnit differenceInMonths(Date a, Date b) {

        if (dateBefore(b, a)) {
            Date temp = a;
            a = b;
            b = temp;
        }

        int yearDiffInMonths = MONTHS_IN_YEAR * differenceInYears(a, b).getCount();

        return new CompoundTimeUnit(yearDiffInMonths + (b.getMonth() - a.getMonth()), TimeUnit.MONTH);
    }



    public static boolean dateBefore(Date a, Date b) {
        if (a.getYear() < b.getYear()) {
            return true;
        } else if (a.getYear() == b.getYear()) {
            if (a.getMonth() < b.getMonth()) {
                return true;
            } else if (a.getDay() <= b.getDay()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

}
