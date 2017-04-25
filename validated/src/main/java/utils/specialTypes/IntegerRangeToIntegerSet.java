package utils.specialTypes;

import utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRangeToIntegerSet implements LabeledValueSet<IntegerRange, Integer> {

    private Map<IntegerRange, Integer> map = new HashMap<>();

    public IntegerRangeToIntegerSet(List<IntegerRange> labels, List<Integer> values) {

        if(labels.size() != values.size()) {
            throw new LabeledValueSetInitException("Labels and values lists of differing sizes", labels, values);
        }

        int c = 0;
        for(IntegerRange iR : labels) {
            map.put(iR, values.get(c));
            c++;
        }
    }

    public IntegerRangeToIntegerSet(Set<IntegerRange> labels, Integer integerInitValue) {

        for(IntegerRange iR : labels) {
            map.put(iR, 0);
        }

    }

    public IntegerRangeToIntegerSet() {}

    @Override
    public Map<IntegerRange, Integer> getMap() {
        return map;
    }

    @Override
    public Integer getSumOfValues() {

        Integer sum = 0;

        for(IntegerRange iR : map.keySet()) {
            sum += map.get(iR);
        }

        return sum;

    }

    @Override
    public Integer getValue(IntegerRange label) {
        return map.get(label);
    }

    @Override
    public Set<IntegerRange> getLabels() {
        return map.keySet();
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> productOfLabelsAndValues() {

        List<IntegerRange> labels = new ArrayList<>();
        List<Integer> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(iR.getValue() * getValue(iR));
        }

        return new IntegerRangeToIntegerSet(labels, products);

    }

    @Override
    public void add(IntegerRange label, Integer value) {
        map.put(label, value);
    }

    @Override
    public Integer get(IntegerRange label) {
        return map.get(label);
    }

    @Override
    public void update(IntegerRange label, Integer value) {
        map.replace(label, value);
    }

    @Override
    public Integer remove(IntegerRange label) {
        return map.remove(label);
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> productOfValuesAndN(Integer n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Integer> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) * n);
        }

        return new IntegerRangeToIntegerSet(labels, products);
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSum() {
        return this.clone();
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> floorValues() {
        return clone();
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> clone() {

        List<IntegerRange> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            values.add(getValue(iR));
        }

        return new IntegerRangeToIntegerSet(labels, values);
    }

    @Override
    public IntegerRange getLabelOfValueWithGreatestRemainder(Set<IntegerRange> usedLabels) {
        throw new NoSuchElementException("Integer cannot have remainders when divided by one, therefore the largest " +
                "remainder is by definition an undefinable concept");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public LabeledValueSet<IntegerRange, Double> valuesPlusValues(LabeledValueSet<IntegerRange, ? extends Number> n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> results = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);

            Number sub = n.getValue(iR);
            if(sub == null) {
                throw new IncompatibleLabelValueSets("Sets do not contain same labels - " +
                        "mathematical operations not possible", this, n);
            }

            results.add(getValue(iR) + sub.doubleValue());
        }

        return new IntegerRangeToDoubleSet(labels, results);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public LabeledValueSet<IntegerRange, Double> valuesSubtractValues(LabeledValueSet<IntegerRange, ? extends Number> n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> results = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);

            Number sub = n.getValue(iR);
            if(sub == null) {
                throw new IncompatibleLabelValueSets("Sets do not contain same labels - " +
                        "mathematical operations not possible", this, n);
            }

            results.add(getValue(iR) - sub.doubleValue());
        }

        return new IntegerRangeToDoubleSet(labels, results);
    }
}
