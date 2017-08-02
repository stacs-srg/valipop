package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.TableInstances;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTtable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes.DiedNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes.NewPartnerAgeNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes.SexNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.IntNodes.DiedNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.IntNodes.NewPartnerAgeNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.VariableNotFoundExcepction;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtableDeath extends CTtable {

    public CTtableDeath(CTtree tree) {

        Iterator<Node> leafs = tree.getLeafNodes().iterator();

        while(leafs.hasNext()) {
            Node n = leafs.next();
            CTRow leaf = n.toCTRow();

            if(leaf != null && leaf.getCount() != null) {

                try {
                    leaf.addDateVariable();

                    leaf.deleteVariable("YOB");
                    leaf.deleteVariable("PNCIP");
                    leaf.deleteVariable("NCIY");
                    leaf.deleteVariable("CIY");
                    leaf.deleteVariable("NPCIAP");
                    leaf.deleteVariable("NCIP");
                    leaf.deleteVariable("Separated");
                    leaf.deleteVariable("NPA");

                    CTRow h = table.get(leaf.hash());

                    if (h == null) {
                        table.put(leaf.hash(), leaf);
                    } else {
                        h.setCount(h.combineCount(h.getCount(), leaf.getCount()));
                    }


                } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                    // Unfilled row - thus pass
                }

            }

        }
    }

}
