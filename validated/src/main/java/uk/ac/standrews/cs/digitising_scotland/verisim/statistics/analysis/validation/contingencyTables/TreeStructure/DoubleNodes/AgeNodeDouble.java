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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNodeDouble extends DoubleNode<IntegerRange, DiedOption> implements ControlChildrenNode, RunnableNode {

    boolean initNode = false;

    Collection<IPersonExtended> people = new ArrayList<>();

    public AgeNodeDouble(IntegerRange age, SexNodeDouble parentNode, double initCount, boolean init) {
        super(age, parentNode, initCount);

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer ageI = age.getValue();

        Date currentDate = yob.advanceTime(ageI, TimeUnit.YEAR);

        if(DateUtils.dateBefore(currentDate, getInputStats().getStartDate())) {
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
    public void processPerson(IPersonExtended person, Date currentDate) {

        initNode = true;
        people.add(person);

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = getOption().getValue();

        Date calcCurrentDate = yob.advanceTime(age, TimeUnit.YEAR);

        incCountByOne();

        DiedOption option;

        if(person.diedInYear(calcCurrentDate.getYearDate())) {
            option = DiedOption.YES;
        } else {
            option = DiedOption.NO;
        }

        try {
            getChild(option).processPerson(person, calcCurrentDate);
        } catch(ChildNotFoundException e) {
//            DiedNodeDouble n = (DiedNodeDouble) addChild(option);

            DiedNodeDouble n = (DiedNodeDouble) addChild(new DiedNodeDouble(option, this, true));
            n.processPerson(person, calcCurrentDate);
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
