package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.Table;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeDouble extends DoubleNode<DiedOption, Integer> implements ControlSelfNode, RunnableNode, ControlChildrenNode {



    public DiedNodeDouble(DiedOption option, AgeNodeDouble parentNode, boolean init) {
        super(option, parentNode);

        if(!init) {
            calcCount();

            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();
            if(age == 0) {
                makeChildren();
            }

        }
    }

    public DiedNodeDouble() {
        super();
    }

    @Override
    public void advanceCount() {

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        if(getOption() == DiedOption.NO && DateUtils.dateBefore(currentDate, getEndDate()) && getCount() > Table.NODE_MIN_COUNT) {


            SexNodeDouble sN = (SexNodeDouble) getAncestor(new SexNodeDouble());
            IntegerRange ageR = new IntegerRange(age + 1);

            try {
                sN.getChild(ageR).incCount(getCount());
            } catch (ChildNotFoundException e) {
                sN.addChild(ageR, getCount());
            }

//            sN.addChild(new IntegerRange(age + 1), getCount());


//            AgeNodeDouble aN = (AgeNodeDouble) getAncestor(new AgeNodeDouble());
//            aN.getParent().addChild(new IntegerRange(aN.getOption().getValue() + 1), getCount());
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

        SexOption sexOption = (SexOption) getAncestor(new SexNodeDouble()).getOption();

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

        incCountByOne();

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
        return new PreviousNumberOfChildrenInPartnershipNodeDouble(childOption, this, initCount);
    }

    @Override
    public void runTask() {
        advanceCount();
    }

    @Override
    public void makeChildren() {

        PreviousNumberOfChildrenInPartnershipNodeDouble pncip = new PreviousNumberOfChildrenInPartnershipNodeDouble(0, this, getCount());
        addChild(pncip);


        NumberOfPreviousChildrenInAnyPartnershipNodeDouble npciap = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.makeChildInstance(0, getCount());
        pncip.addChild(npciap);

        addDelayedTask(npciap);

    }
}
