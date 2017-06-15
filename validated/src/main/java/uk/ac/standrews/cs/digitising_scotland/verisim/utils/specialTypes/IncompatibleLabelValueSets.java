package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IncompatibleLabelValueSets extends RuntimeException {

    private String message;
    private LabeledValueSet setA;
    private LabeledValueSet setB;

    public IncompatibleLabelValueSets(String message, LabeledValueSet setA, LabeledValueSet setB) {
        this.message = message;
        this.setA = setA;
        this.setB = setB;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public LabeledValueSet getSetA() {
        return setA;
    }

    public LabeledValueSet getSetB() {
        return setB;
    }
}
