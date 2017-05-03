package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartneringStatsKey extends StatsKey {

    public PartneringStatsKey(Integer age, int forNumberOfFemales, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        super(age, forNumberOfFemales, consideredTimePeriod, currentDate);
    }

    public Integer getAge() {
        return getYLabel();
    }

    public int getForNumberOfFemales() {
        return getForNPeople();
    }
}
