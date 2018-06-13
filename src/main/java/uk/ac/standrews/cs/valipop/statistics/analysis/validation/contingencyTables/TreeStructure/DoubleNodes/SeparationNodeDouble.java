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

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.AgeNodeInt;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.*;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SeparationOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;

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

        Integer newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);
            newPartnerAge = partner.ageOnDate(activePartnership.getPartnershipDate());
        }

        // check if the partner falls into one of the child ranges
        for(Node<IntegerRange, ?, Double, ?> node : getChildren()) {

            Boolean in;
            try {
                in = node.getOption().contains(newPartnerAge);
            } catch (InvalidRangeException e) {
                in = null;
            } catch (NullPointerException e) {
                // newP
                e.printStackTrace();
                System.out.println("---- NULL ----");
                System.out.println("NPA:");
                System.out.println(newPartnerAge);

                System.out.println("Node Children:");
                for(Node<IntegerRange, ?, Double, ?> nT : getChildren()) {
                    System.out.println(nT.getOption().toString());
                }
                System.out.println("Node Children End");

                System.out.println(person.toString());
                printDesent();
                System.out.println("---- END  ----");
                in = false;
            }

            // if partners age is in the considered range then process this person using this NPA range and return
            if (in != null && in){
                node.processPerson(person, currentDate);
                return;
            }

            // if in is null due to range being 'na' and there is no new partner (thus NPA == null) then process this person using the current NPA range (na)
            if(newPartnerAge == null && in == null) {
                node.processPerson(person, currentDate);
                return;
            }

        }

        // if we get here then the age range we want hasn't been created yet

        if(newPartnerAge == null) {
            // if no NPA then a 'na' range hasn't been created yet - so we create it
            addChild(
                    new NewPartnerAgeNodeDouble(new IntegerRange("na"), this, 0.0, true)
            ).processPerson(person, currentDate);
        } else {

            // this accessing of the statistical code isn't to calculate new values - we just use it to get the age
            // ranges from the stats tables
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            double numberOfFemales = getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

            // getting the age range labels
            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

            // finding which the persons partner is in and creating it
            for (IntegerRange o : options) {
                if (o.contains(newPartnerAge)) {
                    addChild(
                            new NewPartnerAgeNodeDouble(o, this, 0.0, true)
                    ).processPerson(person, currentDate);
                    return;
                }
            }
        }


//        incCountByOne();
//
//        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);
//
//        IntegerRange newPartnerAge = null;
//
//        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
//            IPersonExtended partner = activePartnership.getPartnerOf(person);
//            newPartnerAge = new IntegerRange(partner.ageOnDate(activePartnership.getPartnershipDate()));
//        }
//
//        if(newPartnerAge == null) {
//            newPartnerAge = new IntegerRange("na");
//        }
//
//        try {
//            getChild(newPartnerAge).processPerson(person, currentDate);
//        } catch (ChildNotFoundException e) {
//            addChild(new NewPartnerAgeNodeDouble(newPartnerAge, this, 0.0, true)).processPerson(person, currentDate);
//        }


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

        if(getOption() == SeparationOption.NA) {
            setCount(getParent().getCount());
        } else {

            IntegerRange numberOfChildrenInPartnership = ((NumberOfChildrenInPartnershipNodeDouble)
                    getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();

//            Integer numberOfChildrenInYear = ((NumberOfChildrenInYearNodeDouble)
//                    getAncestor(new NumberOfChildrenInYearNodeDouble())).getOption();

            if (numberOfChildrenInPartnership.getValue() == 0) {

                setCount(getParent().getCount());
            } else {

                double forNPeople = getParent().getCount();
                CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

                YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
                Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

                Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

                SingleDeterminedCount sDC = (SingleDeterminedCount) getInputStats().getDeterminedCount(
                        new SeparationStatsKey(
                                numberOfChildrenInPartnership.getValue(), age, forNPeople, timePeriod, currentDate), null);

                if (getOption() == SeparationOption.YES) {
                    setCount(sDC.getRawUncorrectedCount());
                } else {
                    setCount(forNPeople - sDC.getRawUncorrectedCount());
                }

            }
        }

        advanceCount();


    }

    @Override
    public void makeChildren() {

        // WHY DO WE NEVER CONSIDER THE SEPARATION STATUS IN THIS NODE? SURELY IT SHOULD IMPACT ON THE CHILDREN?
            // Answer: No, you need to remember that the New Partner Age is to do with the person who children have been
            // had with in the current year (NOT the next partner who will be moved onto). The fact this node comes
            // beneath the 'Separation' node in the tree is a misnoma, this age does not pertain to the partner who will
            // be moved onto post separation.
            // Also it is possible for there to partner ages below both the YES and NO separation nodes as you may
            // get a female who had no children entering the year (PNCIP = 0) who then has children (CIY = YES) who thus
            // has a new partner (thus NPA will be set) but who also separates from the partner in the same year
            // (Separation = YES).

        IntegerRange ncip = ((NumberOfChildrenInPartnershipNodeDouble)
                getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();

        if(ncip.getValue() == 0) { // i.e. no current partner and no children in year, therefore no NPA as no NP
            addChild(new IntegerRange("na"));
        } else {
            ChildrenInYearOption ciy = ((ChildrenInYearNodeDouble)
                    getAncestor(new ChildrenInYearNodeDouble())).getOption();

            if(ciy == ChildrenInYearOption.NO) { // if no children in year then by definition no new partner can exit - thus no NPA
                addChild(new IntegerRange("na"));
            } else {

                IntegerRange pncip = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                        getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();

                // at this point we know this partnership has borne children, paired with the knowledge of if there has
                // been any previous children in this partnership we can decide

                if(pncip.getValue() != 0) { // if it is an ongoing partnership - then no NP and thus no NPA
                    addChild(new IntegerRange("na"));
                } else {
                    // or is a new partnership - thus record NPA

                    YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
                    Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

                    Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

                    double numberOfFemales = getCount();
                    CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

                    MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                            .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

                    Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

                    for (IntegerRange o : options) {
                        addChild(o);
                    }
                }
            }
        }
    }

    @Override
    public void runTask() {
        calcCount();
    }
}
