package utils.specialTypes;

import utils.specialTypes.integerRange.IntegerRange;

import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface LabeledValueSet<L,V> {

    Map<L,V> getMap();

    V getSumOfValues();

    V getValue(L label);

    Set<L> getLabels();

    LabeledValueSet<L,V> productOfLabelsAndValues();

    void add(L label, V value);

    V get(L label);

    void update(L label, V value);

    V remove(L label);

    LabeledValueSet<L,V> productOfValuesAndN(Integer n);

    LabeledValueSet<L, Double> valuesSubtractValues(LabeledValueSet<L, ? extends Number> n);

    LabeledValueSet<L,Integer> controlledRoundingMaintainingSum();

    LabeledValueSet<L,V> clone();

    L getLabelOfValueWithGreatestRemainder(Set<L> usedLabels);

    LabeledValueSet<L,V> valuesPlusValues(LabeledValueSet<L, ? extends Number> n);
}
