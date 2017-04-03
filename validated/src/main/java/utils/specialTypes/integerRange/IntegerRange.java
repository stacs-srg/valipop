package utils.specialTypes.integerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRange implements Comparable<IntegerRange> {

    Boolean plus = false;
    private Integer min = null;
    private Integer max = null;

    public IntegerRange(String label) {

        String[] parts = label.split("-");
        if (parts.length == 1) {
            try {
                min = Integer.parseInt(parts[0]);
                max = Integer.parseInt(parts[0]);
                plus = false;
            } catch (NumberFormatException e) {
                if (parts[0].toCharArray()[parts[0].length() - 1] == '+') {
                    plus = true;
                    min = Integer.parseInt(parts[0].split("\\+")[0]);
                } else {
                    throw new NumberFormatException();
                }
            }
        } else if (parts.length == 2) {
            min = Integer.parseInt(parts[0]);
            max = Integer.parseInt(parts[1]);

            if (min >= max) {
                throw new InvalidRangeException("The minimum value of the range is greater than the maximum value");
            }
        } else {
            throw new NumberFormatException();
        }

    }

    public IntegerRange(int min, int max) {
        if (min >= max) {
            throw new InvalidRangeException("The minimum value of the range is greater than the maximum value");
        }

        this.min = min;
        this.max = max;
    }

    public IntegerRange(int value, boolean plus) {
        this.min = value;
        this.plus = plus;
    }

    public IntegerRange(int value) {
        this.min = value;
        this.max = value;
        plus = false;
    }

    public boolean contains(int integer) {
//        if (plus == null) {
//            // if standard integerRange
//            if (integer >= min && integer <= max) {
//                return true;
//            }
//        } else {

            if (plus) {
                // if min value +
                if (integer >= min) {
                    return true;
                }
            } else {
                // if single value
                if (integer >= min && integer <= max) {
                    return true;
                }
            }


//        }

        return false;

    }

    public int getValue() {
        return min;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        if (max == null) {
            return min;
        } else {
            return max;
        }
    }

    public boolean isPlus() {
        return plus;
    }

    public String toString() {
        String s = "";
        s += min;
        s += min.equals(max) ? "" : "to" + max;
        s += plus ? "+" : "";
        return s;
    }

    @Override
    public int compareTo(IntegerRange o) {
        return Integer.compare(getValue(), o.getValue());
    }

    /**
     * Returns each distinct integer value that is represented in the Integer Range. If a plus is present (e.g. this
     * range represents the value 4+) then the parameter given sets the highest value the return array can take.
     *
     * @param limitForPlus
     * @return
     */
    public int[] getValues(int limitForPlus) {

        int lMin = getMin();
        int lMax;

        if(plus && limitForPlus >= lMin) {
            lMax = limitForPlus;
        } else {
            lMax = getMax();
        }

        int numberOfValue = lMax - lMin + 1;
        int[] values = new int[numberOfValue];


        for(int i = 0; i < numberOfValue; i++) {
            values[i] = lMin + i;
        }

        return values;
    }
}
