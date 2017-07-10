package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNodeDouble extends DoubleNode<IntegerRange, DiedOption> implements ControlChildrenNode {


    public AgeNodeDouble(IntegerRange age, SexNodeDouble parentNode, double initCount) {
        super(age, parentNode, initCount);

    }

    public AgeNodeDouble() {
        super();
    }

    @Override
    public Node<DiedOption, ?, Double, ?> makeChildInstance(DiedOption childOption, Double initCount) {
        return new DiedNodeDouble(childOption, this);
    }

    @Override
    public void makeChildren() {

        for(DiedOption o : DiedOption.values()) {
            addChild(o);
        }

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = getOption().getValue();

        Date calcCurrentDate = yob.advanceTime(age, TimeUnit.YEAR);

        incCountByOne();

        DiedOption option;

        if(person.diedInYear(calcCurrentDate.getYearDate())) {
            option = DiedOption.YES;
        } else {
            option = DiedOption.NO;
        }

        try {
            getChild(option).processPerson(person, calcCurrentDate);
        } catch(ChildNotFoundException e) {
            DiedNodeDouble n = (DiedNodeDouble) addChild(option);
            n.processPerson(person, calcCurrentDate);
            addDelayedTask(n);
        }
    }

}
