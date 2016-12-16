package model.simulationLogic.stochastic;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValueOutOfRateBounds extends Throwable {
    public ValueOutOfRateBounds(String message) {
        super(message);
    }
}
