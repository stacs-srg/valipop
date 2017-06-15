package uk.ac.standrews.cs.digitising_scotland.verisim.events.birth;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NoChildrenOfDesiredOrder extends Throwable {

    private String message;

    public NoChildrenOfDesiredOrder(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
