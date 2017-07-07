package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;

import java.util.Collection;


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
        super(option, parentNode);
    }

    @Override
    public void incCount(Integer byCount) {
        setCount(getCount() + byCount);
    }

    @Override
    public void incCountByOne() {
        setCount(getCount() + 1);
    }

    @Override
    public void incChild(cOp childOption, Integer byCount) {

        try {
            getChild(childOption).incCount(byCount);
        } catch (ChildNotFoundException e) {
            addChild(childOption, byCount);
        }

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
