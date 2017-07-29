package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NewPartnerAgeNodeDouble extends DoubleNode<IntegerRange, String> implements ControlSelfNode {

    public NewPartnerAgeNodeDouble(IntegerRange option, SeparationNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
            calcCount();
        }

    }

    @Override
    public Node<String, ?, Double, ?> makeChildInstance(String childOption, Double initCount) {
        return null;
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();
    }

    @Override
    public void advanceCount() {

    }

    @Override
    public void calcCount() {

        if(getOption().getValue() == null) {
            setCount(getParent().getCount());
        } else {
            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            double numberOfFemales = getParent().getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate));

            if(getOption().getValue() == null) {
                setCount(getParent().getCount());
            } else {
                setCount(mDC.getRawUncorrectedCount().get(getOption()));
            }
        }

    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = getParent().toStringAL();
        if(getOption() == null) {
            s.add("na");
        } else {
            s.add(getOption().toString());
        }
        s.add(getCount().toString());
        return s;
    }

    public CTRow<Double> toCTRow() {
        CTRow r = getParent().toCTRow();

        if(getOption() == null) {
            r.setVariable(getVariableName(), "na");
        } else {
            r.setVariable(getVariableName(), getOption().toString());
        }

//        if(((SexNodeDouble) getAncestor(new SexNodeDouble())).getOption() == SexOption.FEMALE) {
//            System.out.print("");
//        }

        r.setCount(getCount());

        return r;
    }

    @Override
    public String getVariableName() {
        return "NPA";
    }

}
