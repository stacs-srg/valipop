package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PreviousNumberOfChildrenInPartnershipNodeInt extends IntNode<Integer, Integer> {

    public PreviousNumberOfChildrenInPartnershipNodeInt(Integer option, DiedNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    public PreviousNumberOfChildrenInPartnershipNodeInt() {
        super();
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        Integer numberOfPrevChildrenInAnyPartnership = person.numberOfChildrenBirthedBeforeDate(currentDate.getYearDate());
        try {
            getChild(numberOfPrevChildrenInAnyPartnership).processPerson(person, currentDate);
        } catch(ChildNotFoundException e) {
            addChild(numberOfPrevChildrenInAnyPartnership).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<Integer, ?, Integer, ?> makeChildInstance(Integer childOption, Integer initCount) {
        return new NumberOfPreviousChildrenInAnyPartnershipNodeInt(childOption, this, initCount);
    }
}
