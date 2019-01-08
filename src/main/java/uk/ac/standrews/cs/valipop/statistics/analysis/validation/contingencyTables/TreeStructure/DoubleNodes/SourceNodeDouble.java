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
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTRowDouble;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SourceNodeDouble extends DoubleNode<SourceType, Year> {

    private Node parent;

    public SourceNodeDouble(SourceType option, Node parent) {
         super(option, parent);
         this.parent = parent;
    }

    public SourceNodeDouble() {
        super();
    }

    @Override
    public Node<Year, ?, Double, ?> makeChildInstance(Year childOption, Double initCount) {
        return new YOBNodeDouble(childOption, this, initCount);
    }

    public Node getAncestor(Node nodeType) {

        if(nodeType.getClass().isInstance(this)) {
            return this;
        } else if(nodeType.getClass().isInstance(parent)) {
            return parent;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void addDelayedTask(RunnableNode node) {
        parent.addDelayedTask(node);
    }

    public void processPerson(IPerson person, LocalDate currentDate) {

        // increase own count
        incCountByOne();

        // pass person to appropriate child node
        Year yob = Year.of(person.getBirthDate().getYear());

        try {
            getChild(yob).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(yob).processPerson(person, currentDate);
        }
    }

    public CTRow<Double> toCTRow() {
        CTRow r = new CTRowDouble();
        r.setVariable(getVariableName(), getOption().toString());
        return r;
    }

    @Override
    public String getVariableName() {
        return "Source";
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getOption().toString());
        return s;
    }

    public ArrayList<String> getVariableNamesAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getClass().getName());
        return s;
    }
}

