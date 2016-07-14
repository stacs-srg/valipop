package validation;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.generated.UnsupportedEventType;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.generated.StatisticalTables;
import model.IPopulation;
import model.simulationLogic.StatisticalManipulationCalculationError;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import plots.PlotControl;
import plots.survival.SurvivalPlot;
import utils.MapUtils;
import utils.time.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements IComparativeAnalysis {

    private StatisticalTables desired;
    private StatisticalTables generated;

    private Date startDate;
    private Date endDate;

    private double fisherSumMaleDeath = 0;
    private double fisherCountMaleDeath = 0;

    private double fisherSumFemaleDeath = 0;
    private double fisherCountFemaleDeath = 0;

    private double fisherSumFirstBirth = 0;
    private double fisherCountFirstBirth = 0;


    private Map<Date, Map<EventType, IKaplanMeierAnalysis>> results;

    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated, Date analysisStartDate, Date analysisEndDate) {
        this.desired = desired;
        this.generated = generated;
        this.startDate = analysisStartDate;
        this.endDate = analysisEndDate;
    }

    @Override
    public IKaplanMeierAnalysis runKaplanMeier(EventType event, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError {

        IntegerRange[] orderedKeys = observedEvents.getData().keySet().toArray(new IntegerRange[observedEvents.getData().keySet().size()]);

        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        double sumObs_exp = 0;
        double sumVar = 0;

        for (int c = 0; c < orderedKeys.length - 1; c++) {

            int n1fTHISROW = observedEvents.getData(c).intValue();
            int n1fNEXTROW = observedEvents.getData(c + 1).intValue();

            int n2fTHISROW = expectedEvents.getData(c).intValue();
            int n2fNEXTROW = expectedEvents.getData(c + 1).intValue();

            if (n1fTHISROW + n2fTHISROW == 0) {
                // No people left in either at risk group
                break;
            }

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

        return new KaplanMeierAnalysis(event, observedEvents.getYear(), logRankValue);
    }

    @Override
    public boolean passed() {
        return false;
    }

    @Override
    public Map<Date, Map<EventType, IKaplanMeierAnalysis>> getResults() {
        return results;
    }

    @Override
    public void outputResults(PrintStream resultOutput) throws UnsupportedDateConversion {

        int mPasses = 0;
        int mFails = 0;

        int fPasses = 0;
        int fFails = 0;

        int b0Passes = 0;
        int b0Fails = 0;

        resultOutput.println("Year | M Death | F Death | 0 Order |");

        Date[] years = results.keySet().toArray(new Date[results.keySet().size()]);

        Arrays.sort(years, Date::compareTo);

        for (Date d : years) {
            Map<EventType, IKaplanMeierAnalysis> res = results.get(d);

            resultOutput.print(d.getYear() + " | ");

            if(printPassAndPValue(EventType.MALE_DEATH, res, resultOutput)) {
                mPasses++;
            } else {
                mFails++;
            }

            if(printPassAndPValue(EventType.FEMALE_DEATH, res, resultOutput)) {
                fPasses++;
            } else {
                fFails++;
            }

            if(printPassAndPValue(EventType.FIRST_BIRTH, res, resultOutput)) {
                b0Passes++;
            } else {
                b0Fails++;
            }

            resultOutput.println();
        }

        int tPasses = mPasses + fPasses + b0Passes;
        int tFails = mFails + fFails + b0Fails;

        resultOutput.println();
        resultOutput.print("Summed Passes and Fails\n");
        resultOutput.print("Male death   - Passes: " + mPasses + "    |    Fails: " + mFails + "\n");
        resultOutput.print("Female death - Passes: " + fPasses + "    |    Fails: " + fFails + "\n");
        resultOutput.print("First births - Passes: " + b0Passes + "    |    Fails: " + b0Fails + "\n");
        resultOutput.print("Totals       - Passes: " + tPasses + "    |    Fails: " + tFails + "\n");

//        System.out.println();
//        System.out.println("Fisher Combined P Values");
//        System.out.println("Male Death   P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountMaleDeath).cumulativeProbability(fisherSumMaleDeath)));
//        System.out.println("Female Death P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountFemaleDeath).cumulativeProbability(fisherSumFemaleDeath)));
//        System.out.println("Both Deaths  P Value = " + (1 - new ChiSquaredDistribution(2 * (fisherCountMaleDeath + fisherCountFemaleDeath)).cumulativeProbability(fisherSumMaleDeath + fisherSumFemaleDeath)));
//        System.out.println("First Births P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountFirstBirth).cumulativeProbability(fisherSumFirstBirth)));

    }

    private boolean printPassAndPValue(EventType eventType, Map<EventType, IKaplanMeierAnalysis> res, PrintStream resultOutput) {

        boolean result = res.get(eventType).significantDifferenceBetweenGroups();

        if (res.containsKey(eventType)) {
            resultOutput.print(getPassPrint(result, false) + "-");
            resultOutput.printf("%.3f ", res.get(eventType).getPValue());

            if (Double.isNaN(res.get(eventType).getPValue())) {
                resultOutput.print("  ");
            }

            resultOutput.print("| ");
        } else {
            resultOutput.print("        | ");
        }

        return !result;


    }

    private String getPassPrint(boolean result, boolean passCase) {
        if (result == passCase) {
            return "P";
        } else {
            return "F";
        }
    }

    @Override
    public void runAnalysis(IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException {

        Map<Date, Map<EventType, IKaplanMeierAnalysis>> results = new HashMap<Date, Map<EventType, IKaplanMeierAnalysis>>();

        // for each year in analysis period
        for (DateClock d = startDate.getDateClock(); DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // EVENT - Male Death

            Map<EventType, IKaplanMeierAnalysis> temp = new HashMap<EventType, IKaplanMeierAnalysis>();

            IKaplanMeierAnalysis result;
            try {
                result = runKMAnalysis(d, EventType.MALE_DEATH, desired, generated, generatedPopulation, config);
                temp.put(EventType.MALE_DEATH, result);


                if (!Double.isNaN(result.getPValue())) {
                    fisherSumMaleDeath += Math.log(result.getPValue());
                    fisherCountMaleDeath++;
                }

//                System.out.println(d.toString() + " | " + EventType.MALE_DEATH.toString() + " | Sig Diff? " + result.significantDifferenceBetweenGroups() + " | " + result.getPValue() + " | " + result.getLogRankValue());

            } catch (UnsupportedEventType unsupportedEventType) {
                unsupportedEventType.printStackTrace();
            }

            try {
                result = runKMAnalysis(d, EventType.FEMALE_DEATH, desired, generated, generatedPopulation, config);
                temp.put(EventType.FEMALE_DEATH, result);

                if (!Double.isNaN(result.getPValue())) {
                    fisherSumFemaleDeath += Math.log(result.getPValue());
                    fisherCountFemaleDeath++;
                }

//                System.out.println(d.toString() + " | " + EventType.FEMALE_DEATH.toString() + " | Sig Diff? " + result.significantDifferenceBetweenGroups() + " | " + result.getPValue() + " | " + result.getLogRankValue());

            } catch (UnsupportedEventType unsupportedEventType) {
                unsupportedEventType.printStackTrace();
            }

            try {
                result = runKMAnalysis(d, EventType.FIRST_BIRTH, desired, generated, generatedPopulation, config);
                temp.put(EventType.FIRST_BIRTH, result);

                if (!Double.isNaN(result.getPValue())) {
                    fisherSumFirstBirth += Math.log(result.getPValue());
                    fisherCountFirstBirth++;
                }


//                System.out.println(d.toString() + " | " + EventType.FIRST_BIRTH.toString() + " | Sig Diff? " + result.significantDifferenceBetweenGroups() + " | " + result.getPValue() + " | " + result.getLogRankValue());

            } catch (UnsupportedEventType unsupportedEventType) {
                unsupportedEventType.printStackTrace();
            }

            results.put(d, temp);
        }

        this.results = results;

//        PlotControl.showPlots();

    }

    private IKaplanMeierAnalysis runKMAnalysis(Date date, EventType eventType, StatisticalTables expected, StatisticalTables observed, IPopulation generatedPopulation, Config config) throws StatisticalManipulationCalculationError, UnsupportedDateConversion, UnsupportedEventType, IOException {
        // get survival tables for all males born in year
        OneDimensionDataDistribution populationSurvivorTable = observed.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType);
//        MapUtils.outputResults("G SUR-" + date.toString(), populationSurvivorTable.getData(), 0, 1, 100);

//        System.out.println(populationSurvivorTable.getData(0));

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType, populationSurvivorTable.getData(0), populationSurvivorTable.getLargestLabel().getMax(), generatedPopulation);
//        MapUtils.outputResults("S SUR-" + date.toString(), statisticsSurvivorTable.getData(), 0, 1, 100);

        if(config.produceGraphs()) {
            new SurvivalPlot(statisticsSurvivorTable, populationSurvivorTable).generatePlot(eventType, date, config);
        }

        // perform KM analysis and log result
        return runKaplanMeier(eventType, statisticsSurvivorTable, populationSurvivorTable);


    }
}
