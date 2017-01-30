package populationStatistics.dataDistributionTables.selfCorrecting;

import utils.specialTypes.DataKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SelfCorrection {

    double getCorrectingData(DataKey data);

    void returnAppliedData(DataKey data, double appliedData);

}
