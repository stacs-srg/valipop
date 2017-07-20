package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTCell {

    private String variable;
    private String value;

    public CTCell(String variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
