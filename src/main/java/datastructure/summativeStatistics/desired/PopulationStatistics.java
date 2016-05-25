package datastructure.summativeStatistics.desired;

import model.enums.EventType;
import datastructure.summativeStatistics.PopulationComposition;
import config.Config;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.TwoDimensionDataDistribution;
import model.interfaces.dataStores.informationAccess.EventRateTables;
import model.time.DateClock;
import model.time.DateUtils;
import model.time.YearDate;
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
    Map<YearDate, TwoDimensionDataDistribution> orderedBirth;
    Map<YearDate, TwoDimensionDataDistribution> multipleBirth;
    Map<YearDate, OneDimensionDataDistribution> separation;

    public PopulationStatistics(Config config,
                                Map<YearDate, OneDimensionDataDistribution> maleDeath,
                                Map<YearDate, OneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, TwoDimensionDataDistribution> partnering,
                                Map<YearDate, TwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, TwoDimensionDataDistribution> multipleBirth,
                                Map<YearDate, OneDimensionDataDistribution> separation) {

        this.maleDeath = maleDeath;
        this.femaleDeath = femaleDeath;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.separation = separation;

        this.startDate = config.gettS();
        this.endDate = config.gettE();


    }

    @Override
    public DateClock getEarliestDate() {
        return startDate;
    }

    @Override
    public DateClock getLatestDate() {
        return endDate;
    }


    @Override
    public OneDimensionDataDistribution getDeathRates(YearDate year, char gender) {
        if(gender == 'm') {
            return maleDeath.get(getNearestYearInMap(year, maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year, femaleDeath));
        }
    }

    @Override
    public TwoDimensionDataDistribution getPartneringRates(YearDate year) {
        return partnering.get(getNearestYearInMap(year, partnering));
    }

    @Override
    public TwoDimensionDataDistribution getOrderedBirthRates(YearDate year) {
        return orderedBirth.get(getNearestYearInMap(year, orderedBirth));
    }

    @Override
    public TwoDimensionDataDistribution getMultipleBirthRates(YearDate year) {
        return multipleBirth.get(getNearestYearInMap(year, multipleBirth));
    }

    @Override
    public OneDimensionDataDistribution getSeparationByChildCountRates(YearDate year) {
        return separation.get(getNearestYearInMap(year, separation));
    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }

    private YearDate getNearestYearInMap(YearDate year, Map<YearDate, ?> map) {

        int minDifferenceInMonths = Integer.MAX_VALUE;
        YearDate nearestTableYear = null;

        for (YearDate tableYear : map.keySet()) {
            int difference = DateUtils.differenceInMonths(tableYear, year).getCount();
            if(difference < minDifferenceInMonths) {
                minDifferenceInMonths = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;

    }

}
