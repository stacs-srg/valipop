package datastructure;


import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import model.interfaces.populationModel.IPopulation;
import model.interfaces.populationModel.Population;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection implements PersonCollection, IPopulation {

    private MaleCollection males = new MaleCollection();
    private FemaleCollection females = new FemaleCollection();

    public MaleCollection getMales() {
        return males;
    }

    public FemaleCollection getFemales() {
        return females;
    }

    public Collection<IPerson> getAll() {
        return AggregatePersonCollectionFactory.makeCollectionOfIPersons(females, males);
    }

    public void addPerson(IPerson person) {
        if (person.getSex() == 'm') {
            males.addPerson(person);
        } else {
            females.addPerson(person);
        }
    }

    public void updatePerson(IPerson person, int numberOfChildrenInMostRecentMaternity) {
        if (person.getSex() == 'f') {
            females.updatePerson(person, numberOfChildrenInMostRecentMaternity);
        }
    }

    public boolean removePerson(IPerson person) {
        if (person.getSex() == 'm') {
            return males.removePerson(person);
        } else {
            return females.removePerson(person);
        }
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
        return 0;
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
