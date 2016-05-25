package datastructure.population;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonNotFoundException extends Throwable {

    private final String message;

    public PersonNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
