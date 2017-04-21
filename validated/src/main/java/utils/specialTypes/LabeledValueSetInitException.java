package utils.specialTypes;

import utils.specialTypes.integerRange.IntegerRange;

import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class LabeledValueSetInitException extends RuntimeException {

    private String message;
    private List labels;
    private List values;

    public LabeledValueSetInitException(String message, List labels, List values) {

        this.message = message;
        this.labels = labels;
        this.values = values;

    }

    public List getLabels() {
        return labels;
    }

    public List getValues() {
        return values;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
