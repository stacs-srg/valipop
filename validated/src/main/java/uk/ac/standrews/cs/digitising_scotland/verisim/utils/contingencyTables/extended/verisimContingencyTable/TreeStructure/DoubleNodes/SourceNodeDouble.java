package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRowDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SourceType;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SourceNodeDouble extends DoubleNode<SourceType, YearDate> {

    private Node parent;

    public SourceNodeDouble(SourceType option, Node parent) {
         super(option, parent);
         this.parent = parent;
    }

    public SourceNodeDouble() {
        super();
    }

    @Override
    public Node<YearDate, ?, Double, ?> makeChildInstance(YearDate childOption, Double initCount) {
        return new YOBNodeDouble(childOption, this, initCount);
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

    public void addDelayedTask(RunnableNode node) {
        parent.addDelayedTask(node);
    }

    public void processPerson(IPersonExtended person, Date currentDate) {

        // increase own count
//        incCountByOne();

        // pass person to appropriate child node
        YearDate yob = person.getBirthDate_ex().getYearDate();

        try {
            getChild(yob).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(yob).processPerson(person, currentDate);
        }
    }

    public CTRow<Double> toCTRow() {
        CTRow r = new CTRowDouble();
        r.setVariable(getVariableName(), getOption().toString());
        return r;
    }

    @Override
    public String getVariableName() {
        return "Source";
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getOption().toString());
        return s;
    }

    public ArrayList<String> getVariableNamesAL() {
        ArrayList<String> s = new ArrayList<>();
        s.add(getClass().getName());
        return s;
    }
}

