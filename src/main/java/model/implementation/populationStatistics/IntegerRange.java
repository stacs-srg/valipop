package model.implementation.populationStatistics;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRange {

    private Integer min = null;
    private Integer max = null;
    Boolean minPlus = null;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntegerRange(int value, boolean minPlus) {
        this.min = value;
        this.minPlus = minPlus;
    }

    public boolean contains(int integer) {
        if(minPlus == null) {
            // if standard integerRange
            if(integer >= min && integer <= max) {
                return true;
            }
        } else {
            if(minPlus) {
                // if min value +
                if(integer >= min) {
                    return true;
                }
            } else {
                // if single value
                if(integer == min) {
                    return true;
                }
            }
        }

        return false;

    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

}
