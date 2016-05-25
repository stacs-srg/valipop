package datastructure.summativeStatistics;

import datastructure.summativeStatistics.structure.IntegerRange;
import model.time.YearDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistribution {

    public YearDate getYear();

    public String getSourcePopulation();

    public String getSourceOrganisation();

    int getMinRowLabelValue();

    IntegerRange getMaxRowLabelValue();
}
