package events;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class UnsupportedEventType extends Throwable {

    private String message;

    public UnsupportedEventType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
