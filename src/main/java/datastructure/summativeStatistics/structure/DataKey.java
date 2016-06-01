package datastructure.summativeStatistics.structure;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DataKey {

    private final Integer rowValue;
    private final Integer columnValue;
    private final Integer maxColumnValue;
    private final int forNPeople;

    public DataKey(Integer rowValue, Integer columnValue, Integer maxColumnValue, int forNPeople) {
        this.rowValue = rowValue;
        this.columnValue = columnValue;
        this.maxColumnValue = maxColumnValue;
        this.forNPeople = forNPeople;
    }

    public int getForNPeople() {
        return forNPeople;
    }

    public Integer getColumnValue() {
        return columnValue;
    }

    public Integer getMaxColumnValue() {
        return maxColumnValue;
    }

    public Integer getRowValue() {
        return rowValue;
    }

}
