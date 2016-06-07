package utils.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtils {

    private static final int MONTHS_IN_YEAR = 12;

    public static CompoundTimeUnit differenceInYears(Date a, Date b) {

        int months = differenceInMonths(a, b).getCount();

        return new CompoundTimeUnit(months / MONTHS_IN_YEAR, TimeUnit.YEAR);
    }

    public static CompoundTimeUnit differenceInMonths(Date a, Date b) {

        if (dateBefore(b, a)) {
            Date temp = a;
            a = b;
            b = temp;
        }

        int yearDiffInMonths = MONTHS_IN_YEAR * Math.abs(a.getYear() - b.getYear());

        return new CompoundTimeUnit(yearDiffInMonths + (b.getMonth() - a.getMonth()), TimeUnit.MONTH);
    }


    public static boolean dateBefore(Date a, Date b) {

        if (a.getYear() < b.getYear()) {
            return true;
        } else if (a.getYear() == b.getYear()) {
            if (a.getMonth() < b.getMonth()) {
                return true;
            } else if (a.getMonth() == b.getMonth()) {
                return a.getDay() <= b.getDay();
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

    public static boolean matchesInterval(Date date, CompoundTimeUnit interval) {

        // If year interval and first month of year
        if (interval.getUnit() == TimeUnit.YEAR) {
            if (date.getMonth() == 1) {
                // Checks year based on remainder from the year 0
                if (date.getYear() % interval.getCount() == 0) {
                    return true;
                }
            }
        }

        // If month interval
        if (interval.getUnit() == TimeUnit.MONTH) {
            // Offset by one to make month 1, 4, 7, 10 the months for the quarterly interval
            if ((date.getMonth() - 1) % interval.getCount() == 0) {
                return true;
            }
        }

        return false;

    }

    public static Date getEarlistDate(Date startDate, Date startDate1) {
        if(dateBefore(startDate, startDate1)) {
            return startDate;
        } else {
            return startDate1;
        }
    }

    public static Date getLatestDate(Date startDate, Date startDate1) {
        if(dateBefore(startDate, startDate1)) {
            return startDate1;
        } else {
            return startDate;
        }
    }
}
