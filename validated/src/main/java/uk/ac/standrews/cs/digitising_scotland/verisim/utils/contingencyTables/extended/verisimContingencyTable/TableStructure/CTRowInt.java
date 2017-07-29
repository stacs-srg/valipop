package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTRowInt extends CTRow<Integer> {

    @Override
    public Integer combineCount(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public boolean countEqualToZero() {
        return getCount() == 0;
    }

    @Override
    public boolean countGreaterThan(Double v) {
        return true;
    }
}
