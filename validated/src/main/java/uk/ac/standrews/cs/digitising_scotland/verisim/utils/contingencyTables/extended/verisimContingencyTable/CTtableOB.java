package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableOB {

    private HashMap<String, CTRow> table = new HashMap<>();

    public CTtableOB(CTtree tree, PopulationStatistics inputStats) {

        Iterator<Node> leafs = tree.getLeafNodes().iterator();

        while(leafs.hasNext()) {
            Node n = leafs.next();
            CTRow leaf = n.toCTRow();

            try {
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

                CTRow h = table.get(leaf.hash());

                if(h == null) {
                    table.put(leaf.hash(), leaf);
                } else {
                    h.setCount(h.combineCount(h.getCount(), leaf.getCount()));
                }

            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                // Unfilled row - thus pass
            }

        }
    }

    public void outputToFile(PrintStream ps) throws NoTableRowsException {

        ps.print(getVarNames(","));

        for(CTRow row : table.values()) {
            ps.print(row.toString(","));
        }

        ps.close();

    }

    private String getVarNames(String sep) throws NoTableRowsException {

        ArrayList<String> keys = new ArrayList<>(table.keySet());
        if(keys.size() == 0) {
            throw new NoTableRowsException();
        }

        CTRow row = table.get(keys.get(0));

        String s = "";

        for(Object cell : row.getCells()) {

            s += ((CTCell) cell).getVariable() + sep;

        }

        s += "freq\n";


        return s;
    }

}
