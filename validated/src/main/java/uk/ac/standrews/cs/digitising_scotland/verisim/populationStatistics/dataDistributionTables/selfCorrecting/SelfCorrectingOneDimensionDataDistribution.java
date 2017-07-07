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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.MapUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;


import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingOneDimensionDataDistribution extends OneDimensionDataDistribution {

    private boolean binominalSampling;
    private RandomGenerator rng;

    private Map<IntegerRange, Double> appliedRates;
    private Map<IntegerRange, Double> appliedCounts;

    public SelfCorrectingOneDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, Double> tableData, boolean binominalSampling) {
        super(year, sourcePopulation, sourceOrganisation, tableData);

        this.appliedRates = MapUtils.cloneODM(tableData);
        this.appliedCounts = MapUtils.cloneODM(tableData);
        this.binominalSampling = binominalSampling;

        for (IntegerRange iR : appliedCounts.keySet()) {
            appliedCounts.replace(iR, 0.0);
            appliedRates.replace(iR, 0.0);
        }

        rng = new JDKRandomGenerator();

    }

    public SingleDeterminedCount determineCount(StatsKey key) {

        IntegerRange age = resolveRowValue(key.getYLabel());

        // target rate
        double tD = targetRates.get(age);

//        if(binominalSampling) {
//            int determinedCount = new BinomialDistribution(rng, key.getForNPeople(), tD).sample();
//            return new SingleDeterminedCount(key, determinedCount);
//
//        } else {

            // applied count
            double aC = appliedCounts.get(age);

            // if no correction data - i.e. first call to this method
            if (aC == 0) {
                double rateToApply = calcSubRateFromYearRate(tD, key.getConsideredTimePeriod());
                return resolveRateToCount(key, rateToApply, rateToApply); // Same due to correction rate currently same as target rate
            }

            // to apply to
            Double tAT = key.getForNPeople();

            // applied rate
            double aD = appliedRates.get(age);

            // if no N value given in StatsKey
            if (tAT == 0) {
                double rateToApply = calcSubRateFromYearRate(tD, key.getConsideredTimePeriod());
                return resolveRateToCount(key, rateToApply, rateToApply);
            }

            // Correction rate
            double cD = (tD * (aC + tAT) - (aD * aC)) / tAT;

            // Checks that rate falls in bounds
            if (cD < 0) {
                cD = 0;
            } else if (cD > 1) {
                cD = 1;
            }

            double rateToApply = calcSubRateFromYearRate(cD, key.getConsideredTimePeriod());
            double uncorrectedRate = calcSubRateFromYearRate(tD, key.getConsideredTimePeriod());
            return resolveRateToCount(key, rateToApply, uncorrectedRate);
//        }
    }

    public void returnAchievedCount(DeterminedCount<Integer, Double> achievedCount) {

        StatsKey key = achievedCount.getKey();

        int count = achievedCount.getFufilledCount();
        double achievedRate = 0;
        if(key.getForNPeople() != 0) {
            achievedRate = count / (double) key.getForNPeople();
        }

        // This is age for Death (1DDD) but this is order in the case of birth (2DDD)
        IntegerRange age = resolveRowValue(key.getYLabel());

        // old applied rate
        double aDo = appliedRates.get(age);

        // old applied count
        double aCo = appliedCounts.get(age);

        // actually applied correction rate
        double aacD = calcAppliedYearRateFromSubRate(achievedRate, key.getConsideredTimePeriod());

        // to apply to
        Double tAT = key.getForNPeople();

        // new applied count
        double aCn = aCo + tAT;

        // new applied rate
        double aDn = 0;
        if(aCn != 0) {
            aDn = ((aDo * aCo) + (aacD * tAT)) / aCn;
        }

        // target rate
        double tD = targetRates.get(age);

        // if new applied rate has switched across target rate then reset count
        if((aDo < tD && aDn >= tD) || (aDo > tD && aDn <= tD)) {

            // the number of people it takes at the the applied rate back to the target rate
            double numberOfPeopleToBringRateToCrossOverPoint;

            if(tD == aacD) {
                numberOfPeopleToBringRateToCrossOverPoint = tAT;
            } else {
                numberOfPeopleToBringRateToCrossOverPoint = (aCo * (aDo - tD)) / (tD - aacD);
            }

            appliedRates.replace(age, aacD);
            appliedCounts.replace(age, tAT - numberOfPeopleToBringRateToCrossOverPoint);
        } else {
            appliedRates.replace(age, aDn);
            appliedCounts.replace(age, aCn);
        }

    }


    private SingleDeterminedCount resolveRateToCount(StatsKey key, double rate, double uncorrectedRate) {

        double rawCorrectedCount = rate * key.getForNPeople();
        double rawUncorrectedCount = uncorrectedRate * key.getForNPeople();

        int determinedCount;
        if(binominalSampling) {

            determinedCount = new BinomialDistribution(rng, (int) Math.round(key.getForNPeople()), rate).sample();
        } else {
            determinedCount = (int) Math.round(rate * key.getForNPeople());
        }
        return new SingleDeterminedCount(key, determinedCount, rawCorrectedCount, rawUncorrectedCount);
    }

    private double calcAppliedYearRateFromSubRate(double subRate, CompoundTimeUnit timePeriod) {
        double stepsInYear = DateUtils.stepsInYear(timePeriod);
//        return subRate * stepsInYear;

        double appliedYearRate = 1 - Math.pow(1 - subRate, stepsInYear);
        return appliedYearRate;
    }

    private double calcSubRateFromYearRate(double yearRate, CompoundTimeUnit timePeriod) {

        double stepsInYear = DateUtils.stepsInYear(timePeriod);
//        return yearRate / stepsInYear;

        double subRate = 1 - Math.pow(1 - yearRate, 1 / stepsInYear);

        return subRate;
    }

}
