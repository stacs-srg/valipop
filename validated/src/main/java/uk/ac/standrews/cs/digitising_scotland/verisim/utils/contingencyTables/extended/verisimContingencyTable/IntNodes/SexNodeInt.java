package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SexNodeInt extends IntNode<SexOption, IntegerRange> {

    public SexNodeInt(SexOption option, YOBNodeInt parentNode, int initCount) {
        super(option, parentNode, initCount);
    }

    public SexNodeInt() {
        super();
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new AgeNodeInt(childOption, this, initCount);
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCount(1);

        int age = person.ageOnDate(currentDate);
        try {
            resolveChildNodeForAge(age).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new IntegerRange(age)).processPerson(person, currentDate);

        }
    }

    private Node<IntegerRange, ?, ?, ?> resolveChildNodeForAge(int age) throws ChildNotFoundException {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(age)) {
                return aN;
            }
        }

        throw new ChildNotFoundException();
    }
}
