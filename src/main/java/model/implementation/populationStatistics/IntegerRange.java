package model.implementation.populationStatistics;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRange {

    Boolean plus = null;
    private Integer min = null;
    private Integer max = null;

    public IntegerRange(String label) {

        String[] parts = label.split("-");
        if (parts.length == 1) {
            try {
                min = Integer.parseInt(parts[0]);
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
                throw new InvalidRangeException();
            }
        } else {
            throw new NumberFormatException();
        }

    }

    public IntegerRange(int min, int max) {
        if (min >= max) {
            throw new InvalidRangeException();
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
        plus = false;
    }

    public boolean contains(int integer) {
        if (plus == null) {
            // if standard integerRange
            if (integer >= min && integer <= max) {
                return true;
            }
        } else {
            if (plus) {
                // if min value +
                if (integer >= min) {
                    return true;
                }
            } else {
                // if single value
                if (integer == min) {
                    return true;
                }
            }
        }

        return false;

    }

    public int getValue() {
        return min;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
