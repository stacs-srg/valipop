package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeNode extends Node<IntegerRange, DiedOption> implements RunnableNode {

    public AgeNode() {
        super();
    }

    public AgeNode(IntegerRange age, SexNode parentNode, double initCount, boolean incremental) {
        super(age, parentNode, initCount);

        if(!incremental) {
            makeChildren();
        }
    }

    @Override
    public void makeChildren() {

        for(DiedOption o : DiedOption.values()) {
            addChild(o);
        }

    }

    @Override
    public Node<DiedOption, ?> addChild(DiedOption childOption, double initCount) {
        // NA
        return null;
    }

    @Override
    public Node<DiedOption, ?> addChild(DiedOption childOption) {
        return addChild(new DiedNode(childOption, this));
    }

    @Override
    public void advanceCount() {
        // NA
    }

    @Override
    public void calcCount() {
        // NA
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
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
    public void runTask() {
        makeChildren();
    }
}
