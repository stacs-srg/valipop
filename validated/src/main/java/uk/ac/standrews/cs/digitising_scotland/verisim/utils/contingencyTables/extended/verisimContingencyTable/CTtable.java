package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtable {

    private ArrayList<CTRow> table = new ArrayList<>();

    public CTtable(CTtable table) {
        this.table = (ArrayList<CTRow>) table.getTable().clone();
    }

    public CTtable(CTtree tree) {

        Collection<Node> leafs = tree.getLeafNodes();

        for(Node leaf : leafs) {
            table.add(leaf.toCTRow());
        }
    }

    public ArrayList<CTRow> getTable() {
        return table;
    }

    public void addDateColumn() {

        for(CTRow row : table) {
            row.addDateVariable();
        }

    }

    public void deleteVariable(String variable) {

        for(CTRow row : table) {
            row.deleteVariable(variable);
        }

    }

    public void deleteRowsWhere(String variable, String hasValue) {

        for(CTRow row : table) {

            try {
                String value = row.getVariable(variable).getValue();

                if(Objects.equals(value, hasValue)) {
                    table.remove(row);
                }
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                // Not an issue, cannot delete row as does not have the variable to test on
            }

        }

    }

    public void collectLikeRows() {

        for(int i = 0; i < table.size(); i++) {
            CTRow row = table.get(i);
            for(int j = i+1; j < table.size(); j++) {
                if(row.tryAbsorbRow(table.get(j))) {
                    table.remove(j);
                }
            }
        }
    }

    public void outputToFile(PrintStream ps) {

        for(CTRow row : table) {
            ps.print(row.toString(","));
        }

        ps.close();

    }

    public void discritiseVariable(String variable, String forInput, PopulationStatistics populationStatistics) {

        for (CTRow row : table) {
            row.discritiseVariable(variable, forInput, populationStatistics);
        }

    }

}
