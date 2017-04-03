package populationStatistics.validation.exceptions;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StatisticalManipulationCalculationError extends Error {

    String message;

    public StatisticalManipulationCalculationError(String message) {
        this.message = message;
    }
}
