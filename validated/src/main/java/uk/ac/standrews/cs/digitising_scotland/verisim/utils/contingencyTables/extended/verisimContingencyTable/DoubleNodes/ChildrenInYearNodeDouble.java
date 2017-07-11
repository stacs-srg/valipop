package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlChildrenNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ChildrenInYearNodeDouble extends DoubleNode<ChildrenInYearOption, Integer> implements ControlSelfNode, ControlChildrenNode {

    public ChildrenInYearNodeDouble(ChildrenInYearOption option, NumberOfPreviousChildrenInAnyPartnershipNodeDouble parentNode, Double initCount) {
        super(option, parentNode, initCount);
        calcCount();
    }

    public ChildrenInYearNodeDouble() {
        super();
    }

    @Override
    public Node<Integer, ?, Double, ?> makeChildInstance(Integer childOption, Double initCount) {
        return new NumberOfChildrenInYearNodeDouble(childOption, this, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Integer option;

        if(activePartnership == null){
            option = 0;
        } else {
            option = PersonCharacteristicsIdentifier.getChildrenBirthedInYear(activePartnership, currentDate.getYearDate());
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(option).processPerson(person, currentDate);
        }

    }

    @Override
    public void advanceCount() {
        makeChildren();
    }

    @Override
    public void calcCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Integer order = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                                    getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        double forNPeople = getParent().getCount();
        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        SingleDeterminedCount sDC = (SingleDeterminedCount) getInputStats().getDeterminedCount(
                new BirthStatsKey(age, order, forNPeople, timePeriod, currentDate));

        if(getOption() == ChildrenInYearOption.YES) {
            setCount(sDC.getRawUncorrectedCount());
        } else {
            setCount(forNPeople - sDC.getRawUncorrectedCount());
        }

        advanceCount();

    }

    @Override
    public void makeChildren() {

        if(getOption() == ChildrenInYearOption.NO) {
            addChild(0);
        } else {

            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new MultipleBirthStatsKey(age, getCount(), new CompoundTimeUnit(1, TimeUnit.YEAR), currentDate));

            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

            for (IntegerRange o : options) {
                addChild(o.getValue());
            }
        }

    }
}
