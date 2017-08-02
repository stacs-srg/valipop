package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.TableInstances;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTCell;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTtable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;

import java.io.PrintStream;
import java.util.Iterator;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableFull extends CTtable {

    public CTtableFull(CTtree tree, PrintStream ps) {

        Iterator<Node> leafs = tree.getLeafNodes().iterator();

        boolean first = true;


        while(leafs.hasNext()) {
            CTRow leaf = leafs.next().toCTRow();

            if(leaf != null) {

                if (first) {
                    ps.print(getVarNames(",", leaf));
                    first = false;
                }

                if (leaf.getCount() != null && leaf.countGreaterThan(0.1)) {

                    ps.print(leaf.toString(","));
                }

            }

        }

        ps.close();
    }

    protected String getVarNames(String sep, CTRow row) {

        String s = "";

        for(Object cell : row.getCells()) {

            s += ((CTCell) cell).getVariable() + sep;

        }

        s += "freq\n";


        return s;
    }

}
