package datastructure.population.exceptions;

import model.simulationEntities.IPerson;

import java.util.Collection;

/**
 * The {@link InsufficientNumberOfPeopleException} is thrown when there is not enough people to meet a request made of
 * a PersonCollection data structure.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InsufficientNumberOfPeopleException extends Exception {

    private final String message;
    private Collection<IPerson> availiablePeople = null;

    /**
     * @param message the message
     */
    public InsufficientNumberOfPeopleException(String message, Collection<IPerson> availaiblePeople) {
        this.message = message;
        this.availiablePeople = availaiblePeople;
    }

    public InsufficientNumberOfPeopleException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Collection<IPerson> getAvailaiblePeople() {
        return availiablePeople;
    }
}
