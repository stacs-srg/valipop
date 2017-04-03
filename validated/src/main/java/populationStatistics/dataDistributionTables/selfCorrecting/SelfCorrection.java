package populationStatistics.dataDistributionTables.selfCorrecting;

import dateModel.timeSteps.CompoundTimeUnit;
import utils.specialTypes.dataKeys.DataKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SelfCorrection {

    double getCorrectingRate(DataKey data, CompoundTimeUnit consideredTimePeriod);

    void returnAppliedRate(DataKey data, double appliedData, CompoundTimeUnit consideredTimePeriod);

}
