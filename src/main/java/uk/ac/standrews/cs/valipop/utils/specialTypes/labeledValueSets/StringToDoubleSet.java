package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToDoubleSet extends AbstractLabelToAbstractValueSet<String, Double> {

    public StringToDoubleSet(List<String> labels, List<Double> values) {
        super(labels, values);
    }

    public StringToDoubleSet(Set<String> labels, Double initValue) {
        super(labels, initValue);
    }

    public StringToDoubleSet(Map<String, Double> map) {
        super(map);
    }

    public StringToDoubleSet() {
        super();
    }

    @Override
    public Class getLabelClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return Double.class;
    }

    @Override
    public LabelledValueSet<String, Double> constructSelf(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquiverlent(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquiverlent(List<String> labels, List<Double> values) {
        return constructSelf(labels, values);
    }

    @Override
    public Double zero() {
        return 0.0;
    }

    @Override
    public Double sum(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double multiply(Double a, int n) {
        return a * n;
    }
}
