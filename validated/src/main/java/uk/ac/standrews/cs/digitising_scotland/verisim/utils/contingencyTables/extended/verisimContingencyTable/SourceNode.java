package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SourceNode extends Node<SourceType, YearDate> {

    public SourceNode(SourceType option, Table parent) {
         super(option, parent);
    }

    public void makeChildren() {
        // NA
    }

    public void advanceCount() {
        // NA
    }

    public void calcCount() {
        // NA
    }

    public Node<YearDate, ?> addChild(YearDate childOption, double initCount) {

        YOBNode childNode;
        try {
            childNode = (YOBNode) getChild(childOption);
            childNode.incCount(initCount);
        } catch (ChildNotFoundException e) {
            childNode = new YOBNode(childOption, this, initCount);
            super.addChild(childNode);
        }

        return childNode;
    }

    @Override
    public Node<YearDate, ?> addChild(YearDate childOption) {
        return addChild(childOption, 0);
    }

    public void processPerson(IPersonExtended person, Date currentDate) {

        // increase own count
        incCount(1);

        // pass person to appropriate child node
        YearDate yob = person.getBirthDate_ex().getYearDate();

        try {
            getChild(yob).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(yob).processPerson(person, currentDate);
        }
    }
}
