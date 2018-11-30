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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ControlChildrenNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfPreviousChildrenInAnyPartnershipNodeDouble extends DoubleNode<IntegerRange, ChildrenInYearOption> implements RunnableNode, ControlChildrenNode {

    public NumberOfPreviousChildrenInAnyPartnershipNodeDouble(IntegerRange option, PreviousNumberOfChildrenInPartnershipNodeDouble parentNode, Double initCount) {
        super(option, parentNode, initCount);
    }

    public NumberOfPreviousChildrenInAnyPartnershipNodeDouble() {
        super();
    }

    @Override
    public Node<ChildrenInYearOption, ?, Double, ?> makeChildInstance(ChildrenInYearOption childOption, Double initCount) {
        return new ChildrenInYearNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPerson person, ValipopDate currentDate) {

        incCountByOne();

        IPartnership activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        ChildrenInYearOption option;

        if (activePartnership == null) {
            option = ChildrenInYearOption.NO;
        } else {
            option = PersonCharacteristicsIdentifier.getChildrenBirthedInYear(activePartnership, currentDate.getYearDate()) == 0 ?
                    ChildrenInYearOption.NO : ChildrenInYearOption.YES;
        }

        try {
            getChild(option).processPerson(person, currentDate);

        } catch (ChildNotFoundException e) {
            addChild(new ChildrenInYearNodeDouble(option, this, 0.0, true)).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "NPCIAP";
    }

    @Override
    public void runTask() {
        makeChildren();
    }

    @Override
    public void makeChildren() {

        addChild(ChildrenInYearOption.YES);
        addChild(ChildrenInYearOption.NO);
    }
}
