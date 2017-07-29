package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.TableInstances;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTtable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTCell;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.NoTableRowsException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.VariableNotFoundExcepction;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableOB extends CTtable {



    public CTtableOB(CTtree tree, PopulationStatistics inputStats) {

        Iterator<Node> leafs = tree.getLeafNodes().iterator();

        while(leafs.hasNext()) {
            Node n = leafs.next();
            CTRow leaf = n.toCTRow();

            try {
                leaf.addDateVariable();



                if (Objects.equals(leaf.getVariable("Sex").getValue(), "FEMALE")) {
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

                    if (h == null) {
                        table.put(leaf.hash(), leaf);
                    } else {
                        h.setCount(h.combineCount(h.getCount(), leaf.getCount()));
                    }
                }



            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                // Unfilled row - thus pass
            }

        }
    }



}
