package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathStatsKey extends StatsKey {

    public DeathStatsKey(Integer age, int forNPeople, CompoundTimeUnit consideredTimePeriod) {
        super(age, forNPeople, consideredTimePeriod);
    }

    public Integer getAge() {
        return getYLabel();
    }
}
