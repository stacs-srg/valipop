package model.interfacesnew.populationModel;

/**
 * This interface defines the additional variables needed for this version of the population model.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface Population extends IPopulation {


    /**
     * Returns a subset of the population as specified in the given cohort query.
     *
     * @param cohort the details of the required cohort
     * @return the set of persons in the population meeting the given query
     */
    IPerson[] getPersons(CohortQuery cohort);

}
