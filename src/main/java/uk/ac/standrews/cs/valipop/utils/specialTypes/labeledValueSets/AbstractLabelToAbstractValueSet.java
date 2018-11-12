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

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class AbstractLabelToAbstractValueSet<AL, AV extends Number> implements LabelledValueSet<AL, AV>, Cloneable {

    protected Map<AL, AV> map = new TreeMap<>();

    public AbstractLabelToAbstractValueSet(List<AL> labels, List<AV> values) {

        if(labels.size() != values.size()) {
            throw new LabeledValueSetInitException("Labels and values lists of differing sizes", labels, values);
        }

        int c = 0;
        for(AL iR : labels) {
            map.put(iR, values.get(c));
            c++;
        }
    }

    public AbstractLabelToAbstractValueSet(Set<AL> labels, AV initValue) {

        for(AL iR : labels) {
            map.put(iR, initValue);
        }
    }

    public AbstractLabelToAbstractValueSet(Map<AL, AV> map) {
        init(map);
    }

    public AbstractLabelToAbstractValueSet() {}

    public abstract Class getLabelClass();
    public abstract Class getValueClass();

    public AbstractLabelToAbstractValueSet init(Map<AL, AV> map) {
        this.map = map;
        return this;
    }

    public abstract LabelledValueSet<AL, AV> constructSelf(List<AL> labels, List<AV> values);

    public abstract LabelledValueSet<AL, Integer> constructIntegerEquiverlent(List<AL> labels, List<Integer> values);
    public abstract LabelledValueSet<AL, Double> constructDoubleEquiverlent(List<AL> labels, List<Double> values);

    public abstract AV zero();
    public abstract AV sum(AV a, AV b);
    public abstract AV multiply(AV a, int n);

    @Override
    public Map<AL, AV> getMap() {
        return map;
    }

    @Override
    public AV getSumOfValues() {

        AV sum = zero();

        for(Map.Entry<AL, AV> iR : map.entrySet()) {
            sum = sum(sum, iR.getValue());
        }

        return sum;
    }

    @Override
    public AV getValue(AL label) {
        return map.get(label);
    }

    @Override
    public Set<AL> getLabels() {
        return map.keySet();
    }



    @Override
    public void add(AL label, AV value) {
        map.put(label, value);
    }

    @Override
    public AV get(AL label) {
        return map.get(label);
    }

    @Override
    public void update(AL label, AV value) {
        if(map.replace(label, value) == null) {
            add(label, value);
        }
    }

    @Override
    public AV remove(AL label) {
        return map.remove(label);
    }

    @Override
    public LabelledValueSet<AL, AV> productOfValuesAndN(Integer n) {
        List<AL> labels = new ArrayList<>();
        List<AV> products = new ArrayList<>();

        for(AL iR : map.keySet()) {
            labels.add(iR);
            products.add(multiply(getValue(iR), n));
        }

        return constructSelf(labels, products);
    }

    @Override
    public LabelledValueSet<AL, Double> productOfValuesAndN(Double n) {
        List<AL> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(AL iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR).doubleValue() * n);
        }

        return constructDoubleEquiverlent(labels, products);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public LabelledValueSet<AL, Double> valuesSubtractValues(LabelledValueSet<AL, ? extends Number> n) {
        List<AL> labels = new ArrayList<>();
        List<Double> results = new ArrayList<>();


        for(AL iR : map.keySet()) {
            labels.add(iR);

            Number sub = n.getValue(iR);
            if(sub == null) {
                throw new IncompatibleLabelValueSets("Sets do not contain same labels - " +
                        "mathematical operations not possible", this, n);
            }

            results.add(getValue(iR).doubleValue() - sub.doubleValue());
        }

        return constructDoubleEquiverlent(labels, results);
    }

    @Override
    public LabelledValueSet<AL, Integer> floorValues() {
        List<AL> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for(AL iR : map.keySet()) {
            labels.add(iR);
            values.add((int) Math.floor(getValue(iR).doubleValue()));
        }

        return constructIntegerEquiverlent(labels, values);
    }

    @Override
    public LabelledValueSet<AL, AV> clone() {
        List<AL> labels = new ArrayList<>();
        List<AV> values = new ArrayList<>();

        for(AL iR : map.keySet()) {
            labels.add(iR);
            values.add(getValue(iR));
        }

        return constructSelf(labels, values);
    }

    @Override
    public AL getLabelOfValueWithGreatestRemainder(Set<AL> usedLabels) {

        double largestRemainder = 0;
        AL labelOfLargestRemainder = null;

        for(AL iR : map.keySet()) {

            if(!usedLabels.contains(iR)) {
                double remainder = getValue(iR).doubleValue() % 1;
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
    public LabelledValueSet<AL, Double> valuesPlusValues(LabelledValueSet<AL, ? extends Number> n) {
        List<AL> labels = new ArrayList<>();
        List<Double> results = new ArrayList<>();


        for(AL iR : map.keySet()) {
            labels.add(iR);

            Number sub = n.getValue(iR);
            if(sub == null) {
                throw new IncompatibleLabelValueSets("Sets do not contain same labels - " +
                        "mathematical operations not possible", this, n);
            }

            results.add(getValue(iR).doubleValue() + sub.doubleValue());
        }

        return constructDoubleEquiverlent(labels, results);
    }

    @Override
    public LabelledValueSet<AL, Double> reproportion() {

        return divisionOfValuesByN(getSumOfValues());
    }

    @Override
    public LabelledValueSet<AL, Double> divisionOfValuesByN(AV n) {
        List<AL> labels = new ArrayList<>();
        List<Double> products = new ArrayList<>();

        for(AL iR : map.keySet()) {
            labels.add(iR);
            products.add(getValue(iR).doubleValue() / n.doubleValue());
        }

        return constructDoubleEquiverlent(labels, products);
    }

    @Override
    public int countNegativeValues() {
        int count = 0;

        for(AL label : getLabels()) {
            if(getValue(label).doubleValue() < 0) {
                count++;
            }
        }

        return count;
    }

    @Override
    public LabelledValueSet<AL, AV> zeroNegativeValues() {
        ArrayList<AL> labels = new ArrayList<>(getLabels());
        ArrayList<AV> newValues = new ArrayList<>();


        for(AL label : labels) {
            if(getValue(label).doubleValue() < 0) {
                newValues.add(zero());
            } else {
                newValues.add(getValue(label));
            }
        }

        return constructSelf(labels, newValues);
    }

    @Override
    public int countPositiveValues() {
        int count = 0;

        for(AL label : getLabels()) {
            if(getValue(label).doubleValue() > 0) {
                count++;
            }
        }

        return count;
    }


}
