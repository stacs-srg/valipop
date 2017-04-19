package populationStatistics.dataDistributionTables.selfCorrecting;

import dateModel.DateUtils;
import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import utils.MapUtils;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.integerRange.IntegerRange;


import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingOneDimensionDataDistribution extends OneDimensionDataDistribution {

    private Map<IntegerRange, Double> appliedRates;
    private Map<IntegerRange, Double> appliedCounts;

    public SelfCorrectingOneDimensionDataDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, Double> tableData) {
        super(year, sourcePopulation, sourceOrganisation, tableData);

        this.appliedRates = MapUtils.cloneODM(tableData);
        this.appliedCounts = MapUtils.cloneODM(tableData);

        for (IntegerRange iR : appliedCounts.keySet()) {
            appliedCounts.replace(iR, 0.0);
            appliedRates.replace(iR, 0.0);
        }

    }

    public DeterminedCount determineCount(StatsKey key) {

        IntegerRange age = resolveRowValue(key.getYLabel());

        // target rate
        double tD = targetRates.get(age);

        // applied count
        double aC = appliedCounts.get(age);

        // if no correction data - i.e. first call to this method
        if(aC == 0) {
            double rateToApply = calcSubRateFromYearRate(tD, key.getConsideredTimePeriod());
            return resolveRateToCount(key, rateToApply);
        }

        // to apply to
        int tAT = key.getForNPeople();

        // applied rate
        double aD = appliedRates.get(age);

        // if no N value given in StatsKey
        if(tAT == 0) {
            double rateToApply = calcSubRateFromYearRate(tD, key.getConsideredTimePeriod());
            return resolveRateToCount(key, rateToApply);
        }

        // Correction rate
        double cD = ( tD * ( aC + tAT ) - ( aD * aC ) ) / tAT;

        // Checks that rate falls in bounds
        if(cD < 0) {
            cD = 0;
        } else if(cD > 1) {
            cD = 1;
        }

        double rateToApply = calcSubRateFromYearRate(cD, key.getConsideredTimePeriod());
        return resolveRateToCount(key, rateToApply);
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

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
        int tAT = key.getForNPeople();

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


    private DeterminedCount resolveRateToCount(StatsKey key, double rate) {
        int determinedCount = (int) Math.round(rate * key.getForNPeople());
        return new DeterminedCount(key, determinedCount);
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
