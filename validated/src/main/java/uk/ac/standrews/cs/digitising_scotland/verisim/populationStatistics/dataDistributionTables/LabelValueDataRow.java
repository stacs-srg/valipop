package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class LabelValueDataRow {

    private int label;
    private double value;
    private String group;

    public LabelValueDataRow(int label, double value, String group) {
        this.label = label;
        this.value = value;
        this.group = group;
    }

    public String rowAsString() {
        return Integer.toString(label) + " " + Double.toString(value) + " " + group;
    }

}
