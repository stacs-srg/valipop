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

import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.Utilities;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.ControlChildrenNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SeparationOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;

import java.time.LocalDate;
import java.time.Year;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInPartnershipNodeDouble extends DoubleNode<IntegerRange, SeparationOption> implements ControlChildrenNode {

    NumberOfChildrenInPartnershipNodeDouble(IntegerRange option, NumberOfChildrenInYearNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if (!init) {
            makeChildren();
        }
    }

    NumberOfChildrenInPartnershipNodeDouble() {
        super();
    }

    @Override
    public Node<SeparationOption, ?, Double, ?> makeChildInstance(SeparationOption childOption, Double initCount) {
        return new SeparationNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPerson person, LocalDate currentDate) {

        incCountByOne();

        IPartnership activePartnership = Utilities.getActivePartnership(person, currentDate);

        SeparationOption option = Utilities.toSeparate(activePartnership, Year.of(currentDate.getYear()));

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {

            SeparationNodeDouble n = (SeparationNodeDouble) addChild(new SeparationNodeDouble(option, this, 0.0, true));
            n.processPerson(person, currentDate);
            addDelayedTask(n);
        }
    }

    @Override
    public String getVariableName() {
        return LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP;
    }

    @Override
    public void makeChildren() {

        boolean childrenInYear = ((ChildrenInYearNodeDouble) getAncestor(new ChildrenInYearNodeDouble())).getOption();

        if (getOption().getValue() == 0 || !childrenInYear) {
            addChild(SeparationOption.NA, getCount());
        } else {
            addChild(SeparationOption.YES);
            addChild(SeparationOption.NO);
        }
    }
}
