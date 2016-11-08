package datastructure.summativeStatistics.generated;

import datastructure.population.FemaleCollection;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.PopulationComposition;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.exceptions.NoChildrenOfDesiredOrder;
import model.exceptions.NotDeadException;
import model.simulationEntities.IPartnership;
import model.simulationEntities.IPerson;
import model.simulationEntities.IPopulation;
import model.simulationEntities.Partnership;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.*;
import utils.time.Date;

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

    private PeopleCollection population;

    public static Logger log = LogManager.getLogger(GeneratedPopulationComposition.class);

    public GeneratedPopulationComposition(Date startDate, Date endDate, PeopleCollection population) {
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
    public OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event) throws UnsupportedEventType {

        if (event == EventType.MALE_DEATH || event == EventType.FEMALE_DEATH) {
            return getCohortDeathTable(cohortYear, event);
        }

        if (event == EventType.FIRST_BIRTH) {
            return getCohortFirstBirthTable(cohortYear, event);
        }

        throw new UnsupportedEventType("No method to create survivor table of specified EventType");

    }

    @Override
    public OneDimensionDataDistribution getCohortSurvivorTable(Date cohortYear, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedEventType {
        return getCohortSurvivorTable(cohortYear, event);
    }

    @Override
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, CompoundTimeUnit timePeriod, EventType event) throws UnsupportedEventType, UnsupportedDateConversion {

        Map<IntegerRange, Double> survival = new HashMap<>();

        double survivors = 1000;
        survival.put(new IntegerRange(0), survivors);


        for(int age = 0; age < 100; age ++) {

            int totalDeathsForAge = 0;
            int totalPopulationOfAge = 0;

            for(DateClock d = startYear.getDateClock(); DateUtils.dateBefore(d, startYear.getDateClock().advanceTime(timePeriod)); d = d.advanceTime(1, TimeUnit.YEAR)) {
//                System.out.println(d.rowAsString());
                Collection<IPerson> people = population.getByYear(d.advanceTime(new CompoundTimeUnit(age, TimeUnit.YEAR).negative()));

                for(IPerson person : people) {
                    try {
                        if(person.ageAtDeath() == age) {
                            totalDeathsForAge++;
                        }
                    } catch (NotDeadException e) {
                        // No need to handle
                    }

                    if(person.aliveOnDate(d.advanceTime(6, TimeUnit.MONTH))) {
//                    if(age != 0 && person.aliveOnDate(d)) {
                        totalPopulationOfAge++;
                    } else if (person.aliveOnDate(d.advanceTime(1, TimeUnit.YEAR))) {
                        totalPopulationOfAge++;
                    }

                }

//                System.out.println(d.rowAsString() + " " + age + ": " + totalDeathsForAge + " / " + totalPopulationOfAge);

            }

            double nMxForAge;

            if(totalPopulationOfAge != 0) {
                nMxForAge = totalDeathsForAge / (double) totalPopulationOfAge;
            } else {
                nMxForAge = 0;
            }

            survivors = survivors * (1 - nMxForAge);

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
    public OneDimensionDataDistribution getTimePeriodSurvivorTable(Date startYear, int ageLimit, EventType event) throws UnsupportedEventType {
        return null;
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event) {

        if(event == EventType.MALE_DEATH) {

            Collection<IPerson> people = population.getByYearAndSex('m', year);



            return getDeathAtTimesTable(people, denoteGroupAs, simulationEndDate);

        } else if(event == EventType.FEMALE_DEATH) {

            Collection<IPerson> people = population.getByYearAndSex('f', year);

            return getDeathAtTimesTable(people, denoteGroupAs, simulationEndDate);

        }
//        else if(event == EventType.FIRST_BIRTH) {
//
//            Collection<IPerson> people = population.getByYearAndSex('f', year);
//
//
//        }

        // TODO implement for birth

        return null;
    }

    @Override
    public Collection<FailureTimeRow> getFailureAtTimesTable(Date year, String denoteGroupAs, Date simulationEndDate, EventType event, Double scalingFactor, int timeLimit, IPopulation generatedPopulation) throws UnsupportedDateConversion {
        return null;

    }

    @Override
    public Collection<YearDate> getDataYearsInMap(EventType maleDeath) {
        return null;
    }

    @Override
    public OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear) throws UnsupportedDateConversion {
        return null;
    }


    @Override
    public OneDimensionDataDistribution getSeparationData(Date startYear, Date endYear, int childrenCap) throws UnsupportedDateConversion {

        int marriagesActiveInEachYearOfTimePeriod = 0;
        int[] separationCounts = new int[childrenCap];

        for(DateClock d = startYear.getDateClock(); DateUtils.dateBefore(d, endYear); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // count number of marriages in year and inc total

            for(IPerson p : population.getFemales().getAll()) {

                if(p.aliveOnDate(d) && p.getPartnerships().size() != 0 && !p.isWidow(d)) {
                    // inc marriages count
                    marriagesActiveInEachYearOfTimePeriod ++;

                }

            }

            // find each childborn in year
            Collection<IPerson> cohort = population.getByYear(d);

            for(IPerson child : cohort) {
                IPartnership p = child.isInstigatorOfSeparationOfMothersPreviousPartnership();

                if (p != null) {

                    int numberOfChildren = p.getChildren().size();

                    if(numberOfChildren > childrenCap) {
                        numberOfChildren = childrenCap;
                    }

                    separationCounts[numberOfChildren - 1] ++;

                }

            }



        }

        Map<IntegerRange, Double> tableData = new HashMap<>();

        // process for multiple children in preg
        for(int c = 0; c < childrenCap; c++) {
            separationCounts[c] = separationCounts[c] / (c + 1);
            tableData.put(new IntegerRange(c + 1), separationCounts[c] / (double) marriagesActiveInEachYearOfTimePeriod);

        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "generated", "", tableData);

    }

    @Override
    public OneDimensionDataDistribution getPartneringData(Date startYear, Date endYear, IntegerRange femaleAgeRange, Set<IntegerRange> maleAgeBrackets) {

        ArrayList<IntegerRange> extendedMaleAgeBracketsList = new ArrayList<>();
        extendedMaleAgeBracketsList.addAll(maleAgeBrackets);

        Map<IntegerRange, Integer> map = new HashMap<>();
        int sum = 0;

        for(IPartnership p : population.getPartnerships()) {

            Date partnershipDate = p.getPartnershipDate();

            // if event happened in interested time period
            if(DateUtils.dateBefore(startYear, partnershipDate) && DateUtils.dateBefore(partnershipDate, endYear)) {

                int femaleAgeAtEvent = DateUtils.differenceInYears(p.getFemalePartner().getBirthDate(), partnershipDate).getCount();

                // if female in age bracket
                if(femaleAgeRange.contains(femaleAgeAtEvent)) {

                    Integer maleAgeAtEvent = null;

                    try {
                        maleAgeAtEvent = DateUtils.differenceInYears(p.getMalePartner().getBirthDate(), partnershipDate).getCount();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    IntegerRange row = resolveRow(maleAgeAtEvent, extendedMaleAgeBracketsList);
                    try {
                        map.put(row, map.get(row) + 1);
                    } catch (NullPointerException e) {
                        map.put(row, 1);
                        extendedMaleAgeBracketsList.add(row);
                    }
//                    counts[maleAgeAtEvent]++;
                    sum++;

                }

            }

        }

        Map<IntegerRange, Double> ret = new HashMap<>();

        for(IntegerRange iR : map.keySet()) {

            ret.put(iR, map.get(iR) / (double) sum);

        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "Generated", "", ret);
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getPartneringData(Date StartYear, Date endYear) {
        return null;
    }

    private Collection<FailureTimeRow> getDeathAtTimesTable(Collection<IPerson> people, String denoteGroupAs, Date simulationEndDate) {

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

    private OneDimensionDataDistribution getCohortFirstBirthTable(Date startYear, EventType event) throws UnsupportedEventType {

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

    private OneDimensionDataDistribution getCohortDeathTable(Date startYear, EventType event) throws UnsupportedEventType {

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

//            // CHECK this
//            if(survivors != 0)
                survival.put(new IntegerRange(i + 1), survivors);
        }

        return new OneDimensionDataDistribution(startYear.getYearDate(), "", "", survival);
    }

    private IntegerRange resolveRow(Integer i, List<IntegerRange> rows) {

        for (IntegerRange iR : rows) {
            if (iR.contains(i)) {
                return iR;
            }
        }

        return new IntegerRange(i);
    }

}
