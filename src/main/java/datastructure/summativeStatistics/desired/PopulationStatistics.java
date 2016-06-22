package datastructure.summativeStatistics.desired;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.PopulationComposition;
import config.Config;
import datastructure.summativeStatistics.structure.*;
import datastructure.summativeStatistics.EventRateTables;
import model.IPopulation;
import utils.CollectionUtils;
import utils.time.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements PopulationComposition, EventRateTables {

    private DateClock startDate;
    private DateClock endDate;

    private Map<YearDate, OneDimensionDataDistribution> maleDeath;
    private Map<YearDate, OneDimensionDataDistribution> femaleDeath;
    private Map<YearDate, TwoDimensionDataDistribution> partnering;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<YearDate, TwoDimensionDataDistribution> multipleBirth;
    private Map<YearDate, OneDimensionDataDistribution> separation;

    public PopulationStatistics(Config config,
                                Map<YearDate, OneDimensionDataDistribution> maleDeath,
                                Map<YearDate, OneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, TwoDimensionDataDistribution> partnering,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, TwoDimensionDataDistribution> multipleBirth,
                                Map<YearDate, OneDimensionDataDistribution> separation) {

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
    public OneDimensionDataDistribution getDeathRates(Date year, char gender) {
        if (Character.toLowerCase(gender) == 'm') {
            return maleDeath.get(getNearestYearInMap(year.getYearDate(), maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year.getYearDate(), femaleDeath));
        }
    }

    @Override
    public TwoDimensionDataDistribution getPartneringRates(Date year) {
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
    public OneDimensionDataDistribution getSeparationByChildCountRates(Date year) {
        return separation.get(getNearestYearInMap(year.getYearDate(), separation));
    }

    /*
    -------------------- StatisticalTables interface methods --------------------
     */

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = scalingFactor;
        survival.put(new IntegerRange(0), survivors);

        int age = 0;
        for (DateClock d = startYear.getDateClock(); DateUtils.dateBefore(d, startYear.getDateClock().advanceTime(timeLimit, TimeUnit.YEAR)); d = d.advanceTime(1, TimeUnit.YEAR)) {

            double nMx = 0;

            switch (event) {
                case FIRST_BIRTH:
                    nMx = calculateOrderedBirthRate(startYear, d, age, 0, generatedPopulation, survivors);
                    break;
                case SECOND_BIRTH:
                    nMx = calculateOrderedBirthRate(startYear, d, age, 1, generatedPopulation, survivors);
                    break;
                case THIRD_BIRTH:
                    nMx = calculateOrderedBirthRate(startYear, d, age, 2, generatedPopulation, survivors);
                    break;
                case FOURTH_BIRTH:
                    nMx = calculateOrderedBirthRate(startYear, d, age, 3, generatedPopulation, survivors);
                    break;
                case FIFTH_BIRTH:
                    nMx = calculateOrderedBirthRate(startYear, d, age, 4, generatedPopulation, survivors);
                    break;
                case MALE_DEATH:
                    nMx = getDeathRates(d, 'm').getData(age);
                    break;
                case FEMALE_DEATH:
                    nMx = getDeathRates(d, 'f').getData(age);
                    break;
            }


            int n = timePeriod.getCount();

            double nQx = (n * nMx) / (1 + (n * 0.5 * nMx));


            survivors = survivors * (1 - nQx);

            survival.put(new IntegerRange(age + 1), survivors);

            age++;

        }


        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }

    /*
    --------------------- Private Helper Methods ---------------------
     */

    private double calculateOrderedBirthRate(Date startYear, Date currentDate, int age, int birthOrder, IPopulation generatedPopulation, double survivors) {
        TwoDimensionDataDistribution orderedBirthRates = getOrderedBirthRates(currentDate);

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

        for (YearDate tableYear : map.keySet()) {
            int difference = DateUtils.differenceInMonths(tableYear, year.getYearDate()).getCount();
            if (difference < minDifferenceInMonths) {
                minDifferenceInMonths = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;

    }


}
