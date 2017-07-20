package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.ExactDate;
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
        try {
            return resolveChildNodeForAge(childOption.getValue());
        } catch (ChildNotFoundException e) {
            return new AgeNodeDouble(childOption, this, initCount, false);
        }
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
//        incCountByOne();

        int age = person.ageOnDate(new ExactDate(31, 12, currentDate.getYear() - 1));
        try {
            resolveChildNodeForAge(age).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new AgeNodeDouble(new IntegerRange(age), this, 0, true)).processPerson(person, currentDate);

//            addChild(new IntegerRange(age)).processPerson(person, currentDate);

        }
    }

    @Override
    public String getVariableName() {
        return "Sex";
    }

    public Node<IntegerRange, ?, Double, ?> resolveChildNodeForAge(Integer age) throws ChildNotFoundException {

        if(age != null) {
            for (Node<IntegerRange, ?, Double, ?> aN : getChildren()) {
                if (aN.getOption().contains(age)) {
                    return aN;
                }
            }
        }
        throw new ChildNotFoundException();
    }


    public Node<IntegerRange, ?, Double, ?> getChild(IntegerRange childOption) throws ChildNotFoundException {

        return resolveChildNodeForAge(childOption.getValue());

    }
}
