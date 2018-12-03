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

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ControlSelfNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NewPartnerAgeNodeDouble extends DoubleNode<IntegerRange, String> implements ControlSelfNode {

    public NewPartnerAgeNodeDouble(IntegerRange option, SeparationNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if (!init) {
            calcCount();
        }
    }

    @Override
    public Node<String, ?, Double, ?> makeChildInstance(String childOption, Double initCount) {
        return null;
    }

    @Override
    public void processPerson(IPerson person, LocalDate currentDate) {
        incCountByOne();
    }

    @Override
    public void advanceCount() {

    }

    @Override
    public void calcCount() {

        if (getOption().getValue() == null) {
            setCount(getParent().getCount());
        } else {
            
            Year yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            int age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            LocalDate currentDate = getDateAtAge(yob, age);

            double numberOfFemales = getParent().getCount();
            Period timePeriod = Period.ofYears(1);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats().getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

            if (getOption().getValue() == null) {

                // TODO ???

                if (getParent().getCount() > 20) {
                    System.out.print("");
                }

                setCount(getParent().getCount());
            } else {

                if (mDC.getRawUncorrectedCount().get(getOption()) > 20) {
                    System.out.print("");
                }

                setCount(mDC.getRawUncorrectedCount().get(getOption()));
            }
        }
    }

    public List<String> toStringAL() {
        List<String> s = getParent().toStringAL();
        if (getOption() == null) {
            s.add("na");
        } else {
            s.add(getOption().toString());
        }
        s.add(getCount().toString());
        return s;
    }

    public CTRow<Double> toCTRow() {
        CTRow r = getParent().toCTRow();

        if (r != null) {
            if (getOption() == null) {
                r.setVariable(getVariableName(), "na");
            } else {
                r.setVariable(getVariableName(), getOption().toString());
            }

            r.setCount(getCount());
        }

        return r;
    }

    @Override
    public String getVariableName() {
        return "NPA";
    }
}
