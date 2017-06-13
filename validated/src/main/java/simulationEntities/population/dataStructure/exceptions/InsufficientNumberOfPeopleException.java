package simulationEntities.population.dataStructure.exceptions;

import simulationEntities.person.IPerson;

import java.util.Collection;

/**
 * The {@link InsufficientNumberOfPeopleException} is thrown when there is not enough people to meet a request made of
 * a PersonCollection data structure.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InsufficientNumberOfPeopleException extends Exception {

    private final String message;

    /**
     * @param message the message
     */
    public InsufficientNumberOfPeopleException(String message) {
        this.message = message;
    }


    public InsufficientNumberOfPeopleException(String message, Collection<IPerson> chosen) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
