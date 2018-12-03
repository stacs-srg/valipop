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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dates;

import org.apache.commons.math3.random.RandomGenerator;

import java.time.LocalDate;
import java.time.Period;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateSelector {

    final RandomGenerator random;

    public DateSelector(RandomGenerator random) {

        this.random = random;
    }

    public LocalDate selectRandomDate(LocalDate earliestDate, LocalDate latestDate) {

        int daysInWindow = (int)DAYS.between(earliestDate, latestDate);

        return selectRandomDate(earliestDate, daysInWindow);
    }

    public LocalDate selectRandomDate(LocalDate earliestDate, Period timePeriod) {

        int daysInWindow = DateUtils.getDaysInTimePeriod(earliestDate, timePeriod);

        return selectRandomDate(earliestDate, daysInWindow);
    }

    private LocalDate selectRandomDate(LocalDate earliestDate, int daysInWindow) {

        return earliestDate.plus(daysInWindow, DAYS);
    }
}
