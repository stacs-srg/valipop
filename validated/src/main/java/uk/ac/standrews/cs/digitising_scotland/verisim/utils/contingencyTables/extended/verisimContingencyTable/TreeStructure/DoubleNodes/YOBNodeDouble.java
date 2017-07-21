package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SexOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNodeDouble extends DoubleNode<YearDate, SexOption> implements ControlChildrenNode {


    public YOBNodeDouble(YearDate childOption, SourceNodeDouble parentNode, Double initCount) {
        super(childOption, parentNode, initCount);
        makeChildren();
    }

    public YOBNodeDouble() {
        super();
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

//        incCountByOne();

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

    @Override
    public String getVariableName() {
        return "YOB";
    }

    public CTRow<Double> toCTRow() {
        CTRow r = getParent().toCTRow();
        r.setVariable(getVariableName(), Integer.toString(getOption().getYear()));
        return r;
    }

    @Override
    public Node<SexOption, ?, Double, ?> makeChildInstance(SexOption childOption, Double initCount) {
        return new SexNodeDouble(childOption, this, initCount);
    }

    @Override
    public void makeChildren() {

        addChild(SexOption.MALE);
        addChild(SexOption.FEMALE);

    }
}
