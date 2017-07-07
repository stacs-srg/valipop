package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes.SexNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeDouble extends DoubleNode<DiedOption, Integer> implements ControlSelfNode {

    public DiedNodeDouble(DiedOption option, AgeNodeDouble parentNode) {
        super(option, parentNode);
        calcCount();
    }

    @Override
    public void advanceCount() {

        if(getOption() == DiedOption.NO) {
            AgeNodeDouble aN = (AgeNodeDouble) getAncestor(new AgeNodeDouble());
            aN.getParent().addChild(new IntegerRange(aN.getOption().getValue() + 1), getCount());
        }

    }

    @Override
    public void calcCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        double forNPeople = getParent().getCount();
        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        char sex;

        SexOption sexOption = (SexOption) getAncestor(new SexNodeInt()).getOption();

        if(sexOption == SexOption.MALE) {
            sex = 'm';
        } else {
            sex = 'f';
        }

        SingleDeterminedCount rDC = (SingleDeterminedCount) getInputStats()
                .getDeterminedCount(new DeathStatsKey(age, forNPeople, timePeriod, currentDate, sex));

        if(getOption() == DiedOption.YES) {
            setCount(rDC.getRawUncorrectedCount());
        } else {
            setCount(forNPeople - rDC.getRawUncorrectedCount());
        }

        advanceCount();

    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        if(Character.toUpperCase(person.getSex()) == 'F') {
            ArrayList<IPartnershipExtended> partnershipsInYear = new ArrayList<>(
                    person.getPartnershipsActiveInYear(currentDate.getYearDate()));

            if(partnershipsInYear.size() == 0) {
                try {
                    getChild(0).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(0).processPerson(person, currentDate);
                }
            } else if(partnershipsInYear.size() == 1) {
                IPartnershipExtended partnership = partnershipsInYear.remove(0);
                int numberOfChildren = partnership.getChildren().size();
                try {
                    getChild(numberOfChildren).processPerson(person, currentDate);
                } catch (ChildNotFoundException e) {
                    addChild(numberOfChildren).processPerson(person, currentDate);
                }
            } else {
                throw new UnsupportedOperationException("Woman in too many partnerships in year");
            }

        }

    }

    @Override
    public Node<Integer, ?, Double, ?> makeChildInstance(Integer childOption, Double initCount) {
        // TODO construct
        return null;
    }
}
