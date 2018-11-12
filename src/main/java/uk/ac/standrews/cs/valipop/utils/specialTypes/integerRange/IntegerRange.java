/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange;

import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRange implements Comparable<IntegerRange> {

    boolean plus = false;
    private Integer min = null;
    private Integer max = null;

    String value = "";

    public IntegerRange(String label) {

        if(Objects.equals(label, "na")) {
            value = "na";
        } else {

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
                        throw new NumberFormatException(label);
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
    }

    public IntegerRange(int value) {
        this.min = value;
        this.max = value;
        plus = false;
    }

    public boolean contains(Integer integer) {

        if(value.equals("na")) {
            throw new InvalidRangeException("Range value na - cannot contain value: " + integer);
        }

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

        return false;
    }

    public Integer getValue() {
        return min;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
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

        if(value.equals("na")) {
            return value;
        } else if(plus) {
            s += min;
            s += "+";
        } else {
            s += min;
            s += min.equals(max) ? "" : "-" + max;
        }
        return s;
    }

    @Override
    public int compareTo(IntegerRange other) {

        if (min == null && other.min == null) return 0;
        if (min == null) return -1;
        if (other.min == null) return 1;

        if (min < other.min) return -1;
        if (min > other.min) return 1;

        if (max == null && other.max == null) return 0;
        if (max == null) return -1;
        if (other.max == null) return 1;

        return Integer.compare(min, other.min);
    }

    public int hash() {

        String hc = "";

        if(value.equals("na")) {

            hc += "0";

        } else {
            if (plus) {
                hc += "2";
            } else {
                hc += "1";
            }

            if (min != null) {
                hc += min.toString();
            } else {
                hc += String.valueOf(getValue());
            }

            hc += "0";

            if (max != null) {
                hc += max.toString();
            } else {
                hc += String.valueOf(getValue());
            }
        }

        return Integer.parseInt(hc);
    }
}
