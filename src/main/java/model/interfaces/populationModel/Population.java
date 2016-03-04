package model.interfaces.populationModel;

import model.enums.Gender;

/**
 * This interface defines the additional variables needed for this version of the population model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Population extends IPopulation {


    /**
     * Returns a subset of the population as specified in the given cohort query.
     *
     * @return the set of persons in the population meeting the given query
     */
    IPerson[] getPersons(Gender gender, int minAge, int maxAge);

}
