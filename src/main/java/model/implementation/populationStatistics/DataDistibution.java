package model.implementation.populationStatistics;

import model.time.DateClock;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistibution {

    public DateClock getYear();

    public String getSourcePopulation();

    public String getSourceOrganisation();

}
