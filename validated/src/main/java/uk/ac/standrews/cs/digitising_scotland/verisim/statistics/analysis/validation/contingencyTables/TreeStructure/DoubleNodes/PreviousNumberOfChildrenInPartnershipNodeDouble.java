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
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PreviousNumberOfChildrenInPartnershipNodeDouble extends DoubleNode<IntegerRange, IntegerRange> {


    public PreviousNumberOfChildrenInPartnershipNodeDouble(IntegerRange option, DiedNodeDouble parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    public PreviousNumberOfChildrenInPartnershipNodeDouble() {
        super();
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        Integer numberOfPrevChildrenInAnyPartnership = person.numberOfChildrenBirthedBeforeDate(currentDate.getYearDate());
        IntegerRange range = resolveToChildRange(numberOfPrevChildrenInAnyPartnership);

        try {
            getChild(range).processPerson(person, currentDate);
        } catch(ChildNotFoundException e) {
            addChild(range).processPerson(person, currentDate);
        }
    }

    public Node<IntegerRange, ?, Double, ?> addChild(IntegerRange childOption, Double initCount) {

        Node<IntegerRange, ?, Double, ?> child;

        try {
            child = getChild(childOption);
            child.incCount(initCount);
        } catch (ChildNotFoundException e)  {
            child = makeChildInstance(childOption, initCount);
            super.addChild(child);
        }

        return child;

    }

    @Override
    public String getVariableName() {
        return "PNCIP";
    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new NumberOfPreviousChildrenInAnyPartnershipNodeDouble(childOption, this, initCount);
    }

    @SuppressWarnings("Duplicates")
    public IntegerRange resolveToChildRange(Integer npciap) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(npciap)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);


        Collection<IntegerRange> birthOrders;
        try {
            birthOrders = getInputStats().getOrderedBirthRates(currentDate).getData(age).getLabels();
        } catch (InvalidRangeException e) {
            SelfCorrectingTwoDimensionDataDistribution data = getInputStats().getOrderedBirthRates(currentDate);
            birthOrders = data.getData(data.getSmallestLabel()).getLabels();
        }


        for(IntegerRange o : birthOrders) {
            if(o.contains(npciap)) {
                return o;
            }
        }

        throw new Error("Did not resolve any permissable ranges");
    }
}
