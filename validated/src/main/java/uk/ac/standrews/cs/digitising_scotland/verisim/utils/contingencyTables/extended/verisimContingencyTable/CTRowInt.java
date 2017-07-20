package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTRowInt extends CTRow<Integer> {

    @Override
    Integer combineCount(Integer a, Integer b) {
        return a + b;
    }
}
