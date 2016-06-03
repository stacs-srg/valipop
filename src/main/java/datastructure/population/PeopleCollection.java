package datastructure.population;

import model.Person;
import model.IPartnership;
import model.IPerson;
import model.IPopulation;
import utils.time.Date;
import utils.time.DateClock;
import utils.time.UnsupportedDateConversion;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection extends PersonCollection implements IPopulation {

    private MaleCollection males;
    private FemaleCollection females;

    public PeopleCollection(Date start, Date end) throws UnsupportedDateConversion {
        males = new MaleCollection(start, end);
        females = new FemaleCollection(start, end);
    }

    public MaleCollection getMales() {
        return males;
    }

    public FemaleCollection getFemales() {
        return females;
    }

    @Override
    public Collection<Person> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfPersons(females, males);
    }

    @Override
    public Collection<Person> getByYear(Date year) {
        Collection<Person> people = males.getByYear(year);
        people.addAll(females.getByYear(year));
        return people;
    }

    @Override
    public void addPerson(Person person) {
        if (person.getSex() == 'm') {
            males.addPerson(person);
        } else {
            females.addPerson(person);
        }
    }

    @Override
    public boolean removePerson(Person person) throws PersonNotFoundException {
        if (person.getSex() == 'm') {
            return males.removePerson(person);
        } else {
            return females.removePerson(person);
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
