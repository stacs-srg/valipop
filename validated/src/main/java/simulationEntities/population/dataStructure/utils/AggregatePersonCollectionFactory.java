package simulationEntities.population.dataStructure.utils;

import dateModel.dateImplementations.AdvancableDate;
import simulationEntities.partnership.IPartnership;
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
     */
    public static PeopleCollection makePeopleCollection(PeopleCollection col1, PeopleCollection col2) {

        AdvancableDate start = DateUtils.getEarliestDate(col1.getStartDate(), col2.getStartDate());
        Date end = DateUtils.getLatestDate(col1.getStartDate(), col2.getStartDate());

        PeopleCollection cloneCol1 = col1.clone();
        PeopleCollection cloneCol2 = col2.clone();

        cloneCol1.setStartDate(start);
        cloneCol1.setEndDate(end);

        for(IPerson p : cloneCol2.getPeople()) {
            cloneCol1.addPerson(p);
        }

        for(IPartnership p : cloneCol2.getPartnerships()) {
            cloneCol1.addPartnershipToIndex(p);
        }

        return cloneCol1;
    }


}
