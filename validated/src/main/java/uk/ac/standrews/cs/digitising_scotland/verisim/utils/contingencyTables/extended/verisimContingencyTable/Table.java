package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import org.apache.bcel.generic.POP;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Table extends Node<String,SourceType> implements ContingencyTable {

    private PopulationStatistics inputStats;


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
    public Node<SourceType, ?> addChild(SourceType childOption, double initCount) {
        return null;
    }

    @Override
    public Node<SourceType, ?> addChild(SourceType childOption) {
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

    @Override
    public void addDelayedTask(RunnableNode node) {

    }

    public PopulationStatistics getInputStats() {
        return inputStats;
    }
}
