package utils.specialTypes;

import utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRangeToDoubleSet implements LabeledValueSet<IntegerRange, Double> {

    private Map<IntegerRange, Double> map = new HashMap<>();

    public IntegerRangeToDoubleSet(List<IntegerRange> labels, List<Double> values) {

        if(labels.size() != values.size()) {
            throw new LabeledValueSetInitException("Labels and values lists of differing sizes", labels, values);
        }

        int c = 0;
        for(IntegerRange iR : labels) {
            map.put(iR, values.get(c));
        }

    }

    public IntegerRangeToDoubleSet(Map<IntegerRange, Double> map) {
        this.map = map;
    }

    @Override
    public Map<IntegerRange, Double> getMap() {
        return map;
    }

    @Override
    public Double getSumOfValues() {

        double sum = 0;

        for(IntegerRange iR : map.keySet()) {
            sum += map.get(iR);
        }

        return sum;
    }

    @Override
    public Double getValue(IntegerRange label) {
        return map.get(label);
    }

    @Override
    public Set<IntegerRange> getLabels() {
        return map.keySet();
    }

    @Override
    public LabeledValueSet<IntegerRange, Double> productOfLabelsAndValues() {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(iR.getValue() * getValue(iR));
        }

        return new IntegerRangeToDoubleSet(labels, products);
    }

    @Override
    public void add(IntegerRange label, Double value) {
        map.put(label, value);
    }

    @Override
    public Double get(IntegerRange label) {
        return map.get(label);
    }

    @Override
    public void update(IntegerRange label, Double value) {
        if(map.replace(label, value) == null) {
            add(label, value);
        }
    }

    @Override
    public Double remove(IntegerRange label) {
        return map.remove(label);
    }

    @Override
    public LabeledValueSet<IntegerRange, Double> productOfValuesAndN(Integer n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) * (double) n);
        }

        return new IntegerRangeToDoubleSet(labels, products);
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

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSum() {

        double sum = getSumOfValues();

        if(sum % 1 != 0) {
            throw new ValuesDoNotSumToWholeNumberException("Cannot perform controlled rounding and maintain sum as " +
                    "values do not sum to a whole number", this);
        }

        int sumInt = (int) sum;

        LabeledValueSet<IntegerRange, Integer> roundingSet = new IntegerRangeToIntegerSet();

        for(IntegerRange iR : getLabels()) {
            roundingSet.add(iR, (int) Math.floor(getValue(iR)));
        }

        Set<IntegerRange> usedLabels = new HashSet<>();

        int roundingSetSum;
        while((roundingSetSum = roundingSet.getSumOfValues()) != sumInt) {

            if(roundingSetSum < sumInt) {
                // need more in the rounding set therefore
                IntegerRange labelOfGreatestRemainder = this.getLabelOfValueWithGreatestRemainder(usedLabels);
                roundingSet.update(labelOfGreatestRemainder, roundingSet.getValue(labelOfGreatestRemainder)+1);
            }

        }

        return roundingSet;
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> floorValues() {
        List<IntegerRange> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            values.add((int) Math.floor(getValue(iR)));
        }

        return new IntegerRangeToIntegerSet(labels, values);
    }

    @Override
    public LabeledValueSet<IntegerRange, Double> clone() {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            values.add(getValue(iR));
        }

        return new IntegerRangeToDoubleSet(labels, values);
    }

    @Override
    public IntegerRange getLabelOfValueWithGreatestRemainder(Set<IntegerRange> usedLabels) {

        double largestRemainder = 0;
        IntegerRange labelOfLargestRemainder = null;

        for(IntegerRange iR : map.keySet()) {

            if(!usedLabels.contains(iR)) {
                double remainder = getValue(iR) % 1;
                if(remainder > largestRemainder) {
                    largestRemainder = remainder;
                    labelOfLargestRemainder = iR;
                }
            }
        }

        if(labelOfLargestRemainder == null) {
            throw new NoSuchElementException("No values identifies matching criteria. Labels minus used labels = "
                    + (getLabels().size() - usedLabels.size()));
        }

        return labelOfLargestRemainder;
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
}
