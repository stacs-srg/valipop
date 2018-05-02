package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.basic_model.distributions.StringWithCumulativeProbability;
import uk.ac.standrews.cs.basic_model.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeDependantEnumeratedDistribution implements InputMetaData {

    private final YearDate year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    private final Map<IntegerRange, EnumeratedDistribution> distributionsByAge = new HashMap<>();

    public AgeDependantEnumeratedDistribution(YearDate year, String sourcePopulation, String sourceOrganisation,
                                              Map<IntegerRange, LabelledValueSet<String, BigDecimal>> item_probabilities, RandomGenerator random) throws InconsistentWeightException {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;

        for(IntegerRange iR : item_probabilities.keySet()) {
            distributionsByAge.put(iR, new EnumeratedDistribution(item_probabilities.get(iR).getMap(), random));
        }

    }

    public EnumeratedDistribution getDistributionForAge(Integer age) {

        for(IntegerRange iR : distributionsByAge.keySet()) {
            if(iR.contains(age)) {
                return distributionsByAge.get(iR);
            }
        }

        throw new InvalidParameterException();
    }

    public String getDeathCauseByAge(Integer age) {
        return getDistributionForAge(age).getSample();
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
        return "";
    }

    @Override
    public String getLargestLabel() {
        return "";
    }

    @Override
    public Collection<String> getLabels() {

        return null;
    }

}
