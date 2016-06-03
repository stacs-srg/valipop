package datastructure.population;

import model.Person;
import utils.time.Date;
import utils.time.DateUtils;
import utils.time.UnsupportedDateConversion;

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

    public static PeopleCollection makePeopleCollection(PeopleCollection col1, PeopleCollection col2) throws UnsupportedDateConversion {

        Date start = DateUtils.getEarlistDate(col1.getStartDate(), col2.getStartDate());
        Date end = DateUtils.getLatestDate(col1.getStartDate(), col2.getStartDate());

        PeopleCollection people = new PeopleCollection(start, end);

        col1.getAll().forEach(people::addPerson);
        col2.getAll().forEach(people::addPerson);

        return people;
    }


}
