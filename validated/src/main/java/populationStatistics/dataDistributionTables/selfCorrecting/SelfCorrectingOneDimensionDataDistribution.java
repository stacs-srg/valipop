package populationStatistics.dataDistributionTables.selfCorrecting;

import dateModel.DateUtils;
import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
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
            double rateToApply = calcAdjustedRate(tD, key.getConsideredTimePeriod());
            return resolveRateToCount(key, rateToApply);
        }

        // to apply to
        int tAT = key.getForNPeople();

        // applied rate
        double aD = appliedRates.get(age);

        // if no N value given in StatsKey
        if(tAT == 0) {
            double rateToApply = calcAdjustedRate(tD, key.getConsideredTimePeriod());
            return resolveRateToCount(key, rateToApply);
        }

        // Correction rate
        double cD = ( tD * ( aC + tAT ) - ( aD * aC ) ) / tAT;

        if(cD < 0) {
            cD = 0;
        }


        // New additions
        if(cD > 1) {
            cD = 1;
        }

//        System.out.println("a: " + age + "   |   tD: " + tD + "   |   cD: " + cD + "   |   tAT: " + tAT + "   |   aD: " + aD  + "   |   aC: " + aC );

        double rateToApply = calcAdjustedRate(cD, key.getConsideredTimePeriod());
        return resolveRateToCount(key, rateToApply);
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        StatsKey key = achievedCount.getKey();

        int count = achievedCount.getFufilledCount();
        double achievedRate = 0;
        if(key.getForNPeople() != 0) {
            achievedRate = count / (double) key.getForNPeople();
        }

        IntegerRange age = resolveRowValue(key.getYLabel());

        // old applied rate
        double aDo = appliedRates.get(age);

        // old applied count
        double aCo = appliedCounts.get(age);

        // actually applied correction rate
        double stepsInYear = DateUtils.stepsInYear(key.getConsideredTimePeriod());
        double aacD = 1 - Math.pow(1 - achievedRate, stepsInYear);

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

//        System.out.println("a: " + age + "   |   tD: " + tD + "   |   aaCD: " + aacD + "   |   tAT: " + tAT + "   |   aDo: " + aDo + "   |   aDn " + aDn + "   |   aCo: " + aCo  + "   |   aCn: " + aCn );

//        // if new applied rate has switched across target rate then reset count
        if((aDo < tD && aDn >= tD) || (aDo > tD && aDn <= tD)) {

//            System.out.println("Counts reset   |   y: " + getYear().rowAsString() + " |   a: " + data.getYLabel());
            // calc r - the number of people it takes to get the applied rate back to the target rate
//            double r = ( aDo * aCo ) / ( aacD * ( tD - 1 ) );

            double numberOfPeopleToBringRateToCrossOverPoint;

            if(tD == aacD) {
                numberOfPeopleToBringRateToCrossOverPoint = tAT;
            } else {
                numberOfPeopleToBringRateToCrossOverPoint = (aCo * (aDo - tD)) / (tD - aacD);
            }

//            System.out.println("r : " + r);

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

    private double calcAdjustedRate(double rate, CompoundTimeUnit timePeriod) {

        double stepsInYear = DateUtils.stepsInYear(timePeriod);
        double adjustedRate = 1 - Math.pow(1 - rate, 1 / stepsInYear);

        return adjustedRate;
    }

}
