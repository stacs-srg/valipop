package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Table extends Node<String, SourceNode> implements ContingencyTable {
    public Table(String option, Node<?, String> parent) {
        super(option, parent);
    }

    public Table() {

    }


    // TODO write code

    @Override
    public Node getRootNode() {
        return null;
    }

    @Override
    public void executeDelayedTasks() {

    }

    @Override
    public void makeChildren() {

    }

    @Override
    public Node<SourceNode, ?> addChild(SourceNode childOption, double initCount) {
        return null;
    }

    @Override
    public Node<SourceNode, ?> addChild(SourceNode childOption) {
        return null;
    }

    @Override
    public void advanceCount() {

    }

    @Override
    public void calcCount() {

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

    }
}
