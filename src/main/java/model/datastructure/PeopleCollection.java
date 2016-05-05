package model.datastructure;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection implements PersonCollection {

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

}
