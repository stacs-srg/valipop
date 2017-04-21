package utils.specialTypes;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValuesDoNotSumToWholeNumberException extends RuntimeException {

    private String message;
    private LabeledValueSet labeledValueSet;

    public ValuesDoNotSumToWholeNumberException(String message, LabeledValueSet labeledValueSet) {
        this.message = message;
        this.labeledValueSet = labeledValueSet;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public LabeledValueSet getLabeledValueSet() {
        return labeledValueSet;
    }

}
