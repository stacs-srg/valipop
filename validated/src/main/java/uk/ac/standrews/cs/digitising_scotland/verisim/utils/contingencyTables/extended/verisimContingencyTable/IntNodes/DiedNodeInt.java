package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeInt extends IntNode<DiedOption, IntegerRange> {

    public DiedNodeInt(DiedOption option, AgeNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        if(Character.toUpperCase(person.getSex()) == 'F') {

            IPartnershipExtended partnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);
            int numberOfChildren;

            if(partnership == null) {
                numberOfChildren = 0;
            } else {
                numberOfChildren = PersonCharacteristicsIdentifier.getChildrenBirthedBeforeDate(partnership, currentDate);
            }

            IntegerRange range = resolveToChildRange(numberOfChildren);

            try {
                getChild(range).processPerson(person, currentDate);
            } catch (ChildNotFoundException e) {
                addChild(range).processPerson(person, currentDate);
            }

        }

    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new PreviousNumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

    private IntegerRange resolveToChildRange(Integer pncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(pncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
        Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(pncip)) {
                return o;
            }
        }

        if(pncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }

}
