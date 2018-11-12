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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces;

import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.CTtree;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class Node<Op, cOp, count extends Number, childCount extends Number> {

    private count count;
    private Op option;
    private Map<cOp, Node<cOp, ?, childCount, ?>> children = new TreeMap<>();
    private Node<?, Op, ?, count> parent;

    public Node() {
    }

    public Node(Op option, Node<?, Op, ?, count> parent, count initCount) {
        this.option = option;
        this.parent = parent;
        this.count = initCount;
    }

    public abstract Node<cOp, ?, childCount, ?> addChild(cOp childOption, childCount initCount);

    public abstract Node<cOp, ?, childCount, ?> addChild(cOp childOption);

    public abstract void incCount(count byCount);

    public abstract void incCountByOne();

    public abstract void processPerson(IPerson person, uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date currentDate);

    public Node<cOp, ?, childCount, ?> addChild(Node<cOp, ?, childCount, ?> child) {
        children.put(child.getOption(), child);
        return child;
    }

    public Collection<Node> getLeafNodes() {

        Collection<Node> childNodes = new ArrayList<>();

        if (getChildren().size() == 0) {
            return Collections.singleton(this);
        } else {

            for (Node n : getChildren()) {
                childNodes.addAll(n.getLeafNodes());
            }
        }

        return childNodes;
    }

    public List<String> getVariableNamesAL() {
        List<String> s = getParent().getVariableNamesAL();
        s.add(getClass().getName());
        return s;
    }

    public List<String> toStringAL() {
        List<String> s = getParent().toStringAL();
        s.add(getOption().toString());
        return s;
    }

    public CTRow<count> toCTRow() {
        CTRow r = getParent().toCTRow();
        if (r != null) {
            r.setVariable(getVariableName(), getOption().toString());
        }
        return r;
    }

    public abstract String getVariableName();

    public void setCount(count count) {
        this.count = count;
    }

    public Op getOption() {
        return option;
    }

    public count getCount() {
        return count;
    }

    public Collection<Node<cOp, ?, childCount, ?>> getChildren() {
        return children.values();
    }

    public Node<cOp, ?, childCount, ?> getChild(cOp childOption) throws ChildNotFoundException {
        Node<cOp, ?, childCount, ?> n = children.get(childOption);

        if (n == null) {
            throw new ChildNotFoundException();
        }

        return n;
    }

    public Node<?, Op, ?, count> getParent() {
        return parent;
    }

    public void addDelayedTask(RunnableNode node) {
        getParent().addDelayedTask(node);
    }

    public Node getAncestor(Node nodeType) {

        if (nodeType.getClass().isInstance(this)) {
            return this;
        } else {
            return getParent().getAncestor(nodeType);
        }
    }

    public PopulationStatistics getInputStats() {
        return getAncestor(new CTtree()).getInputStats();
    }

    public Date getStartDate() {
        return getAncestor(new CTtree()).getStartDate();
    }

    public Date getEndDate() {
        return getAncestor(new CTtree()).getEndDate();
    }

    public int printDescent() {

        int depth = 0;

        Node p = getParent();
        if (p != null) {
            depth = p.printDescent() + 1;
        }

        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }

        System.out.print(this.getClass().getSimpleName());

        if (option == null) {
            System.out.print(" | Op: null");
        } else {
            System.out.print(" | Op: " + option.toString());
        }

        if (count == null) {
            System.out.print(" | Count: null");
        } else {
            System.out.print(" | Count: " + count);
        }

        System.out.println();

        return depth;
    }
}
