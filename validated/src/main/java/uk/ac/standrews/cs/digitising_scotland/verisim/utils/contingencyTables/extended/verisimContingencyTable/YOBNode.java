package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNode extends Node<YearDate, IntegerRange> {



    public YOBNode(YearDate option, SourceNode parentNode, int initCount) {
        super(option, parentNode, initCount);
    }

    public YOBNode() {

    }

    @Override
    public void makeChildren() {
        // NA
    }

    @Override
    public Node<IntegerRange, ?> addChild(IntegerRange childOption, int initCount) {

        AgeNode childNode;
        try {
            childNode = (AgeNode) getChild(childOption);
            childNode.incCount(initCount);
        } catch (ChildNotFoundException e) {
            childNode = new AgeNode(childOption, this, initCount, false);
            super.addChild(childNode);
        }

        return childNode;

    }

    @Override
    public Node<IntegerRange, ?> addChild(IntegerRange childOption) {
        return addChild(childOption);
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
        incCount(1);

        int age = person.ageOnDate(currentDate);
        try {
            resolveChildNodeForAge(age).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(new IntegerRange(age)).processPerson(person, currentDate);
        }

    }

    private Node<IntegerRange, ?> resolveChildNodeForAge(int age) throws ChildNotFoundException {

        for(Node<IntegerRange, ?> aN : getChildren()) {
            if(aN.getOption().contains(age)) {
                return aN;
            }
        }

        throw new ChildNotFoundException();
    }
}
