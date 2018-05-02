package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToIntegerSet extends AbstractLabelToAbstractValueSet<String, Integer> {

    public StringToIntegerSet(List<String> labels, List<Integer> values) {
        super(labels, values);
    }

    public StringToIntegerSet(Set<String> labels, Integer initValue) {
        super(labels, initValue);
    }

    public StringToIntegerSet(Map<String, Integer> map) {
        super(map);
    }


    public StringToIntegerSet() { super();}

    @Override
    public Class getLabelClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return Integer.class;
    }

    @Override
    public LabelledValueSet<String, Integer> constructSelf(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquiverlent(List<String> labels, List<Integer> values) {
        return constructSelf(labels, values);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquiverlent(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values);
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
}
