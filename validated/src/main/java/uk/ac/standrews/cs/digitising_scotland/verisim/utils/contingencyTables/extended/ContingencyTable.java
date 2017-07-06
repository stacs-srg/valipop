package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended;

import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface ContingencyTable {

    Node getRootNode();

    void executeDelayedTasks();

    void addDelayedTask(RunnableNode node);

}
