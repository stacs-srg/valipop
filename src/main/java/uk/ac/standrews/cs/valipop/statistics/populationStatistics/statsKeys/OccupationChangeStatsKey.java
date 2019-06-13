package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OccupationChangeStatsKey extends StatsKey<String, String>  {

    private final SexOption sex;

    public OccupationChangeStatsKey(String occupationA, double forNPeople, Period consideredTimePeriod, LocalDate currentDate, SexOption sex) {
        super(occupationA, forNPeople, consideredTimePeriod, Year.of(currentDate.getYear()));
        this.sex = sex;
    }

    public String getOccupationA() {
        return getYLabel();
    }

    public String getOccupationB() {
        return getXLabel();
    }

    public SexOption getSex() {
        return sex;
    }

}
