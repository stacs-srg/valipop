package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInYearNodeDouble extends DoubleNode<Integer, Integer> implements ControlSelfNode, ControlChildrenNode {

    public NumberOfChildrenInYearNodeDouble(Integer option, ChildrenInYearNodeDouble parentNode, Double initCount) {
        super(option, parentNode, initCount);
    }

    public NumberOfChildrenInYearNodeDouble() {
        super();
    }

    @Override
    public Node<Integer, ?, Double, ?> makeChildInstance(Integer childOption, Double initCount) {
        return new NumberOfChildrenInPartnershipNodeDouble(childOption, this, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        int prevChildren = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                                    getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();

        int childrenThisYear = ((NumberOfChildrenInYearNodeDouble)
                                                    getAncestor(new NumberOfChildrenInYearNodeDouble())).getOption();
        try {
            getChild(prevChildren + childrenThisYear).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(prevChildren + childrenThisYear).processPerson(person, currentDate);
        }
    }

    @Override
    public void advanceCount() {
        makeChildren();
    }

    @Override
    public void calcCount() {

        if(getOption() == 0) {
            setCount(getParent().getCount());
        } else {

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new MultipleBirthStatsKey(age, getCount(), new CompoundTimeUnit(1, TimeUnit.YEAR), currentDate));


            LabeledValueSet<IntegerRange, Double> stat = mDC.getRawUncorrectedCount();

            for(IntegerRange iR : stat.getLabels()) {
                if(iR.contains(getOption())) {
                    setCount(stat.get(iR));
                }
            }

        }

        advanceCount();

    }

    @Override
    public void makeChildren() {

        int numberOfPrevChildInPartnership = ((NumberOfChildrenInPartnershipNodeDouble)
                                            getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();
        int childrenInYear = getOption();

        int numberOfChildInPartnership = numberOfPrevChildInPartnership + childrenInYear;

        addChild(numberOfChildInPartnership, getCount());
    }
}
