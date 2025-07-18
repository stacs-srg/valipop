/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableInstances;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.ContingencyTableRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ContingencyTree;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Node;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.NewPartnerAgeNodeInt.PARTNER_AGE_LABEL;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthContingencyTable extends ContingencyTable {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BirthContingencyTable(final ContingencyTree tree) {

        for (final Node node : tree.getLeafNodes()) {

            final ContingencyTableRow leaf = node.toCTRow();

            if (leaf != null && leaf.getCount() != null) {
                try {
                    leaf.addDateVariable();

                    if (leaf.getVariable("Sex").getValue().equals("F")) {

                        leaf.deleteVariable("Sex");
                        leaf.deleteVariable("Died");
                        leaf.deleteVariable("PNCIP");
                        leaf.deleteVariable("NCIY");
                        leaf.deleteVariable("NCIP");
                        leaf.deleteVariable("Separated");
                        leaf.deleteVariable(PARTNER_AGE_LABEL);

                        final ContingencyTableRow row = table.get(leaf.hash());

                        if (row == null)
                            table.put(leaf.hash(), leaf);
                         else
                            row.setCount(row.combineCount(row.getCount(), leaf.getCount()));

                    }
                } catch (final RuntimeException ignore) {
                    // Unfilled row - thus pass
                }
            }
        }
    }
}
