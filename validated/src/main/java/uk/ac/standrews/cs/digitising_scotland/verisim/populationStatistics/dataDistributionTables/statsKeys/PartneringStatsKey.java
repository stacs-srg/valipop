package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

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
