package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SourceNodeDouble extends DoubleNode<SourceType, YearDate> {

    public SourceNodeDouble(SourceType option, Node parent) {
         super(option, parent);
    }

    @Override
    public Node<YearDate, ?, Double, ?> makeChildInstance(YearDate childOption, Double initCount) {
        return new YOBNodeDouble(childOption, this, initCount);
    }

    public void processPerson(IPersonExtended person, Date currentDate) {

        // increase own count
        incCountByOne();

        // pass person to appropriate child node
        YearDate yob = person.getBirthDate_ex().getYearDate();

        try {
            getChild(yob).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(yob).processPerson(person, currentDate);
        }
    }
}
