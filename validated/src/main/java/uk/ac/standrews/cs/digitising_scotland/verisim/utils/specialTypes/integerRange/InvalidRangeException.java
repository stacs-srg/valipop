package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InvalidRangeException extends IllegalArgumentException {

    String message;

    public InvalidRangeException(String message) {
        this.message = message;
    }

}
