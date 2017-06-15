package uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InvalidInputFileException extends Exception {

    public InvalidInputFileException(String message, Exception cause) {
        super(message, cause);
    }

    public InvalidInputFileException(String message) {
        super(message);
    }


}
