package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.SeparationStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.ChildrenInYearOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SeparationOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeDouble extends DoubleNode<SeparationOption, IntegerRange> implements ControlSelfNode, ControlChildrenNode, RunnableNode {

    public SeparationNodeDouble(SeparationOption option, NumberOfChildrenInPartnershipNodeDouble parentNode, Double initCount) {
        super(option, parentNode, initCount);

    }

    @Override
    public Node<IntegerRange, ?, Double, ?> makeChildInstance(IntegerRange childOption, Double initCount) {
        return new NewPartnerAgeNodeDouble(childOption, this, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        IntegerRange newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);
            newPartnerAge = new IntegerRange(partner.ageOnDate(activePartnership.getPartnershipDate()));
        }

        try {
            getChild(newPartnerAge).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(newPartnerAge).processPerson(person, currentDate);
        }


    }

    @Override
    public void advanceCount() {

        // year passover
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        SexNodeDouble s = (SexNodeDouble) getAncestor(new SexNodeDouble());
        s.incCount(getCount());

        AgeNodeDouble a;
        try {
            a = (AgeNodeDouble) s.resolveChildNodeForAge(age + 1);
        } catch (ChildNotFoundException e) {
            throw new Error("Age Node should have already been created");
        }

        a.incCount(getCount());

        DiedOption died = ((DiedOption) getAncestor(new DiedNodeDouble()).getOption());

        DiedNodeDouble d;

        try {
            d = (DiedNodeDouble) a.getChild(died);
        } catch (ChildNotFoundException e) {
            throw new Error("Died Node should have already been created");
        }

        d.incCount(getCount());

        int prevNumberOfChidrenInPartnership = ((PreviousNumberOfChildrenInPartnershipNodeDouble) getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();


        PreviousNumberOfChildrenInPartnershipNodeDouble pncip;

        try {
            pncip = (PreviousNumberOfChildrenInPartnershipNodeDouble) d.getChild(prevNumberOfChidrenInPartnership);
        } catch (ChildNotFoundException e) {
            pncip = (PreviousNumberOfChildrenInPartnershipNodeDouble) d.addChild(prevNumberOfChidrenInPartnership);
        }

        pncip.incCount(getCount());

        int numberOfChildrenInAnyPartnership = ((NumberOfPreviousChildrenInAnyPartnershipNodeDouble) getAncestor(new NumberOfPreviousChildrenInAnyPartnershipNodeDouble())).getOption();

        NumberOfPreviousChildrenInAnyPartnershipNodeDouble nciap;

        try {
            nciap = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.getChild(numberOfChildrenInAnyPartnership);
        } catch (ChildNotFoundException e) {
            nciap = (NumberOfPreviousChildrenInAnyPartnershipNodeDouble) pncip.addChild(numberOfChildrenInAnyPartnership);
            addDelayedTask(nciap);
        }

        nciap.incCount(getCount());


    }

    @Override
    public void calcCount() {

        int numberOfChildren = ((NumberOfChildrenInPartnershipNodeDouble)
                                        getAncestor(new NumberOfChildrenInPartnershipNodeDouble())).getOption();
        double forNPeople = getParent().getCount();
        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
        Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        SingleDeterminedCount sDC = (SingleDeterminedCount) getInputStats().getDeterminedCount(new SeparationStatsKey(numberOfChildren, forNPeople, timePeriod, currentDate));

        if(getOption() == SeparationOption.YES) {
            setCount(sDC.getRawUncorrectedCount());
        } else {
            setCount(forNPeople - sDC.getRawUncorrectedCount());
        }

        advanceCount();

        // Make separation children nodes

        makeChildren();

    }

    @Override
    public void makeChildren() {

        Integer pncip = ((PreviousNumberOfChildrenInPartnershipNodeDouble)
                getAncestor(new PreviousNumberOfChildrenInPartnershipNodeDouble())).getOption();

        if(pncip == 0) {
            addChild(new IntegerRange("na"));
        } else {
            ChildrenInYearOption ciy = ((ChildrenInYearNodeDouble)
                    getAncestor(new ChildrenInYearNodeDouble())).getOption();

            if(ciy == ChildrenInYearOption.NO) {
                addChild(new IntegerRange("na"));
            } else {
                YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
                Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

                Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

                double numberOfFemales = getCount();
                CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

                MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                        .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate));

                Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

                for(IntegerRange o : options) {
                    addChild(o);
                }
            }
        }

//        if(getOption() == SeparationOption.YES) {
//
//            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
//            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();
//
//            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);
//
//            double numberOfFemales = getCount();
//            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);
//
//            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
//                            .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate));
//
//            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();
//
//            for(IntegerRange o : options) {
//                addChild(o);
//            }
//
//        } else {
//            addChild(null, getCount());
//        }

    }

    @Override
    public void runTask() {
        advanceCount();
    }
}
