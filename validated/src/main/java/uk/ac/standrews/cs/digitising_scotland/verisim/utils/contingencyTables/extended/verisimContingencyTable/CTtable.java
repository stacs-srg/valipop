package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtable {

    private ArrayList<ArrayList<String>> table;
    private ArrayList<String> variables;

    public CTtable(CTtable table) {
        this.table = (ArrayList<ArrayList<String>>) table.getTable().clone();
    }

    public CTtable(CTtree tree) {

        Collection<Node> leafs = tree.getLeafNodes();

        for(Node leaf : leafs) {
            // TODO null pointer on run ?
            table.add(leaf.toStringAL());
        }
    }

    public ArrayList<ArrayList<String>> getTable() {
        return table;
    }

    public void addDateColumn() {

    }

    public void deleteVariable(String variable) {

    }

    public void deleteRowsWhere(String variable, String hasValue) {

    }

    public void collectLikeRows() {

    }

    public void outputToFile(String file) {

    }

    public void discritiseVariable(String variable) {
        // for age


        // else throw not implemented exception


    }

}
