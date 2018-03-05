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
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.*;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.DeathStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeDouble extends DoubleNode<DiedOption, IntegerRange> implements ControlSelfNode, RunnableNode, ControlChildrenNode {



    public DiedNodeDouble(DiedOption option, AgeNodeDouble parentNode, boolean init) {
        super(option, parentNode);

        if(!init) {
            calcCount();

            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();
            if(age == 0) {
                makeChildren();
            }

        }
    }

    public DiedNodeDouble() {
        super();
    }

    @Override
    public void advanceCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        if(getOption() == DiedOption.NO && DateUtils.dateBefore(currentDate, getEndDate()) && getCount() > CTtree.NODE_MIN_COUNT) {


            SexNodeDouble sN = (SexNodeDouble) getAncestor(new SexNodeDouble());
            IntegerRange ageR = new IntegerRange(age + 1);

            try {
                sN.getChild(ageR).incCount(getCount());
            } catch (ChildNotFoundException e) {
                sN.addChild(ageR, getCount());
            }
        }

    }

    @Override
    public void calcCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        double forNPeople = getParent().getCount();
        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        char sex;

        SexOption sexOption = (SexOption) getAncestor(new SexNodeDouble()).getOption();

        if(sexOption == SexOption.MALE) {
            sex = 'm';
        } else {
            sex = 'f';
        }

        SingleDeterminedCount rDC = (SingleDeterminedCount) getInputStats()
                .getDeterminedCount(new DeathStatsKey(age, forNPeople, timePeriod, currentDate, sex), null);

        if(getOption() == DiedOption.YES) {
            setCount(rDC.getRawUncorrectedCount());
        } else {
            setCount(forNPeople - rDC.getRawUncorrectedCount());
        }

        advanceCount();

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        if(Character.toUpperCase(person.getSex()) == 'F') {
            ArrayList<IPartnershipExtended> partnershipsInYear = new ArrayList<>(
                    person.getPartnershipsActiveInYear(currentDate.getYearDate()));

            if(partnershipsInYear.size() == 0) {
                IntegerRange range = resolveToChildRange(0);
                try {
                    getChild(range).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(range).processPerson(person, currentDate);
                }
            } else if(partnershipsInYear.size() == 1) {
                IPartnershipExtended partnership = partnershipsInYear.remove(0);
                int numberOfChildren = partnership.getChildren().size();
                IntegerRange range = resolveToChildRange(numberOfChildren);
                try {
                    getChild(range).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(range).processPerson(person, currentDate);
                }
            } else {
                throw new UnsupportedOperationException("Woman in too many partnerships in year");
            }

        }

    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new PreviousNumberOfChildrenInPartnershipNodeDouble(childOption, this, initCount);
    }

    @Override
    public void runTask() {
        advanceCount();
    }

    @Override
    public void makeChildren() {

        SexOption sex = ((SexNodeDouble) getAncestor(new SexNodeDouble())).getOption();

        if(sex == SexOption.FEMALE) {

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();

            Collection<IntegerRange> ranges = getInputStats().getOrderedBirthRates(yob).getColumnLabels();

            for(IntegerRange iR : ranges) {
                if(iR.contains(0)) {
                    PreviousNumberOfChildrenInPartnershipNodeDouble pncip =
                            new PreviousNumberOfChildrenInPartnershipNodeDouble(iR, this, getCount());

                    addChild(pncip);

                    NumberOfPreviousChildrenInAnyPartnershipNodeDouble npciap =
                            (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.makeChildInstance(new IntegerRange(0), getCount());

                    pncip.addChild(npciap);


                    addDelayedTask(npciap);

                    break;
                }
            }

        }

    }

    @SuppressWarnings("Duplicates")
    public IntegerRange resolveToChildRange(Integer pncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(pncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getColumnLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(pncip)) {
                return o;
            }
        }

        if(pncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = getParent().toStringAL();
        s.add(getOption().toString());
        s.add(getCount().toString());
        return s;
    }

    public CTRow<Double> toCTRow() {
        CTRow r = getParent().toCTRow();

        if(r != null) {
            r.setVariable(getVariableName(), getOption().toString());
            r.setCount(getCount());
        }

        return r;
    }

    @Override
    public String getVariableName() {
        return "Died";
    }
}
