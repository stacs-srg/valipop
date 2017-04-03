package datastructure.population.exceptions;

/**
 * The {@link PersonNotFoundException} is thrown when a the requested person cannot be found in the given data structure.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonNotFoundException extends Throwable {

    private final String message;

    /**
     * @param message the message
     */
    public PersonNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
