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

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class IntNode<Op, cOp> extends Node<Op, cOp, Integer, Integer> {

    public IntNode(Op option, Node parentNode, int initCount) {
        super(option, parentNode, initCount);
    }

    public IntNode() {

    }

    public IntNode(Op option, Node parentNode) {
        super(option, parentNode, 0);
    }

    @Override
    public void incCount(Integer byCount) {
        setCount(getCount() + byCount);
    }

    @Override
    public void incCountByOne() {
        setCount(getCount() + 1);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Node<cOp, ?, Integer, ?> addChild(cOp childOption, Integer initCount) {

        Node<cOp, ?, Integer, ?> child;

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
    public Node<cOp, ?, Integer, ?> addChild(cOp childOption) {
        return addChild(childOption, 0);
    }

    public abstract Node<cOp, ?, Integer, ?> makeChildInstance(cOp childOption, Integer initCount);
}
