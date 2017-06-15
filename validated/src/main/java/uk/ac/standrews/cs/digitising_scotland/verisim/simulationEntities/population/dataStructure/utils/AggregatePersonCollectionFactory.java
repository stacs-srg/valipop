package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.utils;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PersonCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;

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
