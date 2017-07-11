package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.AgeNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.YOBNodeDouble;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SeparationOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationNodeInt extends IntNode<SeparationOption, IntegerRange> {

    public SeparationNodeInt(SeparationOption option, NumberOfChildrenInPartnershipNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        // TODO change this to resolve to the correct interger ranges

        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Integer newPartnerAge = null;

        if(activePartnership != null && PersonCharacteristicsIdentifier.startedInYear(activePartnership, currentDate.getYearDate())) {
            IPersonExtended partner = activePartnership.getPartnerOf(person);

            newPartnerAge = partner.ageOnDate(activePartnership.getPartnershipDate());
        }

        for(Node<IntegerRange, ?, Integer, ?> node : getChildren()) {

            Boolean in = node.getOption().contains(newPartnerAge);

            if(newPartnerAge == null && in == null) {
                node.processPerson(person, currentDate);
                return;
            } else if (in){
                node.processPerson(person, currentDate);
                return;
            }

        }

        if(newPartnerAge == null) {
            addChild(new IntegerRange("na")).processPerson(person, currentDate);
        } else {

            Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

            double numberOfFemales = getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate));

            Set<IntegerRange> options = mDC.getRawUncorrectedCount().getLabels();

            for (IntegerRange o : options) {
                if (o.contains(newPartnerAge)) {
                    addChild(o).processPerson(person, currentDate);
                    return;
                }
            }
        }



    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new NewPartnerAgeNodeInt(childOption, this, initCount);
    }
}
