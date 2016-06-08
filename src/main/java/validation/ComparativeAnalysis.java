package validation;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.generated.StatisticalTables;
import model.simulationLogic.StatisticalManipulationCalculationError;
import utils.MapUtils;
import utils.time.*;

import java.util.Arrays;

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

//        if(getTableSize(expectedEvents) != getTableSize(observedEvents)) {
//            throw new StatisticalManipulationCalculationError("Tables should be the same size");
//        }

        IntegerRange[] orderedKeys = observedEvents.getData().keySet().toArray(new IntegerRange[observedEvents.getData().keySet().size()]);

        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        double sumObs_exp = 0;
        double sumVar = 0;

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

            int ns = n1fTHISROW + n2fTHISROW;
            int ms = m1f + m2f;

            double var = (n1fTHISROW * n2fTHISROW * ms * (ns - ms)) / (Math.pow(ns, 2) * (ns + 1));

            sumObs_exp += obv_exp2;
            sumVar += var;

        }

        double logRankValue = Math.pow(sumObs_exp, 2) / sumVar;

        return new KaplanMeierAnalysis(EventType.MALE_DEATH, new YearDate(1600), logRankValue);
    }

    private int getTableSize(OneDimensionDataDistribution table) {
        return table.getData().keySet().size();
    }

    @Override
    public boolean passed() {
        return false;
    }

    @Override
    public void runAnalysis() throws UnsupportedDateConversion, StatisticalManipulationCalculationError {

        // for each year in analysis period
        for(DateClock d = startDate.getDateClock(); DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // EVENT - Male Death

            IKaplanMeierAnalysis result = runKMAnalysis(d, EventType.MALE_DEATH, desired, generated);
            System.out.println(d.toString() + " | " + EventType.MALE_DEATH.toString() + " | Sig Diff? " + result.significantDifferenceBetweenGroups() + " | " + result.getPValue());


        }

    }

    private IKaplanMeierAnalysis runKMAnalysis(Date date, EventType eventType, StatisticalTables expected, StatisticalTables observed) throws StatisticalManipulationCalculationError, UnsupportedDateConversion {
        // get survival tables for all males born in year
        OneDimensionDataDistribution populationSurvivorTable = observed.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType);

        MapUtils.print("G SUR-" + date.toString(), populationSurvivorTable.getData(), 0, 1, 100);

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType, populationSurvivorTable.getData(0), populationSurvivorTable.getMaxRowLabelValue().getMax());

        MapUtils.print("S SUR-" + date.toString(), statisticsSurvivorTable.getData(), 0, 1, 100);


        // perform KM analysis and log result
        return runKaplanMeier(statisticsSurvivorTable, populationSurvivorTable);


    }
}
