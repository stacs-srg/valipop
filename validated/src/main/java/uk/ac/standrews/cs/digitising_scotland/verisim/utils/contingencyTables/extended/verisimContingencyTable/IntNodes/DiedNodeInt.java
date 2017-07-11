package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeInt extends IntNode<DiedOption, Integer> {

    public DiedNodeInt(DiedOption option, AgeNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        if(Character.toUpperCase(person.getSex()) == 'F') {

            IPartnershipExtended partnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);
            int numberOfChildren;

            if(partnership == null) {
                numberOfChildren = 0;
            } else {
                numberOfChildren = PersonCharacteristicsIdentifier.getChildrenBirthedBeforeDate(partnership, currentDate);
            }

            try {
                getChild(numberOfChildren).processPerson(person, currentDate);
            } catch (ChildNotFoundException e) {
                addChild(numberOfChildren).processPerson(person, currentDate);
            }

        }

    }

    @Override
    public Node<Integer, ?, Integer, ?> makeChildInstance(Integer childOption, Integer initCount) {
        return new PreviousNumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

}
