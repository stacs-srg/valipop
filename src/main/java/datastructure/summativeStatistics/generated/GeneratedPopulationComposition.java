package datastructure.summativeStatistics.generated;

import datastructure.summativeStatistics.PopulationComposition;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import model.IPopulation;
import model.NotDeadException;
import utils.time.CompoundTimeUnit;
import utils.time.Date;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The GeneratedPopulationComposition interface provides the functionality to be able to access the same information about the
 * simulated population as in the provided population. It also provides methods to retrieve data in the forms required
 * for the various statistical analyses that are used in the verification of the produced population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class GeneratedPopulationComposition implements PopulationComposition {

    private Date startDate;
    private Date endDate;

    private IPopulation population;

    public GeneratedPopulationComposition(Date startDate, Date endDate, IPopulation population) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.population = population;
    }


    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) {

        Collection<IPerson> males = population.getByYearAndSex('m', startYear);

        Map<IntegerRange, Double> counts = new HashMap<IntegerRange, Double>();

        OneDimensionDataDistribution countsTable = new OneDimensionDataDistribution(startYear.getYearDate(), "", "", counts);

        for(IPerson m : males) {

            Integer age = null;
            try {
                age = m.ageAtDeath();
                counts.replace(countsTable.resolveRowValue(age), counts.get(countsTable.resolveRowValue(age)) + 1);
            } catch (InvalidRangeException e) {
                counts.put(new IntegerRange(age), 1.0);
            } catch (NotDeadException e) { /* No need to count in our survivor table as they are beyond the end of our time frame */ }

        }

        Map<IntegerRange, Double> survival = new HashMap<IntegerRange, Double>();

        double survivors = males.size();
        survival.put(new IntegerRange(0), survivors);

        for(int i = 0; i < 100; i++) {
            try {
                survivors -= counts.get(countsTable.resolveRowValue(i));
            } catch (InvalidRangeException e) { /* No deaths at this age*/ }
            survival.put(new IntegerRange(i + 1), survivors);
        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);

    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event, Double scalingFactor, int timeLimit) {
        return getSurvivorTable(startYear, timePeriod, event);
    }
}
