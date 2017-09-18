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

import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTCell;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTtable;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;

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

        StringBuilder s = new StringBuilder();

        for(Object cell : row.getCells()) {

            s.append(((CTCell) cell).getVariable() + sep);

        }

        s.append("freq\n");


        return s.toString();
    }

}
