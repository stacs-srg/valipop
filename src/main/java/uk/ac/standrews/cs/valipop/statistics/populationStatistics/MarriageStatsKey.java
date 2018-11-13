package uk.ac.standrews.cs.valipop.statistics.populationStatistics;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MarriageStatsKey extends StatsKey {

    public MarriageStatsKey(Integer age, double forNPeople, CompoundTimeUnit consideredTimePeriod, ValipopDate currentDate) {
        super(age, forNPeople, consideredTimePeriod, currentDate);
    }

    public Integer getAge() {
        return getYLabel();
    }

}
