package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.YOBNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNodeInt extends IntNode<IntegerRange, DiedOption> {

    public AgeNodeInt(IntegerRange option, SexNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    public AgeNodeInt() {
        super();
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

//        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
//        Integer age = getOption().getValue();
//
//        Date calcCurrentDate = yob.advanceTime(age, TimeUnit.YEAR);


        incCountByOne();

        DiedOption option;

        if(person.diedInYear(currentDate.getYearDate())) {
            option = DiedOption.YES;
        } else {
            option = DiedOption.NO;
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch(ChildNotFoundException e) {
            addChild(option).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<DiedOption, ?, Integer, ?> makeChildInstance(DiedOption childOption, Integer initCount) {
        return new DiedNodeInt(childOption, this, initCount);
    }
}
