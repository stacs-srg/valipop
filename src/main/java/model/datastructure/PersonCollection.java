package model.datastructure;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PersonCollection {

    PersonCollection getMales();

    PersonCollection getFemales();

    PersonCollection getAll();

    Collection<IPerson> getCollection();

}
