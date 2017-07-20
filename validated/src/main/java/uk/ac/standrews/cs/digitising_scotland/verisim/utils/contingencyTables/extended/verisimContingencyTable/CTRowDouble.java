package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTRowDouble extends CTRow<Double> {

    @Override
    Double combineCount(Double a, Double b) {
        return a + b;
    }
}
