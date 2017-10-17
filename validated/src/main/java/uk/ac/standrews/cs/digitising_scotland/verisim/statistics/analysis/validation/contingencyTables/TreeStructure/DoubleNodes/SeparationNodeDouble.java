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
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SeparationOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeDouble extends DoubleNode<SeparationOption, IntegerRange> implements ControlSelfNode, ControlChildrenNode, RunnableNode {

    public SeparationNodeDouble(SeparationOption option, NumberOfChildrenInPartnershipNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
            calcCount();
            makeChildren();
        }

    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new NewPartnerAgeNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        IntegerRange newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);
            newPartnerAge = new IntegerRange(partner.ageOnDate(activePartnership.getPartnershipDate()));
        }

        try {
            getChild(newPartnerAge).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new NewPartnerAgeNodeDouble(newPartnerAge, this, 0.0, true)).processPerson(person, currentDate);
        }


    }

    @Override
    public String getVariableName() {
        return "Separated";
    }

    @Override
    public void advanceCount() {

        DiedNodeDouble diedN = (DiedNodeDouble) getAncestor(new DiedNodeDouble());
        DiedOption died = diedN.getOption();
        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        if(died == DiedOption.NO && DateUtils.dateBefore(currentDate, getEndDate()) && diedN.getCount() > CTtree.NODE_MIN_COUNT) {

            SexNodeDouble s = (SexNodeDouble) getAncestor(new SexNodeDouble());
//            s.incCount(getCount());

            AgeNodeDouble a;
            try {
                a = (AgeNodeDouble) s.resolveChildNodeForAge(age + 1);
            } catch (ChildNotFoundException e) {
                throw new Error("Age Node should have already been created");
            }

//            a.incCount(getCount());

//            DiedOption died = ((DiedOption) getAncestor(new DiedNodeDouble()).getOption());

            // Split count between branches

            for(Node<DiedOption, ?, Double, ?> n : a.getChildren()) {

                DiedNodeDouble d = (DiedNodeDouble) n;

                double partOfCount = getCount() * d.getCount() / a.getCount();

                IntegerRange prevNumberOfChildrenInPartnership = ((NumberOfChildrenInPartnershipNodeDouble) getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();

                // Move over to correct IntegerRange object with the same value at age + 1
                prevNumberOfChildrenInPartnership = d.resolveToChildRange(prevNumberOfChildrenInPartnership.getValue());

                PreviousNumberOfChildrenInPartnershipNodeDouble pncip;

                try {
                    pncip = (PreviousNumberOfChildrenInPartnershipNodeDouble) d.getChild(prevNumberOfChildrenInPartnership);
                } catch (ChildNotFoundException e) {
                    pncip = (PreviousNumberOfChildrenInPartnershipNodeDouble) d.addChild(prevNumberOfChildrenInPartnership);
                }

                pncip.incCount(partOfCount);

                IntegerRange numberOfPrevChildrenInAnyPartnership = ((NumberOfPreviousChildrenInAnyPartnershipNodeDouble) getAncestor(new NumberOfPreviousChildrenInAnyPartnershipNodeDouble())).getOption();


                int childrenInYear = ((NumberOfChildrenInYearNodeDouble) getAncestor(new NumberOfChildrenInYearNodeDouble())).getOption();

                int numberOfChildrenInAnyPartnership = numberOfPrevChildrenInAnyPartnership.getValue() + childrenInYear;

                NumberOfPreviousChildrenInAnyPartnershipNodeDouble nciap;

                IntegerRange rangeNCIAP = pncip.resolveToChildRange(numberOfChildrenInAnyPartnership);

                try {
                    nciap = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.getChild(rangeNCIAP);
                } catch (ChildNotFoundException e) {
                    nciap = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.addChild(rangeNCIAP);
                    addDelayedTask(nciap);
                }

                nciap.incCount(partOfCount);
            }

        }

    }

    @Override
    public void calcCount() {

        IntegerRange numberOfChildrenInPartnership = ((NumberOfChildrenInPartnershipNodeDouble)
                getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();


        if(numberOfChildrenInPartnership.getValue() == 0) {
            setCount(getParent().getCount());
        } else {

            double forNPeople = getParent().getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            SingleDeterminedCount sDC = (SingleDeterminedCount) getInputStats().getDeterminedCount(
                    new SeparationStatsKey(
                            numberOfChildrenInPartnership.getValue(), forNPeople, timePeriod, currentDate), null);

            if (getOption() == SeparationOption.YES) {
                setCount(sDC.getRawUncorrectedCount());
            } else {
                setCount(forNPeople - sDC.getRawUncorrectedCount());
            }

        }

        advanceCount();


    }

    @Override
    public void makeChildren() {

        IntegerRange ncip = ((NumberOfChildrenInPartnershipNodeDouble)
                getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();

        if(ncip.getValue() == 0) {
            addChild(new IntegerRange("na"));
        } else {
            ChildrenInYearOption ciy = ((ChildrenInYearNodeDouble)
                    getAncestor(new ChildrenInYearNodeDouble())).getOption();

            if(ciy == ChildrenInYearOption.NO) {
                addChild(new IntegerRange("na"));
            } else {
                YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
                Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

                if(age == 16) {
                    System.out.print("");
                }

                Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

                double numberOfFemales = getCount();
                CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

                MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                        .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

                Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

                for(IntegerRange o : options) {
                    addChild(o);
                }
            }
        }

//        if(getOption() == SeparationOption.YES) {
//
//            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
//            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();
//
//            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);
//
//            double numberOfFemales = getCount();
//            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);
//
//            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
//                            .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate));
//
//            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();
//
//            for(IntegerRange o : options) {
//                addChild(o);
//            }
//
//        } else {
//            addChild(null, getCount());
//        }

    }

    @Override
    public void runTask() {
        calcCount();
    }
}
