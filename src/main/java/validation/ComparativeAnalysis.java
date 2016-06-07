package validation;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.generated.StatisticalTables;
import model.simulationLogic.StatisticalManipulationCalculationError;
import utils.MapUtils;
import utils.time.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements IComparativeAnalysis {

    private StatisticalTables desired;
    private StatisticalTables generated;

    private Date startDate;
    private Date endDate;

    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated, Date analysisStartDate, Date analysisEndDate) {
        this.desired = desired;
        this.generated = generated;
        this.startDate = analysisStartDate;
        this.endDate = analysisEndDate;
    }

    @Override
    public IKaplanMeierAnalysis runKaplanMeier(OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError {

        if(getTableSize(expectedEvents) != getTableSize(observedEvents)) {
            throw new StatisticalManipulationCalculationError("Tables should be the same size");
        }

        int[] m1Failures = new int[getTableSize(observedEvents)];
        int[] m2Failures = new int[getTableSize(expectedEvents)];

        IntegerRange[] orderedKeys = (IntegerRange[]) observedEvents.getData().keySet().toArray();

        Arrays.sort(orderedKeys, IntegerRange::compareTo);


        for(int c = 0; c < orderedKeys.length - 1; c++) {

            int n1fTHISROW = observedEvents.getData(c).intValue();
            int n1fNEXTROW = observedEvents.getData(c+1).intValue();

            int n2fTHISROW = expectedEvents.getData(c).intValue();
            int n2fNEXTROW = expectedEvents.getData(c+1).intValue();

            int m1f = n1fTHISROW - n1fNEXTROW;
            int m2f = n2fTHISROW - n2fNEXTROW;

            double e1f = (n1fTHISROW * (m1f + m2f)) / (double) (n1fTHISROW + n2fTHISROW);

            double e2f = (n2fTHISROW * (m1f + m2f)) / (double) (n1fTHISROW + n2fTHISROW);

            double obv_exp1 = m1f - e1f;
            double obv_exp2 = m2f - e2f;




        }





        return null;
    }

    private int getTableSize(OneDimensionDataDistribution table) {
        return table.getData().keySet().size();
    }

    @Override
    public boolean passed() {
        return false;
    }

    @Override
    public void runAnalysis() throws UnsupportedDateConversion {

        // for each year in analysis period
        for(DateClock d = startDate.getDateClock(); DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {
            // get survival tables for all males born in year
            OneDimensionDataDistribution populationDeathSurvivorTable = generated.getSurvivorTable(d, new CompoundTimeUnit(1, TimeUnit.YEAR), EventType.DEATH);

            MapUtils.print("G SUR-" + d.toString(), populationDeathSurvivorTable.getData(), 0, 1, 100);

            // get equiverlent table from inputs stats
            OneDimensionDataDistribution statisticsDeathSurvivorTable = desired.getSurvivorTable(d, new CompoundTimeUnit(1, TimeUnit.YEAR), EventType.DEATH, populationDeathSurvivorTable.getData(0));

            MapUtils.print("S SUR-" + d.toString(), statisticsDeathSurvivorTable.getData(), 0, 1, 100);


            // perform KM analysis and log result
            IKaplanMeierAnalysis result = runKaplanMeier(statisticsDeathSurvivorTable, populationDeathSurvivorTable);

        }

    }
}
