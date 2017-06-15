package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathStatsKey extends StatsKey {

    private final char sex;

    public DeathStatsKey(Integer age, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate, char sex) {
        super(age, forNPeople, consideredTimePeriod, currentDate);
        this.sex = sex;
    }

    public Integer getAge() {
        return getYLabel();
    }

    public char getSex() {
        return sex;
    }
}
