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

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SexNodeDouble extends DoubleNode<SexOption, IntegerRange> {

    public SexNodeDouble(SexOption option, YOBNodeDouble parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    public SexNodeDouble() {
        super();
    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        try {
            return resolveChildNodeForAge(childOption.getValue());
        } catch (ChildNotFoundException e) {
            return new AgeNodeDouble(childOption, this, initCount, false);
        }
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
//        incCountByOne();

//        int age = person.ageOnDate(new ExactDate(31, 12, currentDate.getYear() - 1));
        int age = person.ageOnDate(currentDate);

        try {
            resolveChildNodeForAge(age).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new AgeNodeDouble(new IntegerRange(age), this, 0, true)).processPerson(person, currentDate);

//            addChild(new IntegerRange(age)).processPerson(person, currentDate);

        }
    }

    @Override
    public String getVariableName() {
        return "Sex";
    }

    public Node<IntegerRange, ?, Double, ?> resolveChildNodeForAge(Integer age) throws ChildNotFoundException {

        if(age != null) {
            for (Node<IntegerRange, ?, Double, ?> aN : getChildren()) {
                if (aN.getOption().contains(age)) {
                    return aN;
                }
            }
        }
        throw new ChildNotFoundException();
    }


    public Node<IntegerRange, ?, Double, ?> getChild(IntegerRange childOption) throws ChildNotFoundException {

        return resolveChildNodeForAge(childOption.getValue());

    }
}
