package validation;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.generated.UnsupportedEventType;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.generated.StatisticalTables;
import model.IPopulation;
import model.simulationLogic.StatisticalManipulationCalculationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plots.survival.SurvivalPlot;
import utils.FileUtils;
import utils.time.*;
import utils.time.Date;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements IComparativeAnalysis {

    private StatisticalTables desired;
    private StatisticalTables generated;

    private Date startDate;
    private Date endDate;

    private Map<Date, Map<EventType, IKaplanMeierAnalysis>> results;

    public static Logger log = LogManager.getLogger(ComparativeAnalysis.class);

    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated, Date analysisStartDate, Date analysisEndDate) {
        this.desired = desired;
        this.generated = generated;
        this.startDate = analysisStartDate;
        this.endDate = analysisEndDate;
    }

    protected static IKaplanMeierAnalysis runKaplanMeier(EventType event, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError {

        IntegerRange[] orderedKeys = expectedEvents.getData().keySet().toArray(new IntegerRange[expectedEvents.getData().keySet().size()]);
        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        PrintStream pS = FileUtils.setupDumpPrintStream("survialTables_" + event + "_" + expectedEvents.getYear().getYear());
        pS.println("Expected");
        expectedEvents.print(pS);
        pS.println("Observed");
        observedEvents.print(pS);

        double sumM1f = 0;
        double sumE1f = 0;

        double sumM2f = 0;
        double sumE2f = 0;

        double sumVar = 0;

        for (int c = 0; c < orderedKeys.length - 1; c++) {

            int n1fNEXTROW;
            int n2fNEXTROW;

            int n1f = observedEvents.getData(c).intValue();
            int n2f = expectedEvents.getData(c).intValue();

            n1fNEXTROW = observedEvents.getData(c + 1).intValue();
            n2fNEXTROW = expectedEvents.getData(c + 1).intValue();


            int m1f = n1f - n1fNEXTROW;
            int m2f = n2f - n2fNEXTROW;

            double e1f = (n1f / (double) (n1f + n2f)) * (m1f + m2f);
            double e2f = (n2f / (double) (n1f + n2f)) * (m1f + m2f);

            sumM1f += m1f;
            sumE1f += e1f;

            sumM2f += m2f;
            sumE2f += e2f;

            int ns = n1f + n2f;

            double var = 0;


            long topPart1 = (n1f * n2f);
            long topPart2 = (m1f + m2f) * (n1f + n2f - m1f - m2f);
            long bottom = pow((n1f + n2f), 2) * (n1f + n2f);

            var = topPart1/(double)bottom;
            var = var * topPart2;

            sumVar += var;

        }

        double logRankValue = Math.pow(sumM1f - sumE1f, 2) / sumVar;

        return new KaplanMeierAnalysis(event, observedEvents.getYear(), logRankValue);
    }

    @Override
    public Map<Date, Map<EventType, IKaplanMeierAnalysis>> getResults() {
        return results;
    }



    private boolean printPassAndPValue(EventType eventType, Map<EventType, IKaplanMeierAnalysis> res, PrintStream resultOutput) {

        boolean result = res.get(eventType).significantDifferenceBetweenGroups();

        if (res.containsKey(eventType)) {
            resultOutput.print(getPassPrint(result, false) + "-");
            resultOutput.printf("%.3f ", res.get(eventType).getPValue());
            resultOutput.print("(");
            resultOutput.printf("%.3f ", res.get(eventType).getLogRankValue());
            resultOutput.print(")");

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

        ArrayList<YearDate> years = new ArrayList<>();


        // for each year in analysis period
        for (DateClock d = startDate.getDateClock(); DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {

            Map<EventType, IKaplanMeierAnalysis> temp = new HashMap<>();

            temp.put(EventType.MALE_DEATH, getKMResult(d, EventType.MALE_DEATH, new CompoundTimeUnit(1, TimeUnit.YEAR), generatedPopulation, config));
            temp.put(EventType.FEMALE_DEATH, getKMResult(d, EventType.FEMALE_DEATH, new CompoundTimeUnit(1, TimeUnit.YEAR), generatedPopulation, config));
            temp.put(EventType.FIRST_BIRTH, getKMResult(d, EventType.FIRST_BIRTH, new CompoundTimeUnit(1, TimeUnit.YEAR), generatedPopulation, config));

            results.put(d.getYearDate(), temp);
        }

        ArrayList<YearDate> maleDeathDataYears = new ArrayList<>(desired.getDataYearsInMap(EventType.MALE_DEATH));
        Collections.sort(maleDeathDataYears);

        Iterator<YearDate> i = maleDeathDataYears.iterator();

        Date prevDate;

        if(DateUtils.dateBefore(startDate, maleDeathDataYears.get(0))) {
            prevDate = startDate;
        } else {
            prevDate = maleDeathDataYears.get(0);
        }

        YearDate thisDate = null;
        YearDate nextDate = null;

        while(i.hasNext()) {
            if(thisDate == null) {
                thisDate = i.next();
            } else {
                thisDate = nextDate;
            }

            if(i.hasNext()) {
                nextDate = i.next();
            }

            int days = DateUtils.differenceInDays(thisDate, nextDate);

            DateInstant midDate;

            if(days == 0) {
                midDate = endDate.getDateInstant();
            } else {
                midDate = DateUtils.calculateDateInstant(thisDate, days);
            }

            if(DateUtils.dateBefore(thisDate, startDate)) {

                if(!DateUtils.dateBefore(midDate, startDate)) {
                    results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, getKMResult(startDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(startDate, midDate), generatedPopulation, config));
                }

            } else if(DateUtils.dateBefore(endDate, midDate)) {

                results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, getKMResult(prevDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(prevDate, endDate), generatedPopulation, config));

            } else {

                results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, getKMResult(prevDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(prevDate, midDate), generatedPopulation, config));
            }

            prevDate = midDate;


        }


        this.results = results;

    }

    private IKaplanMeierAnalysis getKMResult(Date date, EventType eventType, CompoundTimeUnit timePeriod, IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException {
        IKaplanMeierAnalysis result = null;

        try {
            result = runKMAnalysis(date, eventType, desired, generated, timePeriod, generatedPopulation, config);

        } catch (UnsupportedEventType unsupportedEventType) {
            log.info("Kaplan Meier ran with unsupported event type: " + eventType.toString());
        }

        if(config.produceDatFiles() && eventType != EventType.FIRST_BIRTH) {
            outputCalcDataToDatFile(date, timePeriod, "_" + timePeriod.toString(), eventType, generatedPopulation, config);
        }

        return result;
    }

    private IKaplanMeierAnalysis runKMAnalysis(Date date, EventType eventType, StatisticalTables expected, StatisticalTables observed, CompoundTimeUnit timePeriod, IPopulation generatedPopulation, Config config) throws StatisticalManipulationCalculationError, UnsupportedDateConversion, UnsupportedEventType, IOException {
        // get survival tables for all males born in year
        OneDimensionDataDistribution populationSurvivorTable = observed.getCohortSurvivorTable(date, eventType);
//        MapUtils.outputResults("G SUR-" + date.toString(), populationSurvivorTable.getData(), 0, 1, 100);

//        System.out.println(populationSurvivorTable.getData(0));

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getCohortSurvivorTable(date, eventType, populationSurvivorTable.getData(0), populationSurvivorTable.getLargestLabel().getMax() - 1, generatedPopulation);
//        MapUtils.outputResults("S SUR-" + date.toString(), statisticsSurvivorTable.getData(), 0, 1, 100);

        if(config.produceGraphs()) {
            new SurvivalPlot(statisticsSurvivorTable, populationSurvivorTable).generatePlot(eventType, date, config);
        }

        // perform KM analysis and log result
        return runKaplanMeier(eventType, statisticsSurvivorTable, populationSurvivorTable);


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

    }

    private void outputCalcDataToDatFile(Date date, CompoundTimeUnit timePeriod, String note, EventType eventType, IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion {
        Collection<FailureTimeRow> failures = getFailureAtTimesTable(date, timePeriod, eventType, desired, generated, generatedPopulation, config);

        String fName = "";
        switch (eventType) {

            case FIRST_BIRTH:
                fName = "firstBirths";
                break;
            case SECOND_BIRTH:
                fName = "secondBirths";
                break;
            case THIRD_BIRTH:
                fName = "thirdBirths";
                break;
            case FOURTH_BIRTH:
                fName = "fourthBirths";
                break;
            case FIFTH_BIRTH:
                fName = "fifthBirths";
                break;
            case MALE_DEATH:
                fName = "maleDeaths";
                break;
            case FEMALE_DEATH:
                fName = "femaleDeaths";
                break;
        }

        PrintStream resultsOutput = FileUtils.setupDatFileAsStream(fName + date.toOrderableString() + note, config);
        FileUtils.outputFailureTimeTable(failures, resultsOutput);
    }

    public Collection<FailureTimeRow> getFailureAtTimesTable(Date date, CompoundTimeUnit timePeriod, EventType event, StatisticalTables expected, StatisticalTables observed, IPopulation generatedPopulation, Config config) throws UnsupportedDateConversion {


        Collection<FailureTimeRow> rows = observed.getFailureAtTimesTable(date, 1, config.getTE(), event);

        int maxAge = getHighestTimeValue(rows);

        int numberInObserved = rows.size() - 1;
        rows.addAll(expected.getFailureAtTimesTable(date, 0, config.getTE(), event, (double) numberInObserved, maxAge, generatedPopulation));

        return rows;

    }

    private int getHighestTimeValue(Collection<FailureTimeRow> rows) {

        int max = Integer.MIN_VALUE;

        for(FailureTimeRow f : rows) {
            int v;
            if(max < (v = f.getTimeElapsed())) {
                max = v;
            }
        }

        return max;

    }

    private static long pow (long a, int b) {
        if ( b == 0)        return 1;
        if ( b == 1)        return a;
        if (isEven( b ))    return     pow ( a * a, b/2); //even a=(a^2)^b/2
        else                return a * pow ( a * a, b/2); //odd  a=a*(a^2)^b/2

    }


    private static boolean isEven(int i) {
        if((i%2)==0)
            return true;
        else
            return false;
    }

}
