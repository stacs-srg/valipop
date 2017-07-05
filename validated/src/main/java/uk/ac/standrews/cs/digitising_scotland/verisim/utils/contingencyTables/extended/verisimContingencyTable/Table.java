package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Table implements ContingencyTable {


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
    public Node addChild(Object childOption, int initCount) {
        return null;
    }

    @Override
    public Node addChild(Object childOption) {
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
