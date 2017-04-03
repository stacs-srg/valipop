package model.simulationLogic.stochastic;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValueNotInRangesException extends Throwable {

    public ValueNotInRangesException(String message) {
        super(message);
    }
}
