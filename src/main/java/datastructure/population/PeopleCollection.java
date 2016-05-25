package datastructure.population;


import model.Person;
import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import model.interfaces.populationModel.IPopulation;
import model.time.DateClock;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection implements PersonCollection, IPopulation {

    private MaleCollection males;
    private FemaleCollection females;

    public PeopleCollection(DateClock start, DateClock end) {
        males = new MaleCollection(start, end);
        females = new FemaleCollection(start, end);
    }

    public MaleCollection getMales() {
        return males;
    }

    public FemaleCollection getFemales() {
        return females;
    }

    public Collection<Person> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfIPersons(females, males);
    }

    public void addPerson(Person person) {
        if (person.getSex() == 'm') {
            males.addPerson(person);
        } else {
            females.addPerson(person);
        }
    }

    public void updatePerson(Person person, int numberOfChildrenInMostRecentMaternity) {
        if (person.getSex() == 'f') {
            females.updatePerson(person, numberOfChildrenInMostRecentMaternity);
        }
    }

    public boolean removePerson(Person person) {
        if (person.getSex() == 'm') {
            return males.removePerson(person);
        } else {
            return females.removePerson(person);
        }
    }

    public void addPeople(Collection<Person> people) {
        for(Person p : people) {
            addPerson(p);
        }
    }


    @Override
    public int getNumberOfPersons() {
        return males.getNumberOfPersons() + females.getNumberOfPersons();
    }


    // TODO - write these!
    @Override
    public Iterable<IPerson> getPeople() {
        return null;
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return null;
    }

    @Override
    public IPerson findPerson(int id) {
        return null;
    }

    @Override
    public IPartnership findPartnership(int id) {
        return null;
    }

    @Override
    public int getNumberOfPeople() throws Exception {
        return males.getNumberOfPersons() + females.getNumberOfPersons();
    }


    @Override
    public int getNumberOfPartnerships() throws Exception {
        return 0;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public void setConsistentAcrossIterations(boolean consistent_across_iterations) {

    }
}
