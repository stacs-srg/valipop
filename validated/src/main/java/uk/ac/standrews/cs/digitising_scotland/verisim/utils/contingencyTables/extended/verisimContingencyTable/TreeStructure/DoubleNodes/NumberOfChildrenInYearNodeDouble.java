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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInYearNodeDouble extends DoubleNode<Integer, IntegerRange> implements ControlSelfNode, ControlChildrenNode, RunnableNode {

    Collection<IPersonExtended> people = new ArrayList<>();

    public NumberOfChildrenInYearNodeDouble(Integer option, ChildrenInYearNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
//            calcCount();
            advanceCount();
            makeChildren();
        }
    }

    public NumberOfChildrenInYearNodeDouble() {
        super();
    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new NumberOfChildrenInPartnershipNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        people.add(person);

        incCountByOne();

        int prevChildren = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                                    getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption().getValue();

        int childrenThisYear = ((NumberOfChildrenInYearNodeDouble)
                                                    getAncestor(new NumberOfChildrenInYearNodeDouble())).getOption();
        int ncip = prevChildren + childrenThisYear;
        IntegerRange range = resolveToChildRange(ncip);

        try {
            getChild(range).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {

            addChild(new NumberOfChildrenInPartnershipNodeDouble(range, this, 0.0, true))
                    .processPerson(person, currentDate);

//            addChild(prevChildren + childrenThisYear).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "NCIY";
    }

    @Override
    public void advanceCount() {

        // Should we be restricting this so much?
        if(getCount() > CTtree.NODE_MIN_COUNT && getOption() != 0) {
            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            // removed age + 1
            Date currentDate = yob.advanceTime(age + 1, TimeUnit.YEAR);

            SourceNodeDouble sN = (SourceNodeDouble) getAncestor(new SourceNodeDouble());

            YOBNodeDouble yobN;
            try {
                yobN = (YOBNodeDouble) sN.getChild(currentDate.getYearDate());
            } catch (ChildNotFoundException e) {
                yobN = (YOBNodeDouble) sN.addChild(currentDate.getYearDate());
            }

            double sexRatio = getInputStats().getMaleProportionOfBirths();

            for (Node<SexOption, ?, Double, ?> n : yobN.getChildren()) {

                SexNodeDouble sexN = (SexNodeDouble) n;
                double adjCount;

                double childrenFromThisNode = getCount() * getOption();

                if (sexN.getOption() == SexOption.MALE) {
                    adjCount = childrenFromThisNode * sexRatio;
                } else { // i.e. if female
                    adjCount = childrenFromThisNode * (1 - sexRatio);
                }

                try {
                    sexN.getChild(new IntegerRange(0)).incCount(adjCount);
                } catch (ChildNotFoundException e) {
                    AgeNodeDouble aN = new AgeNodeDouble(new IntegerRange(0), sexN, adjCount, true);
                    sexN.addChild(aN);
                    addDelayedTask(aN);
                }

            }
        }


    }

    @Override
    public void calcCount() {

        if(getOption() == 0) {
            setCount(getParent().getCount());
        } else {

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new MultipleBirthStatsKey(age, getParent().getCount(), new CompoundTimeUnit(1, TimeUnit.YEAR), currentDate));


            LabeledValueSet<IntegerRange, Double> stat = mDC.getRawUncorrectedCount();

            for(IntegerRange iR : stat.getLabels()) {
                if(iR.contains(getOption())) {
                    setCount(stat.get(iR));
                }
            }

        }

        advanceCount();
        makeChildren();

    }

    @Override
    public void makeChildren() {

        int numberOfPrevChildInPartnership = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                                            getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption().getValue();
        int childrenInYear = getOption();

        int numberOfChildInPartnership = numberOfPrevChildInPartnership + childrenInYear;
        IntegerRange range = resolveToChildRange(numberOfChildInPartnership);

        addChild(range, getCount());
    }

    @Override
    public void runTask() {
        advanceCount();
    }

    @SuppressWarnings("Duplicates")
    private IntegerRange resolveToChildRange(Integer ncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(ncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(ncip)) {
                return o;
            }
        }

        if(ncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }

}
