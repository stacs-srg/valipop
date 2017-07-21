package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableOB {

    private ArrayList<CTRow> table = new ArrayList<>();

    public CTtableOB(CTtableOB table) {
        this.table = (ArrayList<CTRow>) table.getTable().clone();
    }

    public CTtableOB(CTtree tree, PopulationStatistics inputStats) {

        Iterator<Node> leafs = tree.getLeafNodes().iterator();

        while(leafs.hasNext()) {
            CTRow leaf = leafs.next().toCTRow();

            leaf.addDateVariable();

            try {
                if(Objects.equals(leaf.getVariable("Sex"), "MALE")) {
                    leaf.deleteVariable("Sex");
                }
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                variableNotFoundExcepction.printStackTrace();
            }
            leaf.deleteVariable("Sex");

            leaf.deleteVariable("YOB");
            leaf.deleteVariable("Died");
            leaf.deleteVariable("PNCIP");
            leaf.deleteVariable("NCIY");
            leaf.deleteVariable("NCIP");
            leaf.deleteVariable("Separated");
            leaf.deleteVariable("NPA");

            leaf.discritiseVariable("Age", "OB", inputStats);

            if(!tryCombineIntoExistingRows(leaf)) {
                // could not combine with existing row - so add new row to table
                table.add(leaf);
            }
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

    public boolean tryCombineIntoExistingRows(CTRow newRow) {
        for(int i = 0; i < table.size(); i++) {
            CTRow existingRow = table.get(i);
            if(existingRow.tryAbsorbRow(newRow)) {
                return true;
            }
        }

        return false;
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
