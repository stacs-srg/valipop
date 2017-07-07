package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ContingencyTable {

    Node getRootNode();

    void executeDelayedTasks();

}
