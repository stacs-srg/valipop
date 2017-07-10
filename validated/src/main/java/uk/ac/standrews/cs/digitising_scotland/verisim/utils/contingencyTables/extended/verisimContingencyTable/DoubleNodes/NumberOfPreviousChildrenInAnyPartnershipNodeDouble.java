package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.ChildrenInYearOption;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfPreviousChildrenInAnyPartnershipNodeDouble extends DoubleNode<Integer, ChildrenInYearOption> implements RunnableNode, ControlChildrenNode {

    public NumberOfPreviousChildrenInAnyPartnershipNodeDouble(Integer option, PreviousNumberOfChildrenInPartnershipNodeDouble parentNode, Double initCount) {
        super(option, parentNode, initCount);
    }

    public NumberOfPreviousChildrenInAnyPartnershipNodeDouble() {
        super();
    }

    @Override
    public Node<ChildrenInYearOption, ?, Double, ?> makeChildInstance(ChildrenInYearOption childOption, Double initCount) {
        return new ChildrenInYearNodeDouble(childOption, this, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        ChildrenInYearOption option;


        if(activePartnership == null){
            option = ChildrenInYearOption.NO;
        } else {
            int n = PersonCharacteristicsIdentifier.getChildrenBirthedInYear(activePartnership, currentDate.getYearDate());
            if(n == 0) {
                option = ChildrenInYearOption.NO;
            } else {
                option = ChildrenInYearOption.YES;
            }
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(option).processPerson(person, currentDate);
        }

    }

    @Override
    public void runTask() {
        makeChildren();
    }

    @Override
    public void makeChildren() {

        addChild(ChildrenInYearOption.YES);
        addChild(ChildrenInYearOption.NO);

    }
}
