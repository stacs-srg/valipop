package utils.specialTypes.dataKeys;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DataKey {

    private final Integer yLabel;
    private final Integer xLabel;
    private final Integer maxXLabel;
    private final int forNPeople;

    public DataKey(Integer yLabel, Integer xLabel, Integer maxXLabel, int forNPeople) {
        this.yLabel = yLabel;
        this.xLabel = xLabel;
        this.maxXLabel = maxXLabel;
        this.forNPeople = forNPeople;
    }

    public DataKey(Integer yLabel, Integer xLabel, int forNPeople) {
        this(yLabel, xLabel, null, forNPeople);
    }

    public DataKey(Integer yLabel, int forNPeople) {
        this(yLabel, null, null, forNPeople);
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

}
