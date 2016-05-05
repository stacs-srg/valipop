package model.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TimeUtils {

    public static CompoundTimeUnit differenceInYears(TimeInstant a, TimeInstant b) {

        return new CompoundTimeUnit(Math.abs(a.getYear() - b.getYear()), TimeUnit.YEAR);


    }

    public static CompoundTimeUnit differenceInMonths(TimeInstant a, TimeInstant b) {

        if(dateBefore(b, a)) {
            TimeInstant temp = a;
            a = b;
            b = temp;
        }

        int yearDiffInMonths = 12 * differenceInYears(a, b).getCount();

        return new CompoundTimeUnit(yearDiffInMonths + (b.getMonth() - a.getMonth()), TimeUnit.MONTH);
    }

    public static boolean dateBefore(TimeInstant a, TimeInstant b) {
        if(a.getYear() < b.getYear()){
            return true;
        } else if (a.getYear() == b.getYear()) {
            // TODO finish and write test
            if(a.getMonth() <= b.getMonth()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

}
