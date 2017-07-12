package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SeparationOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInPartnershipNodeDouble extends DoubleNode<Integer, SeparationOption> implements ControlChildrenNode {

    public NumberOfChildrenInPartnershipNodeDouble(Integer option, NumberOfChildrenInYearNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
            makeChildren();
        }
    }

    public NumberOfChildrenInPartnershipNodeDouble() {
        super();
    }

    @Override
    public Node<SeparationOption, ?, Double, ?> makeChildInstance(SeparationOption childOption, Double initCount) {
        return new SeparationNodeDouble(childOption, this, initCount, false);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Boolean toSeparate = PersonCharacteristicsIdentifier.toSeparate(activePartnership, currentDate.getYearDate());

        SeparationOption option;

        if(toSeparate == null) {
            option = SeparationOption.NA;
        } else if(toSeparate) {
            option = SeparationOption.YES;
        } else {
            option = SeparationOption.NO;
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
//            SeparationNodeDouble n = (SeparationNodeDouble) addChild(option);

            SeparationNodeDouble n = (SeparationNodeDouble) addChild(new SeparationNodeDouble(option, this, 0.0, true));
            n.processPerson(person, currentDate);
            addDelayedTask(n);
        }

    }

    @Override
    public void makeChildren() {

        if(getOption() == 0) {
            addChild(SeparationOption.NA, getCount());
        } else {
            addChild(SeparationOption.YES);
            addChild(SeparationOption.NO);
        }

    }
}
