package validation;

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
import utils.time.*;

import java.util.Arrays;
import java.util.HashMap;
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
    public void printResults() throws UnsupportedDateConversion {

        System.out.println("Year | M Death | D Death | 0 Order |");

        Date[] years = results.keySet().toArray(new Date[results.keySet().size()]);

        Arrays.sort(years, Date::compareTo);

        for (Date d : years) {
            Map<EventType, IKaplanMeierAnalysis> res = results.get(d);

            System.out.print(d.getYear() + " | ");

            printPassAndPValue(EventType.MALE_DEATH, res);
            printPassAndPValue(EventType.FEMALE_DEATH, res);
            printPassAndPValue(EventType.FIRST_BIRTH, res);

            System.out.println();
        }

        System.out.println();
        System.out.println("Fisher Combined P Values");
        System.out.println("Male Death   P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountMaleDeath).cumulativeProbability(fisherSumMaleDeath)));
        System.out.println("Female Death P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountFemaleDeath).cumulativeProbability(fisherSumFemaleDeath)));
        System.out.println("Both Deaths  P Value = " + (1 - new ChiSquaredDistribution(2 * (fisherCountMaleDeath + fisherCountFemaleDeath)).cumulativeProbability(fisherSumMaleDeath + fisherSumFemaleDeath)));
        System.out.println("First Births P Value = " + (1 - new ChiSquaredDistribution(2 * fisherCountFirstBirth).cumulativeProbability(fisherSumFirstBirth)));

    }

    private void printPassAndPValue(EventType eventType, Map<EventType, IKaplanMeierAnalysis> res) {

        if (res.containsKey(eventType)) {
            System.out.print(getPassPrint(res.get(eventType).significantDifferenceBetweenGroups(), false) + "-");
            System.out.printf("%.3f ", res.get(eventType).getPValue());

            if (Double.isNaN(res.get(eventType).getPValue())) {
                System.out.print("  ");
            }

            System.out.print("| ");
        } else {
            System.out.print("        | ");
        }

    }

    private String getPassPrint(boolean result, boolean passCase) {
        if (result == passCase) {
            return "P";
        } else {
            return "F";
        }
    }

    @Override
    public void runAnalysis(IPopulation generatedPopulation) throws UnsupportedDateConversion, StatisticalManipulationCalculationError {

        Map<Date, Map<EventType, IKaplanMeierAnalysis>> results = new HashMap<Date, Map<EventType, IKaplanMeierAnalysis>>();

        // for each year in analysis period
        for (DateClock d = startDate.getDateClock(); DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // EVENT - Male Death

            Map<EventType, IKaplanMeierAnalysis> temp = new HashMap<EventType, IKaplanMeierAnalysis>();

            IKaplanMeierAnalysis result;
            try {
                result = runKMAnalysis(d, EventType.MALE_DEATH, desired, generated, generatedPopulation);
                temp.put(EventType.MALE_DEATH, result);

                new SurvivalPlot(desired.getSurvivorTable(d, null, EventType.MALE_DEATH), generated.getSurvivorTable(d, null, EventType.MALE_DEATH)).generatePlot();

                if (!Double.isNaN(result.getPValue())) {
                    fisherSumMaleDeath += Math.log(result.getPValue());
                    fisherCountMaleDeath++;
                }

//                System.out.println(d.toString() + " | " + EventType.MALE_DEATH.toString() + " | Sig Diff? " + result.significantDifferenceBetweenGroups() + " | " + result.getPValue() + " | " + result.getLogRankValue());

            } catch (UnsupportedEventType unsupportedEventType) {
                unsupportedEventType.printStackTrace();
            }

            try {
                result = runKMAnalysis(d, EventType.FEMALE_DEATH, desired, generated, generatedPopulation);
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
                result = runKMAnalysis(d, EventType.FIRST_BIRTH, desired, generated, generatedPopulation);
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

        PlotControl.showPlots();

    }

    private IKaplanMeierAnalysis runKMAnalysis(Date date, EventType eventType, StatisticalTables expected, StatisticalTables observed, IPopulation generatedPopulation) throws StatisticalManipulationCalculationError, UnsupportedDateConversion, UnsupportedEventType {
        // get survival tables for all males born in year
        OneDimensionDataDistribution populationSurvivorTable = observed.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType);
//        MapUtils.print("G SUR-" + date.toString(), populationSurvivorTable.getData(), 0, 1, 100);

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getSurvivorTable(date, new CompoundTimeUnit(1, TimeUnit.YEAR), eventType, populationSurvivorTable.getData(0), populationSurvivorTable.getLargestLabel().getMax(), generatedPopulation);
//        MapUtils.print("S SUR-" + date.toString(), statisticsSurvivorTable.getData(), 0, 1, 100);


        // perform KM analysis and log result
        return runKaplanMeier(eventType, statisticsSurvivorTable, populationSurvivorTable);


    }
}
