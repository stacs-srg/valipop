/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.specialisedTables;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTableRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ContingencyTree;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SexOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PartnershipContingencyTable extends ContingencyTable {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PartnershipContingencyTable(final ContingencyTree tree) {

        for (final Node leaf : tree.getLeafNodes()) {
            final ContingencyTableRow row = leaf.toContingencyTableRow();

            if (row != null && row.getCount() != null) {
                try {
                    row.addDateVariable();

                    if (row.getVariable(LABEL_SEX).getValue().equals(SexOption.FEMALE.toString())) {

                        row.deleteVariable(LABEL_SEX);
                        row.deleteVariable(LABEL_DIED);
                        row.deleteVariable(LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_PARTNERSHIP);
                        row.deleteVariable(LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_ANY_PARTNERSHIP);
                        row.deleteVariable(LABEL_CHILDREN_IN_YEAR);
                        row.deleteVariable(LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP);
                        row.deleteVariable(LABEL_SEPARATED);
                        row.deleteVariable(LABEL_NUMBER_OF_CHILDREN_IN_YEAR);

                        final ContingencyTableRow existingRow = table.get(row.hash());

                        if (existingRow == null)
                            table.put(row.hash(), row);
                        else
                            existingRow.setCount(existingRow.combineCount(existingRow.getCount(), row.getCount()));
                    }

                } catch (final RuntimeException ignore) {
                    // Unfilled row - thus pass
                }
            }
        }
    }
}
