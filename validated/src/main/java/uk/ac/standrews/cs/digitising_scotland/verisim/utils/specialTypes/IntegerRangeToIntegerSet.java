/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

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
    public LabeledValueSet<IntegerRange, Double> productOfValuesAndN(Double n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(new Double(getValue(iR)) * n);
        }

        return new IntegerRangeToDoubleSet(labels, products);
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSum() {
        return this.clone();
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumWithProductOfLabelAndValue() {
        return this.clone();
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues() {
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

    @Override
    public LabeledValueSet<IntegerRange, Double> reproportion() {
        throw new UnsupportedOperationException("Cannot re-proportion integer values");
    }

    @Override
    public LabeledValueSet<IntegerRange, Double> divisionOfValuesByN(double n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) / n);
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
    public LabeledValueSet<IntegerRange, Double> divisionOfValuesByLabels() {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) / (double) iR.getValue());
        }

        return new IntegerRangeToDoubleSet(labels, products);
    }


    @Override
    public IntegerRange getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(IntegerRange n) {

        IntegerRange largestLabel = null;

        for(IntegerRange iR : map.keySet()) {

            int currentIRLabel = iR.getValue();

            if(currentIRLabel <= n.getValue()) {
                if(largestLabel == null || currentIRLabel > largestLabel.getValue()) {
                    if(getValue(iR) != 0) {
                        largestLabel = iR;
                    }
                }
            }

        }

        if(largestLabel == null) {
            throw new NoSuchElementException("No values in set or no values in set less that n - set size: "
                    + getLabels().size());
        }

        return largestLabel;
    }

    @Override
    public IntegerRange getLargestLabelOfNoneZeroValueAndLabelPreferablyLessOrEqualTo(IntegerRange n) {

        IntegerRange largestLabel = null;
        IntegerRange smallestLabelLargerThanN = null;

        for(IntegerRange iR : map.keySet()) {

            int currentIRLable = iR.getValue();

            if(getValue(iR) > 0) {
                if (currentIRLable <= n.getValue()) {
                    if (largestLabel == null || currentIRLable > largestLabel.getValue()) {
                        largestLabel = iR;
                    }
                } else {
                    if (smallestLabelLargerThanN == null || currentIRLable < smallestLabelLargerThanN.getValue()) {
                        smallestLabelLargerThanN = iR;
                    }
                }
            }

        }

        if(largestLabel == null) {

            if(smallestLabelLargerThanN != null) {
                return smallestLabelLargerThanN;
            }

            throw new NoSuchElementException("No values in set or no values in set less that n - set size: "
                    + getLabels().size());
        }

        return largestLabel;

    }

    @Override
    public IntegerRange getLargestLabelOfNoneZeroValue() {
        IntegerRange largestLabel = null;

        for(IntegerRange iR : map.keySet()) {

            int currentIRLabel = iR.getValue();

            if(largestLabel == null || currentIRLabel > largestLabel.getValue()) {
                if(get(iR) != 0) {
                    largestLabel = iR;
                }
            }
        }

        if(largestLabel == null) {
            throw new NoSuchElementException("No non zero values in set - set size: " + getLabels().size());
        }

        return largestLabel;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public IntegerRange smallestLabel() {
        Set<IntegerRange> labels = getLabels();

        int minLabelInt = Integer.MAX_VALUE;
        IntegerRange minLabel = null;

        for(IntegerRange label : labels) {
            if(label.getValue() < minLabelInt) {
                minLabel = label;
                minLabelInt = label.getValue();
            }
        }

        return minLabel;

    }

    @Override
    public LabeledValueSet<IntegerRange, Double> valuesAddNWhereCorrespondingLabelNegativeInLVS(double n, LabeledValueSet<IntegerRange, ? extends Number> lvs) {
        ArrayList<IntegerRange> labels = new ArrayList<>(getLabels());
        ArrayList<Double> newValues = new ArrayList<>();


        for(IntegerRange label : labels) {
            if((Double) lvs.getValue(label) < 0) {
                newValues.add(getValue(label) + n);
            } else {
                newValues.add(getValue(label).doubleValue());
            }
        }


        return new IntegerRangeToDoubleSet(labels, newValues);
    }

    @Override
    public int countNegativeValues() {
        int count = 0;

        for(IntegerRange label : getLabels()) {
            if(getValue(label) < 0) {
                count++;
            }
        }

        return count;
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> zeroNegativeValues() {
        ArrayList<IntegerRange> labels = new ArrayList<>(getLabels());
        ArrayList<Integer> newValues = new ArrayList<>();


        for(IntegerRange label : labels) {
            if(getValue(label) < 0) {
                newValues.add(0);
            } else {
                newValues.add(getValue(label));
            }
        }

        return new IntegerRangeToIntegerSet(labels, newValues);
    }

    @Override
    public int countPositiveValues() {
        int count = 0;

        for(IntegerRange label : getLabels()) {
            if(getValue(label) > 0) {
                count++;
            }
        }

        return count;
    }
}
