package model.simulationEntities;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonNotAliveException extends Throwable {
    public PersonNotAliveException(String message) {
        super(message);
    }
}
