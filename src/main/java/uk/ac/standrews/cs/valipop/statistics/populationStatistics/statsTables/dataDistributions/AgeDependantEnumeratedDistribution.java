package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.statistics.distributions.EnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;

import java.security.InvalidParameterException;
import java.time.Year;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeDependantEnumeratedDistribution implements InputMetaData {

    private final Year year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    private final Map<IntegerRange, EnumeratedDistribution> distributionsByAge = new TreeMap<>();

    public AgeDependantEnumeratedDistribution(Year year, String sourcePopulation, String sourceOrganisation,
                                              Map<IntegerRange, LabelledValueSet<String, Double>> item_probabilities, RandomGenerator random) throws InconsistentWeightException {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;

        for (IntegerRange iR : item_probabilities.keySet()) {
            distributionsByAge.put(iR, new EnumeratedDistribution(item_probabilities.get(iR).getMap(), random));
        }
    }

    public EnumeratedDistribution getDistributionForAge(Integer age) {

        for (IntegerRange iR : distributionsByAge.keySet()) {
            if (iR.contains(age)) {
                return distributionsByAge.get(iR);
            }
        }

        throw new InvalidParameterException();
    }

    @Override
    public Year getYear() {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLargestLabel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getLabels() {
        throw new UnsupportedOperationException();
    }
}
