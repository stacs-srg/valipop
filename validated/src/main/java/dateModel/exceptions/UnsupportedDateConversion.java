package dateModel.exceptions;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class UnsupportedDateConversion extends Throwable {

    private String message;

    public UnsupportedDateConversion(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
