package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.SourceNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YOBNodeInt extends IntNode<YearDate, SexOption> {

    public YOBNodeInt(YearDate option, SourceNodeDouble parentNode, int initCount) {
        super(option, parentNode, initCount);
    }

    public YOBNodeInt() {
        super();

    }

    @Override
    public Node<SexOption, ?, Integer, ?> makeChildInstance(SexOption childOption, Integer initCount) {
        return new SexNodeInt(childOption, this, initCount);
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

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
