package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToBigDecimalSet extends AbstractLabelToAbstractValueSet<String, BigDecimal> {

    public StringToBigDecimalSet(List<String> labels, List<BigDecimal> values) {
        super(labels, values);
    }

    public StringToBigDecimalSet() {
        super();
    }

    @Override
    public Class getLabelClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return BigDecimal.class;
    }

    @Override
    public LabelledValueSet<String, BigDecimal> constructSelf(List<String> labels, List<BigDecimal> values) {
        return new StringToBigDecimalSet(labels, values);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquiverlent(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquiverlent(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values);
    }

    @Override
    public BigDecimal zero() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sum(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    @Override
    public BigDecimal multiply(BigDecimal a, int n) {
        return a.multiply(new BigDecimal(n));
    }
}
