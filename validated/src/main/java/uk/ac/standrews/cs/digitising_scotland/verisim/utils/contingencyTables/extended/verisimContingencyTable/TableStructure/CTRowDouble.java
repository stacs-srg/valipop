package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.DoubleComparer;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTRowDouble extends CTRow<Double> {

    @Override
    public Double combineCount(Double a, Double b) {
        return a + b;
    }

    @Override
    public boolean countEqualToZero() {
        return DoubleComparer.equal(getCount(), 0, 0.000001);
    }

    @Override
    public boolean countGreaterThan(Double v) {
        return getCount() > v;
    }
}
