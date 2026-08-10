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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.integerNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTableRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.IntNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SexOption;

import java.time.LocalDate;
import java.time.Year;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.LABEL_YEAR_OF_BIRTH;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNodeInt extends IntNode<Year, SexOption> {

    public YOBNodeInt() {
        super();
    }

    public YOBNodeInt(final Year option, final SourceNodeInt parentNode, final Integer initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public Node<SexOption, ?, Integer, ?> makeChildInstance(final SexOption childOption, final Integer initCount) {
        return new SexNodeInt(childOption, this, initCount);
    }

    @Override
    public void processPerson(final IPerson person, final LocalDate currentDate) {

        incCountByOne();

        try {
            getChild(person.getSex()).processPerson(person, currentDate);
        }
        catch (final ChildNotFoundException e) {
            addChild(person.getSex()).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return LABEL_YEAR_OF_BIRTH;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ContingencyTableRow<Integer> toContingencyTableRow() {

        final ContingencyTableRow r = getParent().toContingencyTableRow();
        r.setVariable(getVariableName(), String.valueOf(getOption().getValue()));
        return r;
    }
}
