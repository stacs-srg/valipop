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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.Interfaces.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.Interfaces.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ChildrenInYearNodeDouble extends DoubleNode<ChildrenInYearOption, Integer> implements ControlSelfNode, ControlChildrenNode {

    public ChildrenInYearNodeDouble(ChildrenInYearOption option, NumberOfPreviousChildrenInAnyPartnershipNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
            calcCount();
        }
    }

    public ChildrenInYearNodeDouble() {
        super();
    }

    @Override
    public Node<Integer, ?, Double, ?> makeChildInstance(Integer childOption, Double initCount) {
        return new NumberOfChildrenInYearNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Integer option;

        if(activePartnership == null){
            option = 0;
        } else {
            option = PersonCharacteristicsIdentifier.getChildrenBirthedInYear(activePartnership, currentDate.getYearDate());
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            NumberOfChildrenInYearNodeDouble n = (NumberOfChildrenInYearNodeDouble) addChild(new NumberOfChildrenInYearNodeDouble(option, this, 0.0, true));
            n.processPerson(person, currentDate);
        }

    }

    @Override
    public String getVariableName() {
        return "CIY";
    }

    @Override
    public void advanceCount() {
        makeChildren();
    }

    @Override
    public void calcCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        AgeNodeDouble aN = ((AgeNodeDouble) getAncestor(new AgeNodeDouble()));
        Integer age = aN.getOption().getValue();

        Integer order = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                                    getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption().getValue();
        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        double forNPeople = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getCount();

        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        SingleDeterminedCount sDC = (SingleDeterminedCount) getInputStats().getDeterminedCount(
                new BirthStatsKey(age, order, forNPeople, timePeriod, currentDate), null);

//        double numberOfMothers = sDC.getRawUncorrectedCount();
        double numberOfChildren = sDC.getRawUncorrectedCount();

        MultipleDeterminedCount mDc = (MultipleDeterminedCount) getInputStats().getDeterminedCount(
                new MultipleBirthStatsKey(age, numberOfChildren, timePeriod, currentDate), null);

        double numberOfMothers = mDc.getRawUncorrectedCount().getSumOfValues();

        NumberOfPreviousChildrenInAnyPartnershipNodeDouble parent = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) getParent();

        double numOfType = aN.sumOfNPCIAPDescendants(parent.getOption());

        double adjustment = parent.getCount() / numOfType;

        if(getOption() == ChildrenInYearOption.YES) {
            double v = numberOfMothers * adjustment;
            if(v > getParent().getCount() || Double.isNaN(v)) {
                v = getParent().getCount();
            }
            setCount(v);
        } else {
            double v = parent.getCount() - (numberOfMothers * adjustment);
            if(v < 0 || Double.isNaN(v)) {
                v = 0;
            }
            setCount(v);
        }

        if(getOption() == ChildrenInYearOption.NO || getCount().equals(0.0)) {
            addChild(0, getCount());
        } else {

//            LabeledValueSet<IntegerRange, Double> stat = mDc.getRawUncorrectedCount();
//
//            for (IntegerRange o : stat.getLabels()) {
//                if(!stat.get(o).equals(0.0)) {
//                    addChild(o.getValue(), stat.get(o) * (1 - adjustment));
//                }
//            }

            LabeledValueSet<IntegerRange, Double> stat = mDc.getRawUncorrectedCount().reproportion();

            for (IntegerRange o : stat.getLabels()) {
                if(!stat.get(o).equals(0.0)) {
                    addChild(o.getValue(), stat.get(o) * getCount());
                }
            }
        }

//        advanceCount();

    }

    @Override
    public void makeChildren() {

        if(getOption() == ChildrenInYearOption.NO || getCount().equals(0.0)) {
            addChild(0);
        } else {

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new MultipleBirthStatsKey(age, getCount(),
                            new CompoundTimeUnit(1, TimeUnit.YEAR), currentDate), null);

            LabeledValueSet<IntegerRange, Double> stat = mDC.getRawUncorrectedCount();

            for (IntegerRange o : stat.getLabels()) {
                if(!stat.get(o).equals(0.0)) {
                    addChild(o.getValue());
                }
            }
        }

    }
}
