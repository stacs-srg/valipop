package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MarriageStatsKey extends StatsKey {

    public MarriageStatsKey(Integer age, double forNPeople, Period consideredTimePeriod, LocalDate currentDate) {
        super(age, forNPeople, consideredTimePeriod, Year.of(currentDate.getYear()));
    }

    public Integer getAge() {
        return getYLabel();
    }

}
