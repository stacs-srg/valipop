package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PreviousNumberOfChildrenInPartnershipNodeInt extends IntNode<IntegerRange, IntegerRange> {

    public PreviousNumberOfChildrenInPartnershipNodeInt(IntegerRange option, DiedNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    public PreviousNumberOfChildrenInPartnershipNodeInt() {
        super();
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        Integer numberOfPrevChildrenInAnyPartnership = person.numberOfChildrenBirthedBeforeDate(currentDate.getYearDate());
        IntegerRange range = resolveToChildRange(numberOfPrevChildrenInAnyPartnership);
        try {
            getChild(range).processPerson(person, currentDate);
        } catch(ChildNotFoundException e) {
            addChild(range).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "PNCIP";
    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NumberOfPreviousChildrenInAnyPartnershipNodeInt(childOption, this, initCount);
    }

    private IntegerRange resolveToChildRange(Integer npciap) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(npciap)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
        Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);


        Collection<IntegerRange> birthOrders;
        try {
            birthOrders = getInputStats().getOrderedBirthRates(currentDate).getData(age).getLabels();
        } catch (InvalidRangeException e) {
            SelfCorrectingTwoDimensionDataDistribution data = getInputStats().getOrderedBirthRates(currentDate);
            birthOrders = data.getData(data.getSmallestLabel()).getLabels();
        }


        for(IntegerRange o : birthOrders) {
            if(o.contains(npciap)) {
                return o;
            }
        }

        throw new Error("Did not resolve any permissable ranges");
    }
}
