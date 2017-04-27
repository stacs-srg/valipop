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

    LabeledValueSet<L,Integer> controlledRoundingMaintainingSumProductOfLabelValues();

    LabeledValueSet<L,Integer> floorValues();

    LabeledValueSet<L,V> clone();

    L getLabelOfValueWithGreatestRemainder(Set<L> usedLabels);

    LabeledValueSet<L,Double> valuesPlusValues(LabeledValueSet<L, ? extends Number> n);

    LabeledValueSet<L,Double> reproportion();

    LabeledValueSet<L,Double> divisionOfValuesByN(double n);

    LabeledValueSet<L,Double> divisionOfValuesByLabels();

    L getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(L n);

    L getLargestLabelOfNoneZeroValue();

    L smallestLabel();
}
