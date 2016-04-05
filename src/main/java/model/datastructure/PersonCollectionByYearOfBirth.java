package model.datastructure;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonCollectionByYearOfBirth implements PersonCollection {

    Map<Integer, PersonCollection> byYear;

    @Override
    public PersonCollection getMales() {
        return null;
    }

    @Override
    public PersonCollection getFemales() {
        return null;
    }

    @Override
    public PersonCollection getAll() {
        return null;
    }

    @Override
    public Collection<IPerson> getCollection() {
        return null;
    }


    public PersonCollection getByYear(int year) {

        return null;
    }
}
