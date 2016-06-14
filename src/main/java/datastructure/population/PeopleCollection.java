package datastructure.population;

import model.IPartnership;
import model.IPerson;
import model.IPopulation;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection extends PersonCollection implements IPopulation {

    private String description = "";

    private MaleCollection males;
    private FemaleCollection females;

    private Map<Integer, IPerson> peopleIndex = new HashMap<Integer, IPerson>();
    private Map<Integer, IPartnership> partnershipIndex = new HashMap<Integer, IPartnership>();

    public PeopleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        males = new MaleCollection(start, end);
        females = new FemaleCollection(start, end);
    }

    public MaleCollection getMales() {
        return males;
    }

    public FemaleCollection getFemales() {
        return females;
    }

    public void addPartnershipToIndex(IPartnership partnership) {
        partnershipIndex.put(partnership.getId(), partnership);
    }

    @Override
    public Collection<IPerson> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfPersons(females, males);
    }

    @Override
    public Collection<IPerson> getByYear(Date year) {
        Collection<IPerson> people = males.getByYear(year);
        people.addAll(females.getByYear(year));
        return people;
    }

    @Override
    public Collection<IPerson> getByYearAndSex(char sex, Date year) {
        if (Character.toLowerCase(sex) == 'm') {
            return getMales().getByYear(year);
        } else {
            return getFemales().getByYear(year);
        }
    }

    @Override
    public void addPerson(IPerson person) {
        peopleIndex.put(person.getId(), person);
        if (person.getSex() == 'm') {
            males.addPerson(person);
        } else {
            females.addPerson(person);
        }
    }

    @Override
    public boolean removePerson(IPerson person) throws PersonNotFoundException {
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
        return peopleIndex.values();
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {
        return partnershipIndex.values();
    }

    @Override
    public IPerson findPerson(int id) {
        return peopleIndex.get(id);
    }

    @Override
    public IPartnership findPartnership(int id) {
        return partnershipIndex.get(id);
    }

    @Override
    public int getNumberOfPeople() throws Exception {
        return peopleIndex.size();
    }


    @Override
    public int getNumberOfPartnerships() throws Exception {
        return partnershipIndex.size();
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setConsistentAcrossIterations(boolean consistent_across_iterations) {

    }

}
