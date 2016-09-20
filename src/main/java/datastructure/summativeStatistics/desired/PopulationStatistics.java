package datastructure.summativeStatistics.desired;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.PopulationComposition;
import config.Config;
import datastructure.summativeStatistics.generated.UnsupportedEventType;
import datastructure.summativeStatistics.structure.*;
import datastructure.summativeStatistics.EventRateTables;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import model.simulationEntities.IPopulation;
import utils.CollectionUtils;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements PopulationComposition, EventRateTables {

    private DateClock startDate;
    private DateClock endDate;

    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> partnering;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<YearDate, TwoDimensionDataDistribution> multipleBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> separation;

    public PopulationStatistics(Config config,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> partnering,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, TwoDimensionDataDistribution> multipleBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> separation) {

        this.maleDeath = maleDeath;
        this.femaleDeath = femaleDeath;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.separation = separation;

        this.startDate = config.getTS();
        this.endDate = config.getTE();

    }

    /*
    -------------------- DateBounds interface methods --------------------
     */

    @Override
    public DateClock getStartDate() {
        return startDate;
    }

    @Override
    public DateClock getEndDate() {
        return endDate;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    @Override
    public SelfCorrectingOneDimensionDataDistribution getDeathRates(Date year, char gender) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleDeath.get(getNearestYearInMap(year.getYearDate(), maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year.getYearDate(), femaleDeath));
        }
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getPartneringRates(Date year) {
        return partnering.get(getNearestYearInMap(year.getYearDate(), partnering));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(Date year) {
        return orderedBirth.get(getNearestYearInMap(year.getYearDate(), orderedBirth));
    }

    @Override
    public TwoDimensionDataDistribution getMultipleBirthRates(Date year) {
        return multipleBirth.get(getNearestYearInMap(year.getYearDate(), multipleBirth));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getSeparationByChildCountRates(Date year) {
        return separation.get(getNearestYearInMap(year.getYearDate(), separation));
    }

    /*
    -------------------- StatisticalTables interface methods --------------------
     */

    @Override
    public OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event) {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = scalingFactor;
        survival.put(new IntegerRange(0), survivors);

        int age = 0;
        for (DateClock d = cohortYear.getDateClock(); DateUtils.dateBefore(d, cohortYear.getDateClock().advanceTime(timeLimit, TimeUnit.YEAR)); d = d.advanceTime(1, TimeUnit.YEAR)) {

            double nMx = 0;

            switch (event) {
                case FIRST_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 0, generatedPopulation, survivors);
                    break;
                case SECOND_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 1, generatedPopulation, survivors);
                    break;
                case THIRD_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 2, generatedPopulation, survivors);
                    break;
                case FOURTH_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 3, generatedPopulation, survivors);
                    break;
                case FIFTH_BIRTH:
                    nMx = calculateOrderedBirthRate(cohortYear, d, age, 4, generatedPopulation, survivors);
                    break;
                case MALE_DEATH:
                    nMx = getDeathRates(d, 'm').getData(age);
                    break;
                case FEMALE_DEATH:
                    nMx = getDeathRates(d, 'f').getData(age);
                    break;
            }


//            int n = timePeriod.getCount();


            // EDIT - put nQx back in here
//            double nQx = (n * nMx) / (1 + (n * 0.5 * nMx));


            survivors = survivors * (1 - nMx);

            if(survivors - (int)survivors < 0.5) {
                survivors = (int) survivors;
            } else {
                survivors = (int) survivors + 1;
            }



            // TEMP ive taken the rounding out of here on survivors and a +1 off age - NOW PUT BACK IN
            survival.put(new IntegerRange(age + 1), survivors);

            age++;

        }


        return new OneDimensionDataDistribution(cohortYear.getYearDate(), "", "", survival);
    }

    @Override
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType {

        return null;
    }

    // Returns the survival table based on only using the statistics from the given startYear. Top survivor value will be 100,000.
    @Override
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, int ageLimit, EventType event) throws UnsupportedEventType {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = 1000;
        survival.put(new IntegerRange(0), survivors);


        for(int age = 0; age < ageLimit; age++) {

            double nMx = 0;

            switch (event) {
                case FIRST_BIRTH:
                case SECOND_BIRTH:
                case THIRD_BIRTH:
                case FOURTH_BIRTH:
                case FIFTH_BIRTH:
                    System.err.println("Not implemented - PopulationStatistics.getTimePeriodSurvivorTable");
                    break;
                case MALE_DEATH:
                    nMx = getDeathRates(startYear, 'm').getData(age);
                    break;
                case FEMALE_DEATH:
                    nMx = getDeathRates(startYear, 'f').getData(age);
                    break;
            }

            survivors = survivors * (1 - nMx);

            if(survivors - (int)survivors < 0.5) {
                survivors = (int) survivors;
            } else {
                survivors = (int) survivors + 1;
            }



            // TEMP ive taken the rounding out of here on survivors and a +1 off age - NOW PUT BACK IN
            survival.put(new IntegerRange(age + 1), survivors);


        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event) {
        return null;
    }


    // TODO NEXT Make PopulationStatistics.getFailureAtTimesTable() a util method that takes in a survivor table and restuns the appropriate collection of FailureTimeRows
    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion {

        Collection<FailureTimeRow> rows = new ArrayList<>();

        OneDimensionDataDistribution survivorTable = getCohortSurvivorTable(year, event, scalingFactor, timeLimit, generatedPopulation);

        double prevSurvivors = scalingFactor + 1;

        for(int i = 1; i < timeLimit; i++) {

            double currentSurvivors = survivorTable.getData(i);
            double dead = prevSurvivors - currentSurvivors;

            int d = (int) dead;

            prevSurvivors -= d;

            for(int r = 0 ; r < d ; r++) {
                rows.add(new FailureTimeRow(i, true, denoteGroupAs));
            }

        }

        for(int s = 0; s < prevSurvivors; s++) {
            rows.add(new FailureTimeRow(timeLimit, false, denoteGroupAs));
        }

        return rows;
    }

    @Override
    public Collection<YearDate> getDataYearsInMap(EventType eventType) {
        switch(eventType) {

            case FIRST_BIRTH:
            case SECOND_BIRTH:
            case THIRD_BIRTH:
            case FOURTH_BIRTH:
            case FIFTH_BIRTH:
                return orderedBirth.keySet();
            case MALE_DEATH:
                return maleDeath.keySet();
            case FEMALE_DEATH:
                return femaleDeath.keySet();
            case SEPARATION:
                return separation.keySet();
        }

        return Collections.emptyList();
    }

    @Override
    public OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear) throws UnsupportedDateConversion {
        return getSeparationByChildCountRates(startYear);
    }

    @Override
    public OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear, int childCap) throws UnsupportedDateConversion {
        return getSeparationData(startYear, endYear);
    }

    /*
    --------------------- Private Helper Methods ---------------------
     */

    private double calculateOrderedBirthRate(Date startYear, Date currentDate, int age, int birthOrder, IPopulation generatedPopulation, double survivors) {
        SelfCorrectingTwoDimensionDataDistribution orderedBirthRates = getOrderedBirthRates(currentDate);

        OneDimensionDataDistribution aSOBR;
        try {
            aSOBR = orderedBirthRates.getData(age);
        } catch (InvalidRangeException e) {
            return 0.0;
        }

        int t = CollectionUtils.countPeopleInCollectionAliveOnDate(generatedPopulation.getByYearAndSex('f', startYear), currentDate);
        double r = aSOBR.getData(birthOrder);
        return (r * t) / survivors;
    }

    private YearDate getNearestYearInMap(Date year, Map<YearDate, ?> map) {

        int minDifferenceInMonths = Integer.MAX_VALUE;
        YearDate nearestTableYear = null;

        ArrayList<YearDate> orderedKeySet = new ArrayList<>(map.keySet());
        Collections.sort(orderedKeySet);


        for (YearDate tableYear : orderedKeySet) {
            int difference = DateUtils.differenceInMonths(tableYear, year.getYearDate()).getCount();
            if (difference < minDifferenceInMonths) {
                minDifferenceInMonths = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;

    }


}
