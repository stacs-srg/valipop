package datastructure;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonCollection {

    Collection<IPerson> getAll();

    public void addPerson(IPerson person);

    public boolean removePerson(IPerson person);

    public void updatePerson(IPerson person, int numberOfChildrenInMostRecentMaternity);

}
