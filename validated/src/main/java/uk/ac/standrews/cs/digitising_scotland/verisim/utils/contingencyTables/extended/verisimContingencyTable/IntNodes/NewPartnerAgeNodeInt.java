package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NewPartnerAgeNodeInt extends IntNode<IntegerRange, String> {

    public NewPartnerAgeNodeInt(IntegerRange option, SeparationNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();
    }

    @Override
    public Node<String, ?, Integer, ?> makeChildInstance(String childOption, Integer initCount) {
        return null;
    }
}
