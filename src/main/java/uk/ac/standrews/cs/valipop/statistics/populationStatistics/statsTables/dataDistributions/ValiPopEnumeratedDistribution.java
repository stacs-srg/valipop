package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions;

import uk.ac.standrews.cs.basic_model.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.StatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValiPopEnumeratedDistribution extends EnumeratedDistribution implements DataDistribution<Double, Double> {

    private final YearDate year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    public ValiPopEnumeratedDistribution(YearDate year, String sourcePopulation, String sourceOrganisation,
                                         Map<String, Double> item_probabilities, Random random)
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
    public int getSmallestLabel() {
        return 0;
    }

    @Override
    public IntegerRange getLargestLabel() {
        return null;
    }

    @Override
    public Collection<IntegerRange> getLabels() {
        return null;
    }

    @Override
    public DeterminedCount determineCount(StatsKey key, Config config) {
        return null;
    }

    @Override
    public void returnAchievedCount(DeterminedCount<Double, Double> achievedCount) {

    }
}
