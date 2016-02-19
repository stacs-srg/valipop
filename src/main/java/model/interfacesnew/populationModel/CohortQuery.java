package model.interfacesnew.populationModel;

import model.enums.VariableType;

/**
 * The CohortQuery allows for a subset of the population to be accessed. This subset can then be worked upon directly to
 * enforce the correct population composition.
 *
 * @param <P> The type of sub restriction on the cohort
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface CohortQuery<P> {

    /**
     * Gets the year of the cohort.
     *
     * @return the year
     */
    int getYear();

    /**
     * Gets the variable controling the subsetting of this cohort.
     *
     * @return the variable
     */
    VariableType getVariable();

    /**
     * Gets any further restrictions on the cohort.
     *
     * @return the restrictions
     */
    P getRestrictions();

}
