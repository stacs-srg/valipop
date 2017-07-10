package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.NumberOfChildrenInYearNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.PreviousNumberOfChildrenInPartnershipNodeDouble;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInYearNodeInt extends IntNode<Integer, Integer> {

    public NumberOfChildrenInYearNodeInt(Integer option, ChildrenInYearNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        int prevChildren = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();

        int childrenThisYear = ((NumberOfChildrenInYearNodeDouble)
                getAncestor(new NumberOfChildrenInYearNodeDouble())).getOption();
        try {
            getChild(prevChildren + childrenThisYear).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(prevChildren + childrenThisYear).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<Integer, ?, Integer, ?> makeChildInstance(Integer childOption, Integer initCount) {
        return new NumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }
}
