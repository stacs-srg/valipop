package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;


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
