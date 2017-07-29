package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNodeDouble extends DoubleNode<IntegerRange, DiedOption> implements ControlChildrenNode, RunnableNode {

    Collection<IPersonExtended> people = new ArrayList<>();

    public AgeNodeDouble(IntegerRange age, SexNodeDouble parentNode, double initCount, boolean init) {
        super(age, parentNode, initCount);

        if(!init) {
            makeChildren();
        }
    }

    @Override
    public void incCount(Double byCount) {
        if(getChildren().size() != 0) {
            System.out.println("Issue with ordering");
        }

        setCount(getCount() + byCount);
    }

    public AgeNodeDouble() {
        super();
    }

    @Override
    public Node<DiedOption, ?, Double, ?> makeChildInstance(DiedOption childOption, Double initCount) {
        return new DiedNodeDouble(childOption, this, false);
    }

    @Override
    public void makeChildren() {

        for(DiedOption o : DiedOption.values()) {
            addChild(o);
        }

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        people.add(person);
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
//            DiedNodeDouble n = (DiedNodeDouble) addChild(option);

            DiedNodeDouble n = (DiedNodeDouble) addChild(new DiedNodeDouble(option, this, true));
            n.processPerson(person, calcCurrentDate);
            addDelayedTask(n);
        }
    }

    @Override
    public String getVariableName() {
        return "Age";
    }

    @Override
    public void runTask() {
        makeChildren();
    }

    public double sumOfNPCIAPDescendants(IntegerRange option) {

        double count = 0;

        for(Node c : getChildren()) {
            // c is of type died
            DiedNodeDouble cD = (DiedNodeDouble) c;
            for(Node gc : cD.getChildren()) {
                // gc is of type pncip
                PreviousNumberOfChildrenInPartnershipNodeDouble gcP = (PreviousNumberOfChildrenInPartnershipNodeDouble) gc;
                for(Node ggc : gcP.getChildren()) {
                    // ggc is of type NPCIAP
                    NumberOfPreviousChildrenInAnyPartnershipNodeDouble ggcN = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) ggc;

                    if(ggcN.getOption().hash() == option.hash()) {
                        count += ggcN.getCount();
                    }

                }
            }
        }
        return count;
    }
}
