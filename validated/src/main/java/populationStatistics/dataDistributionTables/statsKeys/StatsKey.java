package populationStatistics.dataDistributionTables.statsKeys;

import dateModel.Date;
import dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StatsKey {

    private final Integer yLabel;
    private final Integer xLabel;
    private final Integer maxXLabel;
    private final int forNPeople;
    private final CompoundTimeUnit consideredTimePeriod;
    private final Date date;

    public StatsKey(Integer yLabel, Integer xLabel, Integer maxXLabel, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this.yLabel = yLabel;
        this.xLabel = xLabel;
        this.maxXLabel = maxXLabel;
        this.forNPeople = forNPeople;
        this.consideredTimePeriod = consideredTimePeriod;
        this.date = date;
    }

    public StatsKey(Integer yLabel, Integer xLabel, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this(yLabel, xLabel, null, forNPeople, consideredTimePeriod, date);
    }

    public StatsKey(Integer yLabel, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this(yLabel, null, null, forNPeople, consideredTimePeriod, date);
    }

    public int getForNPeople() {
        return forNPeople;
    }

    public Integer getXLabel() {
        return xLabel;
    }

    public Integer getMaxXLabel() {
        return maxXLabel;
    }

    public Integer getYLabel() {
        return yLabel;
    }

    public CompoundTimeUnit getConsideredTimePeriod() {
        return consideredTimePeriod;
    }

    public Date getDate() {
        return date;
    }
}
