package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys;

import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IllegitimateBirthStatsKey extends StatsKey {

    public IllegitimateBirthStatsKey(Integer age, double forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        super(age, forNPeople, consideredTimePeriod, currentDate);
    }

    public Integer getAge() {
        return getYLabel();
    }

}
