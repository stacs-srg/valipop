package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.distributions.StringWithCumulativeProbability;
import uk.ac.standrews.cs.valipop.statistics.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValiPopEnumeratedDistribution extends EnumeratedDistribution implements InputMetaData {

    private final YearDate year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    public ValiPopEnumeratedDistribution(YearDate year, String sourcePopulation, String sourceOrganisation,
                                         Map<String, Double> item_probabilities, RandomGenerator random)
                                                throws InconsistentWeightException {
        super(item_probabilities, random);
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;

    }

    @Override
    public YearDate getYear() {
        return year;
    }

    @Override
    public String getSourcePopulation() {
        return sourcePopulation;
    }

    @Override
    public String getSourceOrganisation() {
        return sourceOrganisation;
    }

    @Override
    public String getSmallestLabel() {
        return items[0].getItem();
    }

    @Override
    public String getLargestLabel() {
        return items[items.length - 1].getItem();
    }

    @Override
    public Collection<String> getLabels() {

        Collection<String> col = new ArrayList<>(items.length);

        for(StringWithCumulativeProbability i : items) {
            col.add(i.getItem());
        }

        return col;
    }

}
