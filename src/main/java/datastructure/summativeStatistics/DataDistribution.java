package datastructure.summativeStatistics;

import datastructure.summativeStatistics.structure.IntegerRange;
import utils.time.YearDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistribution {

    YearDate getYear();

    String getSourcePopulation();

    String getSourceOrganisation();

    int getMinRowLabelValue();

    IntegerRange getMaxRowLabelValue();
}
