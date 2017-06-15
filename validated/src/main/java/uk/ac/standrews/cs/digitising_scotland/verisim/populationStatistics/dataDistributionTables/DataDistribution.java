package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;


/**
 * The DataDistribution interface provides the provision of the general information required of all input statistics in
 * the program. A distribution contains labels which correspond to a value.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface DataDistribution {

    /**
     * @return the year to which the distribution pertains
     */
    YearDate getYear();

    /**
     * @return the 'real world' population which this distribution of statistical data has been drawn
     */
    String getSourcePopulation();

    /**
     * @return the organisation that produced/release the data to make this distribution
     */
    String getSourceOrganisation();

    /**
     * @return the smallest label value in the distribution
     */
    int getSmallestLabel();

    /**
     * @return the largest label value in the distribution
     */
    IntegerRange getLargestLabel();
}
