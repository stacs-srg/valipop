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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class KaplanMeierAnalysis implements IKaplanMeierAnalysis {

    private final Date year;
    private final EventType event;
    private double logRankValue;

    private double p = 0.05;

    public KaplanMeierAnalysis(EventType event, Date year, double logRankValue) {
        this.event = event;
        this.year = year;
        this.logRankValue = logRankValue;
    }

    @Override
    public EventType getVariable() {
        return event;
    }

    @Override
    public Date getYear() {
        return year;
    }

    @Override
    public double getLogRankValue() {
        return logRankValue;
    }

    public void setLogRankValue(double logRankValue) {
        this.logRankValue = logRankValue;
    }

    @Override
    public double getPValue() {
        ChiSquaredDistribution cSD = new ChiSquaredDistribution(1.0);
        return 1 - cSD.cumulativeProbability(logRankValue);
    }

    @Override
    public boolean significantDifferenceBetweenGroups() {
        ChiSquaredDistribution cSD = new ChiSquaredDistribution(1.0);
        double p = 1 - cSD.cumulativeProbability(logRankValue);

        if(Double.isNaN(p)) {
            return true;
        }

        return this.p >= p;
    }

    @Override
    public void setConfidenceLevel(double confidenceLevel) {
        if (confidenceLevel <= 1) {
            this.p = confidenceLevel;
        }
    }

    @Override
    public double getHazardRatio() {
        return 0;
    }

    @Override
    public double getHazardRatioConfidenceIntervals() {
        return 0;
    }

    @Override
    public boolean significantHazardRatioSimilarity() {
        return false;
    }


}
