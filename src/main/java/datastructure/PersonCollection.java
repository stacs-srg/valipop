package datastructure;

import model.Person;
import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonCollection {

    Collection<Person> getAll();

    public void addPerson(Person person);

    public boolean removePerson(Person person);

    public void updatePerson(Person person, int numberOfChildrenInMostRecentMaternity);

}
