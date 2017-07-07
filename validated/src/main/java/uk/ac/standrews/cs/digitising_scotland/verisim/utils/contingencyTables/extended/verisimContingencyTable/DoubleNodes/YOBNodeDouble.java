package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNodeDouble extends DoubleNode<YearDate, SexOption> {


    public YOBNodeDouble(YearDate childOption, SourceNodeDouble parentNode, Double initCount) {
        super(childOption, parentNode, initCount);
    }

    public YOBNodeDouble() {
        super();
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        SexOption sex;

        if(Character.toUpperCase(person.getSex()) == 'M') {
            sex = SexOption.MALE;
        } else {
            sex = SexOption.FEMALE;
        }

        try {
            getChild(sex).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(sex).processPerson(person, currentDate);
        }

    }

    @Override
    public Node<SexOption, ?, Double, ?> makeChildInstance(SexOption childOption, Double initCount) {
        return new SexNodeDouble(childOption, getParent(), initCount);
    }
}
