package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.NumberOfChildrenInYearNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.PreviousNumberOfChildrenInPartnershipNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInYearNodeInt extends IntNode<Integer, IntegerRange> {

    public NumberOfChildrenInYearNodeInt(Integer option, ChildrenInYearNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    public NumberOfChildrenInYearNodeInt() {
        super();
    }


    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        int prevChildren = ((PreviousNumberOfChildrenInPartnershipNodeInt)
                getAncestor(new PreviousNumberOfChildrenInPartnershipNodeInt())).getOption().getValue();

        int childrenThisYear = ((NumberOfChildrenInYearNodeInt)
                getAncestor(new NumberOfChildrenInYearNodeInt())).getOption();

        int ncip = prevChildren + childrenThisYear;
        IntegerRange range = resolveToChildRange(ncip);

        try {
            getChild(range).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(range).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "NCIY";
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

    @SuppressWarnings("Duplicates")
    private IntegerRange resolveToChildRange(Integer ncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(ncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
        Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(ncip)) {
                return o;
            }
        }

        if(ncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }
}
