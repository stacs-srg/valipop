package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SexOption;


import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class Node<Op, cOp, count extends Number, childCount extends Number> {

    private count count;
    private Op option;
    private Map<cOp, Node<cOp, ?, childCount, ?>> children = new HashMap<>();
    private Node<?, Op, ?, count> parent;

    public Node() {}

//    public Node(Op option, Node<?, Op, ?, count> parent) {
//        this.option = option;
//        this.parent = parent;
//    }

    public Node(Op option, Node<?, Op, ?, count> parent, count initCount) {
        this.option = option;
        this.parent = parent;
        this.count = initCount;
    }

    public abstract Node<cOp, ?, childCount, ?> addChild(cOp childOption, childCount initCount);
    public abstract Node<cOp, ?, childCount, ?> addChild(cOp childOption);
    public abstract void incCount(count byCount);
    public abstract void incCountByOne();
    public abstract void processPerson(IPersonExtended person, Date currentDate);

    public Node<cOp, ?, childCount, ?> addChild(Node<cOp, ?, childCount, ?> child) {
        children.put(child.getOption(), child);
        return child;
    }

    public Collection<Node> getLeafNodes() {

        Collection<Node> childNodes = new ArrayList<>();

        if(getChildren().size() == 0) {
            return Collections.singleton(this);
        } else {

            for (Node n : getChildren()) {
                childNodes.addAll(n.getLeafNodes());
            }
        }

        return childNodes;

    }



    public ArrayList<String> getVariableNamesAL() {
        ArrayList<String> s = getParent().getVariableNamesAL();
        s.add(getClass().getName());
        return s;
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = getParent().toStringAL();
        s.add(getOption().toString());
        return s;
    }

    public CTRow<count> toCTRow() {
        CTRow r = getParent().toCTRow();
        r.setVariable(getVariableName(), getOption().toString());
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

        if(n == null) {
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

//        nodeType.getClass().isInstance(this);

        if(nodeType.getClass().isInstance(this)) {
            return this;
        } else {
            return getParent().getAncestor(nodeType);
        }
    }

    public PopulationStatistics getInputStats() {
        return getAncestor(new CTtree()).getInputStats();
    }

    public Date getEndDate() {
        return getAncestor(new CTtree()).getEndDate();
    }

//    public String toString() {
//        String s = "";
//
//        s += getClass().getCanonicalName() + " ";
//        s += getOption().toString() + " --- ";
//        s += getAncestor(new YOBNodeDouble()).getOption().toString() + " @ ";
//        s += getAncestor(new AgeNodeDouble()).getOption().toString() + " ";
//
//        return s;
//    }

}
