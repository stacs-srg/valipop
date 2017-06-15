package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface SelfCorrection1D {

    double getCorrectingRate(StatsKey data, CompoundTimeUnit consideredTimePeriod);

    void returnAppliedRate(StatsKey data, double appliedData, CompoundTimeUnit consideredTimePeriod);

}
