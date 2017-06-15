package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

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
