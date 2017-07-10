package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeInt extends IntNode<DiedOption, Integer> {

    public DiedNodeInt(DiedOption option, AgeNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        if(Character.toUpperCase(person.getSex()) == 'F') {
            ArrayList<IPartnershipExtended> partnershipsInYear = new ArrayList<>(
                    person.getPartnershipsActiveInYear(currentDate.getYearDate()));

            if(partnershipsInYear.size() == 0) {
                try {
                    getChild(0).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(0).processPerson(person, currentDate);
                }
            } else if(partnershipsInYear.size() == 1) {
                IPartnershipExtended partnership = partnershipsInYear.remove(0);
                int numberOfChildren = partnership.getChildren().size();
                try {
                    getChild(numberOfChildren).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(numberOfChildren).processPerson(person, currentDate);
                }
            } else {
                throw new UnsupportedOperationException("Woman in too many partnerships in year");
            }

        }

    }

    @Override
    public Node<Integer, ?, Integer, ?> makeChildInstance(Integer childOption, Integer initCount) {
        return new PreviousNumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

}
