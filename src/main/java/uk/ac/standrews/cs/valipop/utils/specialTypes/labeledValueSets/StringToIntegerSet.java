package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToIntegerSet extends AbstractLabelToAbstractValueSet<String, Integer> implements OperableLabelledValueSet<String, Integer> {

    public StringToIntegerSet(List<String> labels, List<Integer> values, RandomGenerator random) {
        super(labels, values, random);
    }

    public StringToIntegerSet(Set<String> labels, Integer initValue, RandomGenerator random) {
        super(labels, initValue, random);
    }

    public StringToIntegerSet(RandomGenerator random) { super(random);}

    @Override
    public Class getValueClass() {
        return Integer.class;
    }

    @Override
    public LabelledValueSet<String, Integer> constructSelf(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values, random);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquivalent(List<String> labels, List<Integer> values) {
        return constructSelf(labels, values);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquivalent(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values, random);
    }

    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer sum(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer multiply(Integer a, int n) {
        return a * n;
    }

    @Override
    public OperableLabelledValueSet<String, Integer> productOfLabelsAndValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<String, Double> divisionOfValuesByLabels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<String, Integer> controlledRoundingMaintainingSum() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLargestLabelOfNonZeroValueAndLabelLessOrEqualTo(String n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLargestLabelOfNonZeroValueAndLabelPreferablyLessOrEqualTo(String n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLargestLabelOfNonZeroValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRandomLabelOfNonZeroValue() {

        ArrayList<String> keys = new ArrayList<>(map.keySet());

        CollectionUtils.shuffle(keys, random);

        for (String s : keys) {
            if (get(s) != 0)
                return s;
        }

        throw new NoSuchElementException("No non zero values in set - set size: " + getLabels().size());

    }

    @Override
    public String smallestLabel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<String, Double> valuesAddNWhereCorrespondingLabelNegativeInLVS(double n, OperableLabelledValueSet<String, ? extends Number> lvs) {
        throw new UnsupportedOperationException();
    }
}
