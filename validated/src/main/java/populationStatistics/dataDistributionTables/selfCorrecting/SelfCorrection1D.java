package populationStatistics.dataDistributionTables.selfCorrecting;

import dateModel.timeSteps.CompoundTimeUnit;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SelfCorrection1D {

    double getCorrectingRate(StatsKey data, CompoundTimeUnit consideredTimePeriod);

    void returnAppliedRate(StatsKey data, double appliedData, CompoundTimeUnit consideredTimePeriod);

}
