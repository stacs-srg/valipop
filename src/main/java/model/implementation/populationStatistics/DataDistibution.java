package model.implementation.populationStatistics;

import model.time.YearDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistibution {

    public YearDate getYear();

    public String getSourcePopulation();

    public String getSourceOrganisation();

    int getMinRowLabelValue();

    IntegerRange getMaxRowLabelValue();
}
