package simulationEntities.population.dataStructure.utils;

import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.PersonCollection;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.exceptions.UnsupportedDateConversion;

import java.util.Collection;

/**
 * Provides a set of methods to create aggregates of two PersonCollections
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AggregatePersonCollectionFactory {

    /**
     * Aggregates two PersonCollections into a single collection of IPerson.
     *
     * @param col1 The first PersonCollection
     * @param col2 The second PersonCollection
     * @return The aggregated Collection of people
     */
    public static Collection<IPerson> makeCollectionOfPersons(PersonCollection col1, PersonCollection col2) {

        Collection<IPerson> people = col1.getAll();
        people.addAll(col2.getAll());

        return people;
    }

    /**
     * Aggregates two PersonCollections into a single PersonCollection.
     *
     * @param col1 The first PersonCollection
     * @param col2 The second PersonCollection
     * @return The aggregated PersonCollection
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public static PeopleCollection makePeopleCollection(PeopleCollection col1, PeopleCollection col2) throws UnsupportedDateConversion {

        Date start = DateUtils.getEarliestDate(col1.getStartDate(), col2.getStartDate());
        Date end = DateUtils.getLatestDate(col1.getStartDate(), col2.getStartDate());

//        PeopleCollection people = new PeopleCollection(start, end);


        for(IPerson p : col2.getPeople()) {
            col1.addPerson(p);
        }

//        col2.getAll().forEach(col1::addPerson);

        return col1;
    }


}
