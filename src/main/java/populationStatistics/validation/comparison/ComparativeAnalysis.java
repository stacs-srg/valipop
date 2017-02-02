package populationStatistics.validation.comparison;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.MonthDate;
import dateModel.dateImplementations.YearDate;
import dateModel.exceptions.UnsupportedDateConversion;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;
import events.EventType;
import events.UnsupportedEventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.dataDistributionTables.LabelValueDataRow;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.TableTranformationUtils;
import populationStatistics.recording.PopulationComposition;
import populationStatistics.recording.PopulationStatistics;
import populationStatistics.recording.generated.GeneratedPopulationComposition;
import populationStatistics.validation.exceptions.PValueInvalidException;
import populationStatistics.validation.exceptions.StatisticalManipulationCalculationError;
import populationStatistics.validation.kaplanMeier.IKaplanMeierAnalysis;
import populationStatistics.validation.kaplanMeier.KaplanMeierAnalysis;
import populationStatistics.validation.kaplanMeier.plots.SurvivalPlot;
import populationStatistics.validation.kaplanMeier.utils.FailureTimeRow;
import populationStatistics.validation.summaryData.SummaryRow;
import simulationEntities.population.IPopulation;
import simulationEntities.population.dataStructure.PeopleCollection;
import utils.fileUtils.FileUtils;
import utils.specialTypes.integerRange.IntegerRange;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysis implements IComparativeAnalysis {

    private StatisticalTables desired;
    private StatisticalTables generated;

    private AdvancableDate startDate;
    private Date endDate;

    private Map<Date, Map<EventType, IKaplanMeierAnalysis>> results;

    public static Logger log = LogManager.getLogger(ComparativeAnalysis.class);

    public static ComparativeAnalysis performComparison(Config config, PeopleCollection generatedPopulation,
                                                        PopulationStatistics desiredPopulationStatistics)
                                    throws UnsupportedEventType, StatisticalManipulationCalculationError, IOException {

        PopulationComposition generatedPopulationComposition = new GeneratedPopulationComposition(config.getTS(), config.getTE(), generatedPopulation);

        // compare desired and generated population
        ComparativeAnalysis comparisonOfDesiredAndGenerated = new ComparativeAnalysis(desiredPopulationStatistics, generatedPopulationComposition, config.getT0(), config.getTE());

        comparisonOfDesiredAndGenerated.runAnalysis(generatedPopulation, config);


        return comparisonOfDesiredAndGenerated;


    }


    public ComparativeAnalysis(StatisticalTables desired, StatisticalTables generated, AdvancableDate analysisStartDate, Date analysisEndDate) {
        this.desired = desired;
        this.generated = generated;
        this.startDate = analysisStartDate;
        this.endDate = analysisEndDate;
    }

    protected static IKaplanMeierAnalysis runKaplanMeier(EventType event, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents, Config config) throws StatisticalManipulationCalculationError {

        IntegerRange[] orderedKeys = expectedEvents.getData().keySet().toArray(new IntegerRange[expectedEvents.getData().keySet().size()]);
        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        PrintStream pS = FileUtils.setupDumpPrintStream("survialTables-" + event + "-" + expectedEvents.getYear().getYear(), config);
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



    private boolean printPassAndPValue(EventType eventType, Map<EventType, IKaplanMeierAnalysis> res, PrintStream resultOutput) throws PValueInvalidException {

        boolean result = res.get(eventType).significantDifferenceBetweenGroups();

        if (res.containsKey(eventType)) {
            resultOutput.print(getPassPrint(result, false) + "-");
            resultOutput.printf("%.3f ", res.get(eventType).getPValue());

//            resultOutput.print("(");
//            resultOutput.printf("%.3f ", res.get(eventType).getLogRankValue());
//            resultOutput.print(")");

            if (Double.isNaN(res.get(eventType).getPValue())) {
                resultOutput.print("  ");
                resultOutput.print("| ");
                throw new PValueInvalidException();
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
    public void runAnalysis(IPopulation generatedPopulation, Config config) throws StatisticalManipulationCalculationError, IOException, UnsupportedEventType {

        Map<Date, Map<EventType, IKaplanMeierAnalysis>> results = new HashMap<Date, Map<EventType, IKaplanMeierAnalysis>>();

        ArrayList<YearDate> years = new ArrayList<>();


        // for each year in analysis period
        for (AdvancableDate d = startDate; DateUtils.dateBefore(d, endDate); d = d.advanceTime(1, TimeUnit.YEAR)) {

            Map<EventType, IKaplanMeierAnalysis> temp = new HashMap<>();

            temp.put(EventType.MALE_DEATH, runKMAnalysisOnCohort(d, EventType.MALE_DEATH, desired, generated, generatedPopulation, config));
            temp.put(EventType.FEMALE_DEATH, runKMAnalysisOnCohort(d, EventType.FEMALE_DEATH, desired, generated, generatedPopulation, config));
            temp.put(EventType.FIRST_BIRTH, runKMAnalysisOnCohort(d, EventType.FIRST_BIRTH, desired, generated, generatedPopulation, config));

            if(config.produceDatFiles()) {
                Collection<FailureTimeRow> failures = getFailureAtTimesTable(d, EventType.MALE_DEATH, desired, generated, generatedPopulation, config);
                FileUtils.outputFailureTimeRowsToStream(failures, makeNamedStream(d, "-cohort", EventType.MALE_DEATH, config));

                failures = getFailureAtTimesTable(d, EventType.FEMALE_DEATH, desired, generated, generatedPopulation, config);
                FileUtils.outputFailureTimeRowsToStream(failures, makeNamedStream(d, "-cohort", EventType.FEMALE_DEATH, config));

                failures = getTableOfFailureTimes(d, EventType.FIRST_BIRTH, desired, generated, generatedPopulation);
                FileUtils.outputFailureTimeRowsToStream(failures, makeNamedStream(d, "-cohort", EventType.FIRST_BIRTH, config));

            }

            results.put(d.getYearDate(), temp);
        }

        compareSeparation(desired, generated, config);
        comparePartnering(desired, generated, config);

        // Time period anlysis code - only for MALE_DEATH at the moment

//        ArrayList<YearDate> maleDeathDataYears = new ArrayList<>(desired.getDataYearsInMap(EventType.MALE_DEATH));
//        Collections.sort(maleDeathDataYears);
//
//        Iterator<YearDate> i = maleDeathDataYears.iterator();
//
//        Date prevDate;
//
//        if(DateUtils.dateBefore(startDate, maleDeathDataYears.get(0))) {
//            prevDate = startDate;
//        } else {
//            prevDate = maleDeathDataYears.get(0);
//        }
//
//        YearDate thisDate = null;
//        YearDate nextDate = null;
//
//        while(i.hasNext()) {
//
//            System.out.println(prevDate.rowAsString());
//
//            if(thisDate == null) {
//                thisDate = i.next();
//            } else {
//                thisDate = nextDate;
//            }
//
//            if(i.hasNext()) {
//                nextDate = i.next();
//            }
//
//            int days = DateUtils.differenceInDays(thisDate, nextDate);
//
//            DateInstant midDate;
//
//            if(days == 0) {
//                midDate = endDate.getDateInstant();
//            } else {
//                midDate = DateUtils.calculateDateInstant(thisDate, days);
//            }
//
//            if(DateUtils.dateBefore(thisDate, startDate)) {
//
//                if(!DateUtils.dateBefore(midDate, startDate)) {
//
//                    results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, runKMAnalysisForTimePeriod(prevDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(prevDate, midDate), config));
//                }
//
//            } else if(DateUtils.dateBefore(endDate, midDate)) {
//
//                results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, runKMAnalysisForTimePeriod(prevDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(prevDate, endDate), config));
//
//            } else {
//
//                results.get(midDate.getYearDate()).put(EventType.MALE_DEATH, runKMAnalysisForTimePeriod(prevDate, EventType.MALE_DEATH, DateUtils.differenceInMonths(prevDate, midDate), config));
//            }
//
//            prevDate = midDate;
//
//
//        }


        this.results = results;

    }

    private void comparePartnering(StatisticalTables desired, StatisticalTables generated, Config config) {

        ArrayList<YearDate> mapKeys = new ArrayList<>(desired.getDataYearsInMap(EventType.PARTNERING));

        // work out the date bounds of map keys
        Collections.sort(mapKeys);

        // for each bound (i.e. each input table)
        for(int i = 0; i < mapKeys.size(); i++) {

            Date start;
            Date end;

            if(i == 0) {
                start = startDate;

                while(DateUtils.dateBefore(mapKeys.get(i + 1), startDate)) {
                    i++;
                }

            } else {

                AdvancableDate prev = mapKeys.get(i-1);
                Date current = mapKeys.get(i);

                start = prev.advanceTime(DateUtils.differenceInMonths(prev, current).getCount() / 2, TimeUnit.MONTH);

            }

            if(i == mapKeys.size() - 1) {
                end = endDate;
            } else {

                Date next = mapKeys.get(i+1);
                AdvancableDate current = mapKeys.get(i);

                end = current.advanceTime(DateUtils.differenceInMonths(current, next).getCount() / 2, TimeUnit.MONTH);

            }

            ArrayList<IntegerRange> femaleAgeRanges = new ArrayList<>(desired.getPartneringData(start, end).getRowKeys());

            Collections.sort(femaleAgeRanges);

            for(int a = 0; a < femaleAgeRanges.size(); a++) {

                IntegerRange ageRange = femaleAgeRanges.get(a);

                OneDimensionDataDistribution desiredPartnering = desired.getPartneringData(start, end, ageRange, null);
                OneDimensionDataDistribution generatedPartnering = generated.getPartneringData(start, end, ageRange, desiredPartnering.getData().keySet());

//                System.out.println(start.toString());
//                System.out.println("FaR = " + ageRange.toString());
//
//                for(IntegerRange iR : generatedPartnering.getData().keySet()) {
//                    System.out.println(iR.toString());
//                }


//                for(int age = ageRange.getMin(); age <= ageRange.getMax(); age ++) {
//                    generated.getPartneringData(start, end).getData(age);
//                }
//                generated.getPartneringData(start, end);

                Collection<LabelValueDataRow> table = TableTranformationUtils.transform1DDDToCollectionOfLabelValueDataRow(desiredPartnering, "desired");
                table.addAll(TableTranformationUtils.transform1DDDToCollectionOfLabelValueDataRow(generatedPartnering, "generated"));
                FileUtils.outputDataRowsToStream("label value group", table, makeNamedStream(start, "_" + ageRange.toString(), EventType.PARTNERING, config));

            }

        }


    }

    private void compareSeparation(StatisticalTables desired, StatisticalTables generated, Config config) {

        ArrayList<YearDate> mapKeys = new ArrayList<>(desired.getDataYearsInMap(EventType.SEPARATION));

        // work out the date bounds of map keys
        Collections.sort(mapKeys);

        // for each bound (i.e. each input table)
        for(int i = 0; i < mapKeys.size(); i++) {

            AdvancableDate start;
            Date end;

            if(i == 0) {
                start = startDate;

                while(DateUtils.dateBefore(mapKeys.get(i + 1), startDate)) {
                    i++;
                }

            } else {

                AdvancableDate prev = mapKeys.get(i-1);
                Date current = mapKeys.get(i);

                start = prev.advanceTime(DateUtils.differenceInMonths(prev, current).getCount() / 2, TimeUnit.MONTH);

            }

            if(i == mapKeys.size() - 1) {
                end = endDate;
            } else {

                Date next = mapKeys.get(i+1);
                AdvancableDate current = mapKeys.get(i);

                end = current.advanceTime(DateUtils.differenceInMonths(current, next).getCount() / 2, TimeUnit.MONTH);

            }

            // get desired data
            OneDimensionDataDistribution desiredSeparation = desired.getSeparationData(start, end);

            // get generated data
            OneDimensionDataDistribution generatedSeparation = generated.getSeparationData(start, end, desiredSeparation.getLargestLabel().getValue());

            // statistical comparison

            // TODO NEXT - get this outputting to file and stats working in R
            // http://stats.stackexchange.com/questions/231059/compare-the-statistical-significance-of-the-difference-between-two-polynomial-re


            // log it

            // output it
            Collection<LabelValueDataRow> table = TableTranformationUtils.transform1DDDToCollectionOfLabelValueDataRow(desiredSeparation, "desired");
            table.addAll(TableTranformationUtils.transform1DDDToCollectionOfLabelValueDataRow(generatedSeparation, "generated"));
            FileUtils.outputDataRowsToStream("label value group", table, makeNamedStream(start, "", EventType.SEPARATION, config));



        }






    }

    private Collection<FailureTimeRow> getTableOfFailureTimes(AdvancableDate date, EventType eventType,
                                                              StatisticalTables expected, StatisticalTables observed,
                                                              IPopulation generatedPopulation)
                                                                throws UnsupportedEventType {

        OneDimensionDataDistribution populationSurvivorTable = observed.getCohortSurvivorTable(date, eventType);
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getCohortSurvivorTable(date, eventType, populationSurvivorTable.getData(0), populationSurvivorTable.getLargestLabel().getMax() - 1, generatedPopulation);

        Collection<FailureTimeRow> rows = TableTranformationUtils.transformSurvivorTableToTableOfOrderedIndividualFailureTime(populationSurvivorTable, "Observed");
        rows.addAll(TableTranformationUtils.transformSurvivorTableToTableOfOrderedIndividualFailureTime(statisticsSurvivorTable, "Desired"));

        return rows;
    }

    private IKaplanMeierAnalysis runKMAnalysisOnCohort(AdvancableDate date, EventType eventType, StatisticalTables expected,
                                                       StatisticalTables observed, IPopulation generatedPopulation,
                                                       Config config)
                                                        throws StatisticalManipulationCalculationError,
                                                        UnsupportedEventType, IOException {

        // get survival tables for all males born in year
        OneDimensionDataDistribution populationSurvivorTable = observed.getCohortSurvivorTable(date, eventType);

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = expected.getCohortSurvivorTable(date, eventType,
                populationSurvivorTable.getData(0),
                populationSurvivorTable.getLargestLabel().getMax() - 1, generatedPopulation);

        if(config.produceGraphs()) {
            new SurvivalPlot(statisticsSurvivorTable, populationSurvivorTable).generatePlot(eventType, date, config);
        }

        // perform KM analysis and log result
        return runKaplanMeier(eventType, statisticsSurvivorTable, populationSurvivorTable, config);

    }

    private IKaplanMeierAnalysis runKMAnalysisForTimePeriod(AdvancableDate date, EventType eventType, CompoundTimeUnit timePeriod, Config config) throws StatisticalManipulationCalculationError, UnsupportedDateConversion, UnsupportedEventType, IOException {
        // get survival tables for all males born in year

        OneDimensionDataDistribution populationSurvivorTable = generated.getTimePeriodSurvivorTable(date, timePeriod, eventType);

        // get equiverlent table from inputs stats
        OneDimensionDataDistribution statisticsSurvivorTable = desired.getTimePeriodSurvivorTable(date, populationSurvivorTable.getLargestLabel().getMax() - 1, eventType);

        if(config.produceGraphs()) {
            new SurvivalPlot(statisticsSurvivorTable, populationSurvivorTable).generatePlot(eventType, date, config);
        }

        Collection<FailureTimeRow> failures = TableTranformationUtils.transformSurvivorTableToTableOfOrderedIndividualFailureTime(populationSurvivorTable, "Observed");
        failures.addAll(TableTranformationUtils.transformSurvivorTableToTableOfOrderedIndividualFailureTime(statisticsSurvivorTable, "Desired"));
        FileUtils.outputFailureTimeRowsToStream(failures, makeNamedStream(date, "-timeperiod", eventType, config));

        // perform KM analysis and log result
        return runKaplanMeier(eventType, statisticsSurvivorTable, populationSurvivorTable, config);

    }

    @Override
    public SummaryRow outputResults(PrintStream resultOutput, SummaryRow summary) {


        int mPasses = 0;
        int mFails = 0;

        int fPasses = 0;
        int fFails = 0;

        int b0Passes = 0;
        int b0Fails = 0;

        resultOutput.println("Year | M Death | F Death | 0 Order |");

        Date[] years = results.keySet().toArray(new Date[results.keySet().size()]);

        Arrays.sort(years, Date::compareTo);
        years = Arrays.copyOfRange(years, 0, years.length - 1);

        int failYear = -1;

        int yearCount = 0;

        for (Date d : years) {
            Map<EventType, IKaplanMeierAnalysis> res = results.get(d);

            resultOutput.print(d.getYear() + " | ");

            try {
                if (printPassAndPValue(EventType.MALE_DEATH, res, resultOutput)) {
                    mPasses++;
                } else {
                    mFails++;
                }
            } catch (PValueInvalidException e) {
                mFails++;
                if(failYear == -1) {
                    failYear = yearCount;
                }
            }

            try {
            if(printPassAndPValue(EventType.FEMALE_DEATH, res, resultOutput)) {
                fPasses++;
            } else {
                fFails++;
            }
            } catch (PValueInvalidException e) {
                fFails++;
                if(failYear == -1) {
                    failYear = yearCount;
                }
            }

            try {
                if (yearCount < years.length - 15) {
                    if (printPassAndPValue(EventType.FIRST_BIRTH, res, resultOutput)) {
                        b0Passes++;
                    } else {
                        b0Fails++;
                    }
                } else {
                    resultOutput.print("        | ");
                }
            } catch (PValueInvalidException e) {
                b0Fails++;
                if(failYear == -1) {
                    failYear = yearCount;
                }
            }

            yearCount++;

            resultOutput.println();
        }

        if(failYear == -1) {
            summary.setCompleted(1);
        } else {
            summary.setCompleted(failYear / (double) years.length);
        }

        int tPasses = mPasses + fPasses + b0Passes;
        int tFails = mFails + fFails + b0Fails;

        resultOutput.println();
        resultOutput.print("Summed Passes and Fails\n");
        resultOutput.print("Male death   - Passes: " + mPasses + "    |    Fails: " + mFails + "\n");
        resultOutput.print("Female death - Passes: " + fPasses + "    |    Fails: " + fFails + "\n");
        resultOutput.print("First births - Passes: " + b0Passes + "    |    Fails: " + b0Fails + "\n");
        resultOutput.print("Totals       - Passes: " + tPasses + "    |    Fails: " + tFails + "\n");

        summary.setmDPasses(mPasses / (double) (mPasses + mFails));
        summary.setfDPasses(fPasses / (double) (fPasses + fFails));
        summary.setbPasses(b0Passes / (double) (b0Passes + b0Fails));

        summary.setPassed((tPasses) / (double) (tPasses + tFails));


        return summary;

    }

    private PrintStream makeNamedStream(Date date, String note, EventType eventType, Config config) {

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
            case SEPARATION:
                fName = "separation";
                break;
            case PARTNERING:
                fName = "partnering";
        }

        fName += "-";

        return FileUtils.setupDatFileAsStream(eventType, fName + date.toOrderableString() + note, config);

    }

    public Collection<FailureTimeRow> getFailureAtTimesTable(AdvancableDate date, EventType event, StatisticalTables expected, StatisticalTables observed, IPopulation generatedPopulation, Config config) throws UnsupportedEventType {

        Collection<FailureTimeRow> rows = observed.getFailureAtTimesTable(date, "Observed", config.getTE(), event);

        int maxAge = getHighestTimeValue(rows);

        int numberInObserved = rows.size() - 1;
        rows.addAll(expected.getFailureAtTimesTable(date, "Desired", config.getTE(), event, (double) numberInObserved, maxAge, generatedPopulation));

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
