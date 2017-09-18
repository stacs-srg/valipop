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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Date extends Comparable<Date> {


    int getYear();

    /**
     * Months are indexed from 1 to 12 (i.e. Jan to Dec)
     * @return the month number in the year - where 1 is January
     */
    int getMonth();

    /**
     * Days are indexed from 1 to the number of days in the given month.
     * @return the day number in the month - where 1 is the 1st of the month
     */
    int getDay();

    String toString();

    java.util.Date getDate();

    ExactDate getExactDate();

    YearDate getYearDate();

    String toOrderableString();

    MonthDate getMonthDate();


}