package model.implementation.populationStatistics;

import model.enums.EventType;
import model.implementation.analysis.PopulationComposition;
import model.implementation.config.Config;
import model.interfaces.dataStores.informationAccess.EventRateTables;
import model.interfaces.dataStores.informationPassing.tableTypes.OneWayTable;
import model.time.DateClock;
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
            return maleDeath.get(year);
        } else {
            return femaleDeath.get(year);
        }
    }

    @Override
    public TwoDimensionDataDistribution getPartneringRates(YearDate year) {
        return partnering.get(year);
    }

    @Override
    public TwoDimensionDataDistribution getOrderedBirthRates(YearDate year) {
        return orderedBirth.get(year);
    }

    @Override
    public TwoDimensionDataDistribution getMultipleBirthRates(YearDate year) {
        return multipleBirth.get(year);
    }

    @Override
    public OneDimensionDataDistribution getSeparationByChildCountRates(YearDate year) {
        return separation.get(year);
    }

    @Override
    public OneWayTable<Integer> getSurvivorTable(int startYear, int timePeriod, EventType event) {
        return null;
    }
}
