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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange;

import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRange implements Comparable<IntegerRange> {

    Boolean plus = false;
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

    public Boolean contains(Integer integer) {
//        if (plus == null) {
//            // if standard integerRange
//            if (integer >= min && integer <= max) {
//                return true;
//            }
//        } else {
        if(value.equals("na")) {
            return null;
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


//        }

        return false;

    }

    public Integer getValue() {
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

        if(value.equals("na")) {
            return value;
        } else if(plus) {
            s += min;
            s += "+";
        } else {
            s += min;
            s += min.equals(max) ? "" : "to" + max;
        }
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

        return new Integer(hc);
    }

}
