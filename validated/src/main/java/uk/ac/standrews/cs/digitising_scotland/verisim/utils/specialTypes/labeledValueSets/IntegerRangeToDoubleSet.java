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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.labeledValueSets;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.DoubleComparer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRangeToDoubleSet implements LabeledValueSet<IntegerRange, Double> {

    private static double DELTA = 1E-4;

    private Map<IntegerRange, Double> map = new HashMap<>();

    public IntegerRangeToDoubleSet(List<IntegerRange> labels, List<Double> values) {

        if(labels.size() != values.size()) {
            throw new LabeledValueSetInitException("Labels and values lists of differing sizes", labels, values);
        }

        int c = 0;
        for(IntegerRange iR : labels) {
            map.put(iR, values.get(c));
            c++;
        }

    }

    public IntegerRangeToDoubleSet(Set<IntegerRange> labels, Double initValue) {

        for(IntegerRange iR : labels) {
            map.put(iR, initValue);
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

    @Override
    public LabeledValueSet<IntegerRange, Double> productOfValuesAndN(Double n) {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) * n);
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
        double sumRounded = Math.round(sum);

        if(!DoubleComparer.equal(sum, sumRounded, DELTA)) {
            throw new ValuesDoNotSumToWholeNumberException("Cannot perform controlled rounding and maintain sum as " +
                    "values do not sum to a whole number", this);
        }

        int sumInt = (int) sumRounded;

        LabeledValueSet<IntegerRange, Integer> roundingSet = new IntegerRangeToIntegerSet();

        for(IntegerRange iR : getLabels()) {
            if(getValue(iR) < 0) {
                roundingSet.add(iR, 0);
            } else {
                roundingSet.add(iR, (int) Math.floor(getValue(iR)));
            }
        }

        Set<IntegerRange> usedLabels = new HashSet<>();

        int roundingSetSum;
        while((roundingSetSum = roundingSet.getSumOfValues()) != sumInt) {

            if(roundingSetSum < sumInt) {
                // need more in the rounding set therefore
                IntegerRange labelOfGreatestRemainder = this.getLabelOfValueWithGreatestRemainder(usedLabels);
                roundingSet.update(labelOfGreatestRemainder, roundingSet.getValue(labelOfGreatestRemainder)+1);
            }


            if(roundingSetSum > sumInt) {
                IntegerRange largestReducatbleLabel;
                try {
                    largestReducatbleLabel =
                            roundingSet.getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(new IntegerRange(roundingSetSum - sumInt));
                } catch (NoSuchElementException e) {
                    largestReducatbleLabel = this.smallestLabel();
                }
                roundingSet.update(largestReducatbleLabel, roundingSet.getValue(largestReducatbleLabel)-1);
            }

        }

        return roundingSet;
    }

    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumWithProductOfLabelAndValue() {

        double sum = getSumOfValues();
        double sumRounded = Math.round(sum);

        if(!DoubleComparer.equal(sum, sumRounded, DELTA)) {
            throw new ValuesDoNotSumToWholeNumberException("Cannot perform controlled rounding and maintain sum as " +
                    "values do not sum to a whole number", this);
        }

        int sumInt = (int) sumRounded;

        LabeledValueSet<IntegerRange, Integer> roundingSet = new IntegerRangeToIntegerSet();

        for(IntegerRange iR : getLabels()) {
            if(getValue(iR) < 0) {
                roundingSet.add(iR, 0);
            } else {
                roundingSet.add(iR, (int) Math.floor(getValue(iR)));
            }
        }

        Set<IntegerRange> usedLabels = new HashSet<>();

        int roundingSetSum;
        while((roundingSetSum = roundingSet.productOfLabelsAndValues().getSumOfValues()) != sumInt) {

            if(roundingSetSum < sumInt) {
                // need more in the rounding set therefore
                IntegerRange labelOfGreatestRemainder = this.getLabelOfValueWithGreatestRemainder(usedLabels);
                roundingSet.update(labelOfGreatestRemainder, roundingSet.getValue(labelOfGreatestRemainder)+1);
            }


            if(roundingSetSum > sumInt) {
                IntegerRange largestReducatbleLabel;
                try {
                    largestReducatbleLabel =
                            roundingSet.getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(new IntegerRange(roundingSetSum - sumInt));
                } catch (NoSuchElementException e) {
                    largestReducatbleLabel = this.smallestLabel();
                }
                roundingSet.update(largestReducatbleLabel, roundingSet.getValue(largestReducatbleLabel)-1);
            }

        }

        return roundingSet;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public LabeledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues() {
        double sum = productOfLabelsAndValues().getSumOfValues();
        double sumRounded = Math.round(sum);

        if(!DoubleComparer.equal(sum, sumRounded, DELTA)) {
            throw new ValuesDoNotSumToWholeNumberException("Cannot perform controlled rounding and maintain sum as " +
                    "values do not sum to a whole number", this);
        }

        int sumInt = (int) sumRounded;

        LabeledValueSet<IntegerRange, Integer> roundingSet = new IntegerRangeToIntegerSet();

        for(IntegerRange iR : getLabels()) {
            if(getValue(iR) < 0) {
                roundingSet.add(iR, 0);
            } else {
                roundingSet.add(iR, (int) Math.floor(getValue(iR)));
            }
        }

        Set<IntegerRange> usedLabels = new HashSet<>();

        int roundingSetSum;
        while((roundingSetSum = roundingSet.productOfLabelsAndValues().getSumOfValues()) != sumInt) {

            if(roundingSetSum < sumInt) {
                // need more in the rounding set therefore
                IntegerRange labelOfGreatestRemainder;
                try {
                    labelOfGreatestRemainder = this.getLabelOfValueWithGreatestRemainder(usedLabels);
                } catch (NoSuchElementException e) {
                    labelOfGreatestRemainder = this.smallestLabel();
                }
                roundingSet.update(labelOfGreatestRemainder, roundingSet.getValue(labelOfGreatestRemainder) + 1);
                usedLabels.add(labelOfGreatestRemainder);
            }

            // too many in rounding set therefore
            if(roundingSetSum > sumInt) {
                // catch and increase to self - then the up will put in a lower order birth
                IntegerRange largestReducatbleLabel;
                try {
                    largestReducatbleLabel =
                            roundingSet.getLargestLabelOfNoneZeroValueAndLabelPreferablyLessOrEqualTo(new IntegerRange(roundingSetSum - sumInt));
                } catch (NoSuchElementException e) {
                    largestReducatbleLabel = this.smallestLabel();
                }
                roundingSet.update(largestReducatbleLabel, roundingSet.getValue(largestReducatbleLabel)-1);
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

    @Override
    public LabeledValueSet<IntegerRange, Double> reproportion() {

        return divisionOfValuesByN(getSumOfValues());
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

            int currentIRLable = iR.getValue();

            if(currentIRLable <= n.getValue()) {
                if(largestLabel == null || currentIRLable > largestLabel.getValue()) {
                    largestLabel = iR;
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

            if(currentIRLable <= n.getValue()) {
                if(largestLabel == null || currentIRLable > largestLabel.getValue()) {
                    largestLabel = iR;
                }
            } else {
                if(largestLabel == null || currentIRLable < smallestLabelLargerThanN.getValue()) {
                    smallestLabelLargerThanN = iR;
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

            int currentIRLable = iR.getValue();

            if(largestLabel == null || currentIRLable > largestLabel.getValue()) {
                if(!DoubleComparer.equal(0, get(iR), DELTA)) {
                    largestLabel = iR;
                }
            }
        }

        if(largestLabel == null) {
            throw new NoSuchElementException("No non zero values in set - set size: " + getLabels().size());
        }

        return largestLabel;
    }

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
            if((Double) lvs.get(label) < 0) {
                newValues.add(getValue(label) + n);
            } else {
                newValues.add(getValue(label));
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
    public LabeledValueSet<IntegerRange, Double> zeroNegativeValues() {
        ArrayList<IntegerRange> labels = new ArrayList<>(getLabels());
        ArrayList<Double> newValues = new ArrayList<>();


        for(IntegerRange label : labels) {
            if(getValue(label) < 0) {
                newValues.add(0.0);
            } else {
                newValues.add(getValue(label));
            }
        }

        return new IntegerRangeToDoubleSet(labels, newValues);
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
