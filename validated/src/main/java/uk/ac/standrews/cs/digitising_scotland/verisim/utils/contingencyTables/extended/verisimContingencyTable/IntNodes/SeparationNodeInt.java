package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SeparationOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeInt extends IntNode<SeparationOption, IntegerRange> {

    public SeparationNodeInt(SeparationOption option, NumberOfChildrenInPartnershipNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        // TODO change this to resolve to the correct interger ranges

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        IntegerRange newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);
            newPartnerAge = new IntegerRange(partner.ageOnDate(activePartnership.getPartnershipDate()));
        }

        try {
            getChild(newPartnerAge).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(newPartnerAge).processPerson(person, currentDate);
        }

    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NewPartnerAgeNodeInt(childOption, this, initCount);
    }
}
