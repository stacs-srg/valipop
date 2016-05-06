package model.time;

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
                throw new InvalidTimeUnit();
        }

        this.count = Integer.parseInt(count);

    }

    public int getCount() {
        return count;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
