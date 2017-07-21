package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRowInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.CTtree;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SourceType;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SourceNodeInt extends IntNode<SourceType, YearDate> {

    private Node parent;

    public SourceNodeInt(SourceType option, CTtree parent) {
        super(option, parent);
        this.parent = parent;
    }

    public Node getAncestor(Node nodeType) {

        if(nodeType.getClass().isInstance(this)) {
            return this;
        } else if(nodeType.getClass().isInstance(parent)) {
            return parent;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Node<YearDate, ?, Integer, ?> makeChildInstance(YearDate childOption, Integer initCount) {
        return new YOBNodeInt(childOption, this, initCount);
    }

    public void addDelayedTask(RunnableNode node) {
        parent.addDelayedTask(node);
    }

    public void processPerson(IPersonExtended person, Date currentDate) {

        // increase own count
        incCountByOne();

        // pass person to appropriate child node
        YearDate yob = person.getBirthDate_ex().getYearDate();

        try {
            getChild(yob).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(yob).processPerson(person, currentDate);
        }
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getOption().toString());
        return s;
    }

    public CTRow<Integer> toCTRow() {
        CTRow r = new CTRowInt();
        r.setVariable(getVariableName(), getOption().toString());
        return r;
    }

    @Override
    public String getVariableName() {
        return "Source";
    }

    public ArrayList<String> getVariableNamesAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getClass().getName());
        return s;
    }
}
