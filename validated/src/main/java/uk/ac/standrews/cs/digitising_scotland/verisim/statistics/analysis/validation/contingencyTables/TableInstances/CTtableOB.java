/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableInstances;

import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTtable;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.VariableNotFoundExcepction;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.PopulationStatistics;

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

            if(leaf != null && leaf.getCount() != null) {

                try {
                    leaf.addDateVariable();


                    if (Objects.equals(leaf.getVariable("Sex").getValue(), "FEMALE")) {
                        leaf.deleteVariable("Sex");

//                        leaf.deleteVariable("YOB");
                        leaf.deleteVariable("Died");
                        leaf.deleteVariable("PNCIP");
                        leaf.deleteVariable("NCIY");
                        leaf.deleteVariable("NCIP");
                        leaf.deleteVariable("Separated");
                        leaf.deleteVariable("NPA");

//                        leaf.discritiseVariable("Age", "OB", inputStats);

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



}
