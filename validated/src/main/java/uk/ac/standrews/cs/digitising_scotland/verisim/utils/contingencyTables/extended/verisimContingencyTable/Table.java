package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Table extends Node<String, SourceType, Number, Number> implements ContingencyTable {


    public Table() {

    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption, Number initCount) {
        return null;
    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption) {
        return null;
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
    public void incCount(Number byCount) {

    }

    @Override
    public void incCountByOne() {

    }

    @Override
    public void incChild(SourceType childOption, Number byCount) {

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

    }
}
