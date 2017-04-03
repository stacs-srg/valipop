package validation.utils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StatisticalManipulationCalculationError extends Throwable {

    String message;

    public StatisticalManipulationCalculationError(String message) {
        this.message = message;
    }
}
