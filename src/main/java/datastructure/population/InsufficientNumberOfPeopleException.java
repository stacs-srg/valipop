package datastructure.population;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InsufficientNumberOfPeopleException extends Throwable {

    private String message;

    public String getMessage() {
        return message;
    }

    public InsufficientNumberOfPeopleException(String message) {
        this.message = message;
    }
}
