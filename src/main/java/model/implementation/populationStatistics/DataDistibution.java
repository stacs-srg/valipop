package model.implementation.populationStatistics;

import model.time.TimeInstant;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistibution {

    public TimeInstant getYear();
    public String getSourcePopulation();
    public String getSourceOrganisation();

}
