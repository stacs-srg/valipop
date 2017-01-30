package simulationEntities.population.dataStructure;

import simulationEntities.IPartnership;
import simulationEntities.IPerson;
import simulationEntities.population.IPopulation;
import dateModel.Date;
import dateModel.exceptions.UnsupportedDateConversion;
import simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The class PeopleCollection is a concrete instance of the PersonCollection class. It provides the layout to structure
 * and index a population of males and females and provide access to them. The class also implements the IPopulation
 * interface (adapted to us object references rather than integer id references) allowing it to be used with our other
 * population suite tools.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection extends PersonCollection implements IPopulation {

    private String description = "";

    private MaleCollection males;
    private FemaleCollection females;

    private final Map<Integer, IPerson> peopleIndex = new HashMap<>();
    private final Map<Integer, IPartnership> partnershipIndex = new HashMap<>();

    private ArrayList<IPartnership> partTemp = new ArrayList<>();

    /**
     * Instantiates a new PersonCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the PersonCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the PersonCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public PeopleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        males = new MaleCollection(start, end);
        females = new FemaleCollection(start, end);
    }

    /**
     * @return the part of the population data structure containing the males
     */
    public MaleCollection getMales() {
        return males;
    }

    /**
     * @return the part of the population data structure containing the females
     */
    public FemaleCollection getFemales() {
        return females;
    }

    /**
     * Adds partnership to  the partnership index.
     *
     * @param partnership the partnership
     */
    public void addPartnershipToIndex(IPartnership partnership) {
        partnershipIndex.put(partnership.getId(), partnership);
        partTemp.add(partnership);
    }

    public void removePartnershipFromIndex(IPartnership partnership) {
        partnershipIndex.remove(partnership.getId(), partnership);
        partTemp.remove(partnership);
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfPersons(females, males);
    }

    @Override
    public Collection<IPerson> getByYear(Date yearOfBirth) {
        Collection<IPerson> people = males.getByYear(yearOfBirth);
        people.addAll(females.getByYear(yearOfBirth));
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
    public void removePerson(IPerson person) throws PersonNotFoundException {
        peopleIndex.remove(person.getId());
        if (person.getSex() == 'm') {
            males.removePerson(person);
        } else {
            females.removePerson(person);
        }
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    @Override
    public int getNumberOfPersons(Date yearOfBirth) {
        return males.getNumberOfPersons(yearOfBirth) + females.getNumberOfPersons(yearOfBirth);
    }

    /*
    -------------------- IPopulation interface methods --------------------
     */

    @Override
    public Iterable<IPerson> getPeople() {
        return peopleIndex.values();
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        return partTemp;
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
