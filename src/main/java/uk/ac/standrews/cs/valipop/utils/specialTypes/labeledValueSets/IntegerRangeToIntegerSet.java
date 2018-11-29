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
package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRangeToIntegerSet extends AbstractLabelToAbstractValueSet<IntegerRange, Integer>
        implements OperableLabelledValueSet<IntegerRange, Integer>, Cloneable {

    public IntegerRangeToIntegerSet(List<IntegerRange> labels, List<Integer> values) {
        super(labels, values);
    }

    public IntegerRangeToIntegerSet(Set<IntegerRange> labels, Integer initValue) {
        super(labels, initValue);
    }

    public IntegerRangeToIntegerSet(Map<IntegerRange, Integer> map) {
        super(map);
    }

    public IntegerRangeToIntegerSet(LabelledValueSet<IntegerRange, Integer> lvs) {
        super(lvs.getMap());
    }

    public IntegerRangeToIntegerSet() {
        super();
    }

    @Override
    public Class getLabelClass() {
        return IntegerRange.class;
    }

    @Override
    public Class getValueClass() {
        return Integer.class;
    }

    @Override
    public LabelledValueSet<IntegerRange, Integer> constructSelf(List<IntegerRange> labels, List<Integer> values) {
        return new IntegerRangeToIntegerSet(labels, values);
    }

    @Override
    public LabelledValueSet<IntegerRange, Integer> constructIntegerEquiverlent(List<IntegerRange> labels, List<Integer> values) {
        return constructSelf(labels, values);
    }

    @Override
    public LabelledValueSet<IntegerRange, Double> constructDoubleEquiverlent(List<IntegerRange> labels, List<Double> values) {
        return new IntegerRangeToDoubleSet(labels, values);
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
    public OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSum() {
        return new IntegerRangeToIntegerSet(this.clone());
    }

    @Override
    public OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues() {
        return new IntegerRangeToIntegerSet(this.clone());
    }

    @Override
    public OperableLabelledValueSet<IntegerRange, Integer> productOfLabelsAndValues() {

        List<IntegerRange> labels = new ArrayList<>();
        List<Integer> products = new ArrayList<>();

        for (IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(iR.getValue() * getValue(iR));
        }

        return new IntegerRangeToIntegerSet(labels, products);

    }

    @Override
    public OperableLabelledValueSet<IntegerRange, Double> divisionOfValuesByLabels() {
        List<IntegerRange> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for (IntegerRange iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR) / (double) iR.getValue());
        }

        return new IntegerRangeToDoubleSet(labels, products);
    }

    @Override
    public IntegerRange getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(IntegerRange n) {

        IntegerRange largestLabel = null;

        for (IntegerRange iR : map.keySet()) {

            int currentIRLabel = iR.getValue();

            if (currentIRLabel <= n.getValue()) {
                if (largestLabel == null || currentIRLabel > largestLabel.getValue()) {
                    if (getValue(iR) != 0) {
                        largestLabel = iR;
                    }
                }
            }

        }

        if (largestLabel == null) {
            throw new NoSuchElementException("No values in set or no values in set less that n - set size: "
                    + getLabels().size());
        }

        return largestLabel;
    }

    @Override
    public IntegerRange getLargestLabelOfNoneZeroValueAndLabelPreferablyLessOrEqualTo(IntegerRange n) {

        IntegerRange largestLabel = null;
        IntegerRange smallestLabelLargerThanN = null;

        for (IntegerRange iR : map.keySet()) {

            int currentIRLable = iR.getValue();

            if (getValue(iR) > 0) {
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

        if (largestLabel == null) {

            if (smallestLabelLargerThanN != null) {
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

        for (IntegerRange iR : map.keySet()) {

            int currentIRLabel = iR.getValue();

            if (largestLabel == null || currentIRLabel > largestLabel.getValue()) {
                if (get(iR) != 0) {
                    largestLabel = iR;
                }
            }
        }

        if (largestLabel == null) {
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

        for (IntegerRange label : labels) {
            if (label.getValue() < minLabelInt) {
                minLabel = label;
                minLabelInt = label.getValue();
            }
        }

        return minLabel;

    }

    @Override
    public OperableLabelledValueSet<IntegerRange, Double> valuesAddNWhereCorrespondingLabelNegativeInLVS(double n, OperableLabelledValueSet<IntegerRange, ? extends Number> lvs) {
        ArrayList<IntegerRange> labels = new ArrayList<>(getLabels());
        ArrayList<Double> newValues = new ArrayList<>();


        for (IntegerRange label : labels) {
            if ((Double) lvs.getValue(label) < 0) {
                newValues.add(getValue(label) + n);
            } else {
                newValues.add(getValue(label).doubleValue());
            }
        }


        return new IntegerRangeToDoubleSet(labels, newValues);
    }
}
