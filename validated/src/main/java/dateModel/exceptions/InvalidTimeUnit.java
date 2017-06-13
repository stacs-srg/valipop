package dateModel.exceptions;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InvalidTimeUnit extends IllegalArgumentException {

    String message;

    public InvalidTimeUnit(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
