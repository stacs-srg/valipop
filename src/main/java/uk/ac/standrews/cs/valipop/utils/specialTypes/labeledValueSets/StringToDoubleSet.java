package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.utils.CollectionUtils;
import uk.ac.standrews.cs.valipop.utils.DoubleComparer;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToDoubleSet extends AbstractLabelToAbstractValueSet<String, Double> implements OperableLabelledValueSet<String, Double> {

    private static double DELTA = 1E-2;

    public StringToDoubleSet(List<String> labels, List<Double> values, RandomGenerator random) {
        super(labels, values, random);
    }

    public StringToDoubleSet(Set<String> labels, Double initValue, RandomGenerator random) {
        super(labels, initValue, random);
    }

    public StringToDoubleSet(RandomGenerator random) {
        super(random);
    }

    public StringToDoubleSet(LabelledValueSet<String, Double> set, RandomGenerator random) {
        super(set.getMap(), random);
    }

    @Override
    public Class getValueClass() {
        return Double.class;
    }

    @Override
    public LabelledValueSet<String, Double> constructSelf(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values, random);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquivalent(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values, random);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquivalent(List<String> labels, List<Double> values) {
        return constructSelf(labels, values);
    }

    @Override
    public Double zero() {
        return 0.0;
    }

    @Override
    public Double sum(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double multiply(Double a, int n) {
        return a * n;
    }

    @Override
    public OperableLabelledValueSet<String, Double> productOfLabelsAndValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<String, Double> divisionOfValuesByLabels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperableLabelledValueSet<String, Integer> controlledRoundingMaintainingSum() {
        double sum = getSumOfValues();
        double sumRounded = Math.round(sum);

        if (!DoubleComparer.equal(sum, sumRounded, DELTA)) {
            throw new ValuesDoNotSumToWholeNumberException("Cannot perform controlled rounding and maintain sum as values do not sum to a whole number");
        }

        int sumInt = (int) sumRounded;

        OperableLabelledValueSet<String, Integer> roundingSet = new StringToIntegerSet(random);

        for (String s : getLabels()) {
            if (getValue(s) < 0) {
                roundingSet.add(s, 0);
            } else {
                roundingSet.add(s, (int) Math.floor(getValue(s)));
            }
        }

        Set<String> usedLabels = new TreeSet<>();

        int roundingSetSum;
        while ((roundingSetSum = roundingSet.getSumOfValues()) != sumInt) {

            if (roundingSetSum < sumInt) {
                // need more in the rounding set therefore
                String labelOfGreatestRemainder = this.getLabelOfValueWithGreatestRemainder(usedLabels);
                roundingSet.update(labelOfGreatestRemainder, roundingSet.getValue(labelOfGreatestRemainder) + 1);
            }

            if (roundingSetSum > sumInt) {
                String largestReducatbleLabel;
                try {
                    largestReducatbleLabel =
                            roundingSet.getRandomLabelOfNonZeroValue();
                } catch (NoSuchElementException e) {
                    largestReducatbleLabel = this.smallestLabel();
                }
                roundingSet.update(largestReducatbleLabel, roundingSet.getValue(largestReducatbleLabel) - 1);
            }
        }

        return roundingSet;
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
