package datastructure.population;

import model.Person;

import java.util.Collection;

/**
 * Aggregates two PersonCollections into a single Collection of Person objects
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AggregatePersonCollectionFactory {

    public static Collection<Person> makeCollectionOfPersons(PersonCollection col1, PersonCollection col2) {

        Collection<Person> people = col1.getAll();
        people.addAll(col2.getAll());

        return people;
    }


}
