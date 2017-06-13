package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MultipleBirthStatsKey extends StatsKey {

    public MultipleBirthStatsKey(Integer age, int forNumberOfChildren, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        super(age, forNumberOfChildren, consideredTimePeriod, currentDate);
    }

    public Integer getAge() {
        return getYLabel();
    }

    public int getForNumberOfChildren() {
        return getForNPeople();
    }

}
