package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface OperableLabelledValueSet<L,V> extends LabelledValueSet<L,V> {

    OperableLabelledValueSet<L,V> productOfLabelsAndValues();

    OperableLabelledValueSet<L,Double> divisionOfValuesByLabels();

    OperableLabelledValueSet<L,Integer> controlledRoundingMaintainingSum();

    OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumWithProductOfLabelAndValue();

    OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues();

    L getLargestLabelOfNoneZeroValueAndLabelLessOrEqualTo(L n);

    L getLargestLabelOfNoneZeroValueAndLabelPreferablyLessOrEqualTo(L n);

    L getLargestLabelOfNoneZeroValue();

    L smallestLabel();

    OperableLabelledValueSet<L,Double> valuesAddNWhereCorrespondingLabelNegativeInLVS(double n, OperableLabelledValueSet<L, ? extends Number> lvs);
}
