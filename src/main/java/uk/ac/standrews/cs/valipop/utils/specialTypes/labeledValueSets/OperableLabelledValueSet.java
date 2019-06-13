package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface OperableLabelledValueSet<L,V> extends LabelledValueSet<L,V> {

    OperableLabelledValueSet<L,V> productOfLabelsAndValues();

    OperableLabelledValueSet<L,Double> divisionOfValuesByLabels();

    OperableLabelledValueSet<L,Integer> controlledRoundingMaintainingSum();

    OperableLabelledValueSet<IntegerRange, Integer> controlledRoundingMaintainingSumProductOfLabelValues();

    L getLargestLabelOfNonZeroValueAndLabelLessOrEqualTo(L n);

    L getLargestLabelOfNonZeroValueAndLabelPreferablyLessOrEqualTo(L n);

    L getLargestLabelOfNonZeroValue();

    L getRandomLabelOfNonZeroValue();

    L smallestLabel();

    OperableLabelledValueSet<L,Double> valuesAddNWhereCorrespondingLabelNegativeInLVS(double n, OperableLabelledValueSet<L, ? extends Number> lvs);
}
