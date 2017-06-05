package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationStatsKey extends StatsKey {

    public SeparationStatsKey(Integer numberOfChildren, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        super(numberOfChildren, forNPeople, consideredTimePeriod, currentDate);
    }

    public Integer getNumberOfChildren() {
        return getYLabel();
    }
}
