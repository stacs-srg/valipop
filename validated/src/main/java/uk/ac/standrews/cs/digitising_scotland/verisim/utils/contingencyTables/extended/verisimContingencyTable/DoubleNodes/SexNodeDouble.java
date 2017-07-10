package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SexNodeDouble extends DoubleNode<SexOption, IntegerRange> {

    public SexNodeDouble(SexOption option, YOBNodeDouble parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    public SexNodeDouble() {
        super();
    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new AgeNodeDouble(childOption, this, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        int age = person.ageOnDate(currentDate);
        try {
            resolveChildNodeForAge(age).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new IntegerRange(age)).processPerson(person, currentDate);

        }
    }

    public Node<IntegerRange, ?, ?, ?> resolveChildNodeForAge(int age) throws ChildNotFoundException {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(age)) {
                return aN;
            }
        }

        throw new ChildNotFoundException();
    }

}
