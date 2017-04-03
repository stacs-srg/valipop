package populationStatistics.dataDistributionTables.selfCorrecting;

import dateModel.DateUtils;
import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import dateModel.dateImplementations.YearDate;
import utils.MapUtils;
import utils.specialTypes.dataKeys.DataKey;
import utils.specialTypes.integerRange.IntegerRange;


import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingOneDimensionDataDistribution extends OneDimensionDataDistribution implements SelfCorrection {

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

    @Override
    public double getCorrectingRate(DataKey data, CompoundTimeUnit consideredTimePeriod) {

        IntegerRange age = resolveRowValue(data.getYLabel());

        // target rate
        double tD = targetData.get(age);

        // applied count
        double aC = appliedCounts.get(age);

        // if no correction data - i.e. first call to this method
        if(aC == 0) {

            return calcAdjustedRate(tD, consideredTimePeriod);
        }

        // to apply to
        int tAT = data.getForNPeople();

        // applied rate
        double aD = appliedRates.get(age);

        // if no N value given in DataKey
        if(tAT == 0) {
            return calcAdjustedRate(tD, consideredTimePeriod);
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

        return calcAdjustedRate(cD, consideredTimePeriod);
    }

    private double calcAdjustedRate(double rate, CompoundTimeUnit timePeriod) {

        double stepsInYear = DateUtils.stepsInYear(timePeriod);
        double adjustedRate = 1 - Math.pow(1 - rate, stepsInYear / 12);

        return adjustedRate;
    }

    @Override
    public void returnAppliedRate(DataKey data, double appliedData, CompoundTimeUnit consideredTimePeriod) {

        IntegerRange age = resolveRowValue(data.getYLabel());

        // old applied rate
        double aDo = appliedRates.get(age);

        // old applied count
        double aCo = appliedCounts.get(age);

        // actually applied correction rate
        double stepsInYear = DateUtils.stepsInYear(consideredTimePeriod);
        double aacD = 1 - Math.pow(1 - appliedData, stepsInYear / 12);

        // to apply to
        int tAT = data.getForNPeople();

        // new applied count
        double aCn = aCo + tAT;

        // new applied rate
        double aDn = 0;
        if(aCn != 0) {
            aDn = ((aDo * aCo) + (aacD * tAT)) / aCn;
        }

        // target rate
        double tD = targetData.get(age);

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

}
