package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNode extends Node<DiedOption, Integer> {

    public DiedNode(DiedOption option, AgeNode parentNode) {
        super(option, parentNode);
        calcCount();
    }

    @Override
    public void makeChildren() {
        // NA
    }

    @Override
    public Node<Integer, ?> addChild(Integer childOption, int initCount) {

        PreviousNumberOfChildrenInPartnershipNode childNode;
        try {
            childNode = (PreviousNumberOfChildrenInPartnershipNode) getChild(childOption);
            childNode.incCount(initCount);
        } catch (ChildNotFoundException e) {
            childNode = new PreviousNumberOfChildrenInPartnershipNode(childOption, this, initCount);
            super.addChild(childNode);
        }

        return childNode;

    }

    // TODO keep writing code...

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
