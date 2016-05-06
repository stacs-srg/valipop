package datastructure;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonCollection {

    Collection<IPerson> getAll();

    public void addPerson(IPerson person);

}
