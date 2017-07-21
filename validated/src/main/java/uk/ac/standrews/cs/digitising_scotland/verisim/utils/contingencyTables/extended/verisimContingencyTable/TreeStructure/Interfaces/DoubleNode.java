package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class DoubleNode<Op, cOp> extends Node<Op, cOp, Double, Double> {

    public DoubleNode(Op option, Node parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    public DoubleNode(Op option, Node parentNode) {
        this(option, parentNode, 0);
    }

    public DoubleNode() {

    }

    @Override
    public void incCount(Double byCount) {
        setCount(getCount() + byCount);
    }

    @Override
    public void incCountByOne() {
        setCount(getCount() + 1);
    }

    public DoubleNode<?, Op> getParent() {
        return (DoubleNode<?, Op>) super.getParent();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Node<cOp, ?, Double, ?> addChild(cOp childOption, Double initCount) {

        Node<cOp, ?, Double, ?> child;

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
    public Node<cOp, ?, Double, ?> addChild(cOp childOption) {
        return addChild(childOption, 0.0);
    }

    public abstract Node<cOp, ?, Double, ?> makeChildInstance(cOp childOption, Double initCount);
}
