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

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ControlChildrenNode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNodeDouble extends DoubleNode<IntegerRange, DiedOption> implements ControlChildrenNode, RunnableNode {

    boolean initNode = false;

    Collection<IPerson> people = new ArrayList<>();

    public AgeNodeDouble(IntegerRange age, SexNodeDouble parentNode, double initCount, boolean init) {
        super(age, parentNode, initCount);

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer ageI = age.getValue();

        ValipopDate currentDate = yob.advanceTime(ageI, TimeUnit.YEAR);

        if(DateUtils.dateBefore(currentDate, getStartDate())) {
            initNode = true;
        }

        if(!init) {
            makeChildren();
        }
    }

    @Override
    public void incCount(Double byCount) {
        setCount(getCount() + byCount);
    }

    public AgeNodeDouble() {
        super();
    }

    @Override
    public Node<DiedOption, ?, Double, ?> makeChildInstance(DiedOption childOption, Double initCount) {
        return new DiedNodeDouble(childOption, this, false);
    }

    @Override
    public void makeChildren() {

        for(DiedOption o : DiedOption.values()) {
            addChild(o);
        }

    }

    @Override
    public void processPerson(IPerson person, ValipopDate currentDate) {

        initNode = true;

        people.add(person);

        incCountByOne();

//        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
//        Integer age = getOption().getValue();
//
//        Date calcCurrentDate = yob.advanceTime(age, TimeUnit.YEAR);

        DiedOption option;

        if(person.diedInYear(currentDate.getYearDate())) {
            option = DiedOption.YES;
        } else {
            option = DiedOption.NO;
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch(ChildNotFoundException e) {
            DiedNodeDouble n = (DiedNodeDouble) addChild(new DiedNodeDouble(option, this, true));
            n.processPerson(person, currentDate);
            addDelayedTask(n);
        }
    }

    @Override
    public String getVariableName() {
        return "Age";
    }

    @Override
    public void runTask() {
        makeChildren();
    }

    public double sumOfNPCIAPDescendants(IntegerRange option) {

        double count = 0;

        for(Node c : getChildren()) {
            // c is of type died
            DiedNodeDouble cD = (DiedNodeDouble) c;
            for(Node gc : cD.getChildren()) {
                // gc is of type pncip
                PreviousNumberOfChildrenInPartnershipNodeDouble gcP = (PreviousNumberOfChildrenInPartnershipNodeDouble) gc;
                for(Node ggc : gcP.getChildren()) {
                    // ggc is of type NPCIAP
                    NumberOfPreviousChildrenInAnyPartnershipNodeDouble ggcN = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) ggc;

                    if(ggcN.getOption().hash() == option.hash()) {
                        count += ggcN.getCount();
                    }

                }
            }
        }
        return count;
    }

    public CTRow<Double> toCTRow() {

        if(initNode) {
            return null;
        } else {

            CTRow r = getParent().toCTRow();
            if (r != null) {
                r.setVariable(getVariableName(), getOption().toString());
            }
            return r;

        }


    }
}
