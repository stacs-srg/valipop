package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PreviousNumberOfChildrenInPartnershipNode extends Node<Integer, Integer> {


    public PreviousNumberOfChildrenInPartnershipNode(Integer option, DiedNode parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void makeChildren() {

    }

    @Override
    public Node<Integer, ?> addChild(Integer childOption, double initCount) {


        return null;
    }

    @Override
    public Node<Integer, ?> addChild(Integer childOption) {
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
