package model.interfaces.dataStores.informationFlow.query;

import model.enums.VariableType;

/**
 * A TableQuery is used to define the structure of a data table to be retrieved from a data store.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface TableQuery extends Query{

    /**
     * The variable the query focuses on.
     *
     * @return the variable
     */
    VariableType getVariable();

    /**
     * The start year.
     *
     * @return the start year
     */
    int getStartYear();

    /**
     * The increment size.
     *
     * @return the increment size
     */
    int getIncrementSize();

}
