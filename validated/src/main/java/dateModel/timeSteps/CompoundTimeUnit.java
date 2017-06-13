package dateModel.timeSteps;

import dateModel.DateUtils;
import dateModel.exceptions.InvalidTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CompoundTimeUnit {

    private final int count;
    private final TimeUnit unit;

    public CompoundTimeUnit(int count, TimeUnit unit) {
        this.count = count;
        this.unit = unit;
    }

    public CompoundTimeUnit(String compoundTimeUnit) {

        String count = compoundTimeUnit.substring(0, compoundTimeUnit.length() - 1);
        char unit = compoundTimeUnit.toCharArray()[compoundTimeUnit.length() - 1];

        switch (unit) {
            case ('m'):
                this.unit = TimeUnit.MONTH;
                break;
            case ('y'):
                this.unit = TimeUnit.YEAR;
                break;
            default:
                throw new InvalidTimeUnit("Invalid time unit specified");
        }

        this.count = Integer.parseInt(count);

    }

    public int getCount() {
        return count;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public double toDecimalRepresentation() {
        if (unit == TimeUnit.YEAR) {
            return (double) count;
        } else {
            return count / (double) DateUtils.MONTHS_IN_YEAR;
        }
    }

    public CompoundTimeUnit negative() {
        return new CompoundTimeUnit(-count, unit);
    }

    public String toString() {

        String ret = Integer.toString(count);

        switch(unit) {

            case MONTH:
                ret += "m";
                break;
            case YEAR:
                ret += "y";
                break;
        }

        return ret;

    }
}
