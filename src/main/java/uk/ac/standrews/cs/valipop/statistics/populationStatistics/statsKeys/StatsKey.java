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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys;

import java.time.Period;
import java.time.Year;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StatsKey {

    private final int yLabel;
    private final int xLabel;
    private final double forNPeople;
    private final Period consideredTimePeriod;
    private final Year year;

    public StatsKey(int yLabel, int xLabel, double forNPeople, Period consideredTimePeriod, Year year) {

        this.yLabel = yLabel;
        this.xLabel = xLabel;
        this.forNPeople = forNPeople;
        this.consideredTimePeriod = consideredTimePeriod;
        this.year = year;
    }

    public StatsKey(int yLabel, double forNPeople, Period consideredTimePeriod, Year year) {

        this(yLabel, 0, forNPeople, consideredTimePeriod, year);
    }

    public double getForNPeople() {
        return forNPeople;
    }

    public Integer getXLabel() {
        return xLabel;
    }

    public Integer getYLabel() {
        return yLabel;
    }

    public Period getConsideredTimePeriod() {
        return consideredTimePeriod;
    }

    public Year getYear() {
        return year;
    }
}
