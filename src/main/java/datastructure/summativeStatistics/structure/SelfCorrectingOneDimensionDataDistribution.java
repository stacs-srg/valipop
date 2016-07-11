package datastructure.summativeStatistics.structure;

import utils.MapUtils;
import utils.time.YearDate;

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
        }

    }

    @Override
    public double getCorrectingData(DataKey data) {

        IntegerRange age = resolveRowValue(data.getRowValue());

        // target rate
        double tD = targetData.get(age);

        // applied count
        double aC = appliedCounts.get(age);

        // to apply to
        int tAT = data.getForNPeople();

        // applied rate
        double aD = appliedRates.get(age);

        // if no correction data - i.e. first call to this method
        if(aC == 0) {
            return tD;
        }

        // if no N value given in DataKey
        if(tAT == 0) {
            return tD;
        }

        // Correction rate
        double cD = ( tD * ( aC + tAT ) - ( aD * aC ) ) / tAT;

        return cD;
    }

    @Override
    public void returnAppliedData(DataKey data, double appliedData) {

        IntegerRange age = resolveRowValue(data.getRowValue());

        // old applied rate
        double aDo = appliedRates.get(age);

        // old applied count
        double aCo = appliedCounts.get(age);

        // actually applied correction rate
        double aacD = appliedData;

        // to apply to
        int tAT = data.getForNPeople();

        // new applied count
        double aCn = aCo + tAT;

        // new applied rate
        double aDn = ( ( aDo * aCo ) + ( aacD * tAT ) ) / aCn;

        appliedRates.replace(age, aDn);
        appliedCounts.replace(age, aCn);


    }

}
