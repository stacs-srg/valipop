package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfPreviousChildrenInAnyPartnershipNodeInt extends IntNode<IntegerRange, ChildrenInYearOption> {

    public NumberOfPreviousChildrenInAnyPartnershipNodeInt(IntegerRange option, PreviousNumberOfChildrenInPartnershipNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
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
    public String getVariableName() {
        return "NPCIAP";
    }

    @Override
    public Node<ChildrenInYearOption, ?, Integer, ?> makeChildInstance(ChildrenInYearOption childOption, Integer initCount) {
        return new ChildrenInYearNodeInt(childOption, this, initCount);
    }
}
