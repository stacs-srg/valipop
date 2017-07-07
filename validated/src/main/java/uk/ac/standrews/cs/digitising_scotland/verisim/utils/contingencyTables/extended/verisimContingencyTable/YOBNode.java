package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNode extends Node<YearDate, SexOption> {



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
    public Node<SexOption, ?> addChild(SexOption childOption, int initCount) {

        SexNode childNode;
        try {
            childNode = (SexNode) getChild(childOption);
            childNode.incCount(initCount);
        } catch (ChildNotFoundException e) {
            childNode = new SexNode(childOption, this, initCount);
            super.addChild(childNode);
        }

        return childNode;

    }

    @Override
    public Node<SexOption, ?> addChild(SexOption childOption) {
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

}
