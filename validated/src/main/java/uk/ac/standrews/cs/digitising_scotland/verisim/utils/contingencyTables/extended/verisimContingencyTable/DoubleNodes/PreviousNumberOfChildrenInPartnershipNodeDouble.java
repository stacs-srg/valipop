package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes.AgeNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes.YOBNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PreviousNumberOfChildrenInPartnershipNodeDouble extends DoubleNode<IntegerRange, IntegerRange> {


    public PreviousNumberOfChildrenInPartnershipNodeDouble(IntegerRange option, DiedNodeDouble parentNode, double initCount) {
        super(option, parentNode, initCount);
    }

    public PreviousNumberOfChildrenInPartnershipNodeDouble() {
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
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new NumberOfPreviousChildrenInAnyPartnershipNodeDouble(childOption, this, initCount);
    }

    @SuppressWarnings("Duplicates")
    public IntegerRange resolveToChildRange(Integer npciap) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(npciap)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

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
