package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNode extends Node<DiedOption, Integer> {

    public DiedNode(DiedOption option, AgeNode parentNode) {
        super(option, parentNode);
        calcCount();
    }

    @Override
    public void makeChildren() {
        // NA
    }

    @Override
    public Node<Integer, ?> addChild(Integer childOption, int initCount) {

        PreviousNumberOfChildrenInPartnershipNode childNode;
        try {
            childNode = (PreviousNumberOfChildrenInPartnershipNode) getChild(childOption);
            childNode.incCount(initCount);
        } catch (ChildNotFoundException e) {
            childNode = new PreviousNumberOfChildrenInPartnershipNode(childOption, this, initCount);
            super.addChild(childNode);
        }

        return childNode;

    }

    // TODO keep writing code...

    @Override
    public Node<Integer, ?> addChild(Integer childOption) {
        return null;
    }

    @Override
    public void advanceCount() {

        if(getOption() == DiedOption.NO) {
            AgeNode aN = (AgeNode) getAncestor(new AgeNode());
            aN.getParent().addChild(new IntegerRange(aN.getOption().getValue() + 1), getCount());
        }

    }

    @Override
    public void calcCount() {

        YearDate yob = ((YOBNode) getAncestor(new YOBNode())).getOption();
        Integer age = ((AgeNode) getAncestor(new AgeNode())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        double forNPeople = getParent().getCount();
        CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

        char sex;

        SexOption sexOption = (SexOption) getAncestor(new SexNode()).getOption();

        if(sexOption == SexOption.MALE) {
            sex = 'm';
        } else {
            sex = 'f';
        }

        SingleDeterminedCount rDC = (SingleDeterminedCount) getInputStats()
                .getDeterminedCount(new DeathStatsKey(age, forNPeople, timePeriod, currentDate, sex, false));

        if(getOption() == DiedOption.YES) {
            setCount(forNPeople * rDC.getRawCount());
        } else {
            setCount(forNPeople * (1 - rDC.getRawCount()));
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
}
