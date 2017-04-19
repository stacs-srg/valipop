package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthStatsKey extends StatsKey {


    public BirthStatsKey(Integer age, Integer order, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        super(order, age, forNPeople, consideredTimePeriod, currentDate);
    }

    public Integer getAge() {
        return getXLabel();
    }

    public Integer getOrder() {
        return getYLabel();
    }
}
