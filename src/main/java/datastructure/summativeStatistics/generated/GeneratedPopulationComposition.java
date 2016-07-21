package datastructure.summativeStatistics.generated;

import datastructure.summativeStatistics.PopulationComposition;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import model.IPopulation;
import model.NoChildrenOfDesiredOrder;
import model.NotDeadException;
import model.simulationLogic.Simulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateUtils;
import utils.time.UnsupportedDateConversion;

import java.util.*;

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

    public static Logger log = LogManager.getLogger(GeneratedPopulationComposition.class);

    public GeneratedPopulationComposition(Date startDate, Date endDate, IPopulation population) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.population = population;
    }

    /*
    -------------------- DateBounds interface methods --------------------
     */

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    /*
    -------------------- StatisticalTables interface methods --------------------
     */

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType {

        if (event == EventType.MALE_DEATH || event == EventType.FEMALE_DEATH) {
            return getDeathTable(startYear, timePeriod, event);
        }

        if (event == EventType.FIRST_BIRTH) {
            return getFirstBirthTable(startYear, timePeriod, event);
        }

        throw new UnsupportedEventType("No method to create survivor table of specified EventType");

    }

    @Override
    public OneDimensionDataDistribution getSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType {
        return getSurvivorTable(startYear, timePeriod, event);
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, int denoteGroupAs, Date simulationEndDate, EventType event) {

        if(event == EventType.MALE_DEATH) {

            Collection<IPerson> people = population.getByYearAndSex('m', year);

            return getDeathAtTimesTable(people, denoteGroupAs, simulationEndDate);

        } else if(event == EventType.FEMALE_DEATH) {

            Collection<IPerson> people = population.getByYearAndSex('f', year);

            return getDeathAtTimesTable(people, denoteGroupAs, simulationEndDate);

        }

        return null;
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, int denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion {
        return null;
    }

    private Collection<FailureTimeRow> getDeathAtTimesTable(Collection<IPerson> people, int denoteGroupAs, Date simulationEndDate) {

        Collection<FailureTimeRow> rows = new ArrayList<>();

        for(IPerson person : people) {
            if(person.aliveOnDate(simulationEndDate)) {
                int timeElapsed = DateUtils.differenceInYears(person.getBirthDate(), simulationEndDate).getCount() + 1;
                rows.add(new FailureTimeRow(timeElapsed, false, denoteGroupAs));
            } else {
                int timeElapsed = 0;
                try {
                    timeElapsed = person.ageAtDeath() + 1;
                    rows.add(new FailureTimeRow(timeElapsed, true, denoteGroupAs));
                } catch (NotDeadException e) {
                    log.fatal("Could not create death at times table - this should have been handled by surrounding if statement");
                }
            }
        }

        return rows;
    }

    /*
    -------------------- Specialised table creation methods --------------------
     */

    private OneDimensionDataDistribution getFirstBirthTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType {

        Collection<IPerson> women = population.getByYearAndSex('f', startYear);

        Map<IntegerRange, Double> counts = new HashMap<IntegerRange, Double>();

        OneDimensionDataDistribution countsTable = new OneDimensionDataDistribution(startYear.getYearDate(), "", "", counts);

        int maxAge = 0;

        for (IPerson w : women) {

            Integer age = null;
            try {
                age = w.ageAtFirstChild();

                if (age > maxAge) {
                    maxAge = age;
                }

                counts.replace(countsTable.resolveRowValue(age), counts.get(countsTable.resolveRowValue(age)) + 1);

            } catch (InvalidRangeException e) {
                counts.put(new IntegerRange(age), 1.0);
            } catch (NoChildrenOfDesiredOrder noChildrenOfDesiredOrder) { /* No need to count*/ }

        }

        Map<IntegerRange, Double> survival = new HashMap<IntegerRange, Double>();

        double survivors = women.size();
        survival.put(new IntegerRange(0), survivors);

        for (int i = 0; i < maxAge; i++) {
            try {
                survivors -= counts.get(countsTable.resolveRowValue(i));
            } catch (InvalidRangeException e) { /* No deaths at this age*/ }

            survival.put(new IntegerRange(i + 1), survivors);
        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);

    }

    private OneDimensionDataDistribution getDeathTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType {

        char sex;

        if (event == EventType.MALE_DEATH) {
            sex = 'm';
        } else if (event == EventType.FEMALE_DEATH) {
            sex = 'f';
        } else {
            throw new UnsupportedEventType("EventType must be a death type for this method");
        }


        Collection<IPerson> people = population.getByYearAndSex(sex, startYear);

        Map<IntegerRange, Double> counts = new HashMap<IntegerRange, Double>();

        OneDimensionDataDistribution countsTable = new OneDimensionDataDistribution(startYear.getYearDate(), "", "", counts);

        int maxAge = 0;

        for (IPerson m : people) {

            Integer age = null;
            try {
                age = m.ageAtDeath();

                if (age > maxAge) {
                    maxAge = age;
                }

                counts.replace(countsTable.resolveRowValue(age), counts.get(countsTable.resolveRowValue(age)) + 1);
            } catch (InvalidRangeException e) {
                counts.put(new IntegerRange(age), 1.0);
            } catch (NotDeadException e) { /* No need to count in our survivor table as they are beyond the end of our time frame */ }

        }

        Map<IntegerRange, Double> survival = new HashMap<IntegerRange, Double>();

        double survivors = people.size();
        survival.put(new IntegerRange(0), survivors);

        for (int i = 0; i <= maxAge; i++) {
            try {
                survivors -= counts.get(countsTable.resolveRowValue(i));
            } catch (InvalidRangeException e) { /* No deaths at this age*/ }

            survival.put(new IntegerRange(i + 1), survivors);
        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }
}
