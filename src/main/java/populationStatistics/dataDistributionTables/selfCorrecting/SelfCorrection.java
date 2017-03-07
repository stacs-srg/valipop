package populationStatistics.dataDistributionTables.selfCorrecting;

import utils.specialTypes.dataKeys.DataKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SelfCorrection {

    double getCorrectingRate(DataKey data);

    void returnAppliedRate(DataKey data, double appliedData);

}
