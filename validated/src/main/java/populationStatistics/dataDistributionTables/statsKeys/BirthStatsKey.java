package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthStatsKey extends StatsKey {


    public BirthStatsKey(Integer age, Integer order, int forNPeople, CompoundTimeUnit consideredTimePeriod) {
        super(age, order, forNPeople, consideredTimePeriod);
    }

    public Integer getAge() {
        return getYLabel();
    }

    public Integer getOrder() {
        return getXLabel();
    }
}
