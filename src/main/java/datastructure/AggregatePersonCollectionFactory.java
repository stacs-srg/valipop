package datastructure;

import model.Person;
import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AggregatePersonCollectionFactory {

    public static Collection<Person> makeCollectionOfIPersons(PersonCollection col1, PersonCollection col2) {

        Collection<Person> people = col1.getAll();
        people.addAll(col2.getAll());

        return people;
    }


}
