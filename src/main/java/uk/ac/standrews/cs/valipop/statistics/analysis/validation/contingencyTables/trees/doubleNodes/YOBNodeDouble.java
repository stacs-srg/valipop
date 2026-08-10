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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.doubleNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTableRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ControlChildrenNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SexOption;

import java.time.LocalDate;
import java.time.Year;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.LABEL_YEAR_OF_BIRTH;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNodeDouble extends DoubleNode<Year, SexOption> implements ControlChildrenNode {

    public YOBNodeDouble(Year childOption, SourceNodeDouble parentNode, Double initCount) {
        super(childOption, parentNode, initCount);
        makeChildren();
    }

    public YOBNodeDouble() {
        super();
    }

    @Override
    public void processPerson(IPerson person, LocalDate currentDate) {

        try {
            getChild(person.getSex()).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(person.getSex()).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return LABEL_YEAR_OF_BIRTH;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ContingencyTableRow<Double> toContingencyTableRow() {
        ContingencyTableRow r = getParent().toContingencyTableRow();
        r.setVariable(getVariableName(), Integer.toString(getOption().getValue()));
        return r;
    }

    @Override
    public Node<SexOption, ?, Double, ?> makeChildInstance(SexOption childOption, Double initCount) {
        return new SexNodeDouble(childOption, this, initCount);
    }

    @Override
    public void makeChildren() {

        addChild(SexOption.MALE);
        addChild(SexOption.FEMALE);
    }
}
