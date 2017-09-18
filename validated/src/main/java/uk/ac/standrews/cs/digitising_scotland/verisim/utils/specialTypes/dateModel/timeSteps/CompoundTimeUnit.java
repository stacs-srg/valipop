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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.exceptions.InvalidTimeUnit;

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
