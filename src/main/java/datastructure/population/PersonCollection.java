package datastructure.population;

import model.Person;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonCollection {

    Collection<Person> getAll();

    void addPerson(Person person);

    boolean removePerson(Person person);

    void updatePerson(Person person, int numberOfChildrenInMostRecentMaternity);

    int getNumberOfPersons();

}
