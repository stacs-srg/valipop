package datastructure.summativeStatistics.desired;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.PopulationComposition;
import config.Config;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import datastructure.summativeStatistics.structure.TwoDimensionDataDistribution;
import datastructure.summativeStatistics.EventRateTables;
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

    DateClock startDate;
    DateClock endDate;

    Map<YearDate, OneDimensionDataDistribution> maleDeath;
    Map<YearDate, OneDimensionDataDistribution> femaleDeath;
    Map<YearDate, TwoDimensionDataDistribution> partnering;
    Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    Map<YearDate, TwoDimensionDataDistribution> multipleBirth;
    Map<YearDate, OneDimensionDataDistribution> separation;

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

    @Override
    public DateClock getStartDate() {
        return startDate;
    }

    @Override
    public DateClock getEndDate() {
        return endDate;
    }


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

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event, Double scalingFactor) throws UnsupportedDateConversion {

        Map<IntegerRange, Double> survival = new HashMap<IntegerRange, Double>();

        double survivors = scalingFactor;
        survival.put(new IntegerRange(0), survivors);


        int age = 0;
        for(DateClock d = startYear.getDateClock(); DateUtils.dateBefore(d, startYear.getDateClock().advanceTime(100, TimeUnit.YEAR)); d = d.advanceTime(1, TimeUnit.YEAR)) {
            double nMx = getDeathRates(d, 'm').getData(age);
            int n = timePeriod.getCount();

            double nQx = (n * nMx) / (1 + (n * 0.5 * nMx));

            survivors = survivors * (1 - nQx);

            survival.put(new IntegerRange(age + 1), survivors);

            age++;
        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }


}
