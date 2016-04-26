package model.implementation.populationStatistics;

import model.time.TimeClock;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistibution {

    public TimeClock getYear();
    public String getSourcePopulation();
    public String getSourceOrganisation();

}
