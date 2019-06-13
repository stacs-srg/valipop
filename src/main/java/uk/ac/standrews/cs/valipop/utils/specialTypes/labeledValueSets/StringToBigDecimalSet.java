package uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets;

import org.apache.commons.math3.random.RandomGenerator;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StringToBigDecimalSet extends AbstractLabelToAbstractValueSet<String, BigDecimal> {

    public StringToBigDecimalSet(List<String> labels, List<BigDecimal> values, RandomGenerator random) {
        super(labels, values, random);
    }

    public StringToBigDecimalSet(RandomGenerator random) {
        super(random);
    }

    @Override
    public Class getValueClass() {
        return BigDecimal.class;
    }

    @Override
    public LabelledValueSet<String, BigDecimal> constructSelf(List<String> labels, List<BigDecimal> values) {
        return new StringToBigDecimalSet(labels, values, random);
    }

    @Override
    public LabelledValueSet<String, Integer> constructIntegerEquivalent(List<String> labels, List<Integer> values) {
        return new StringToIntegerSet(labels, values, random);
    }

    @Override
    public LabelledValueSet<String, Double> constructDoubleEquivalent(List<String> labels, List<Double> values) {
        return new StringToDoubleSet(labels, values, random);
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
