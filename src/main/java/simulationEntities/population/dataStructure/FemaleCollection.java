package simulationEntities.population.dataStructure;

import dateModel.Date;
import dateModel.DateUtils;
import dateModel.MisalignedTimeDivisionError;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.MonthDate;
import dateModel.dateImplementations.YearDate;
import dateModel.exceptions.UnsupportedDateConversion;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulationEntities.partnership.IPartnership;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import utils.MapUtils;

import java.util.*;


/**
 * The FemaleCollection is a specialised concrete implementation of a PersonCollection. The implementation offers an
 * additional layer of division below the year of birth level which divides females out into separate collections based
 * on how many children they have had.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection extends PersonCollection {

    private static final Logger log = LogManager.getLogger(FemaleCollection.class);
    private final Map<MonthDate, Map<Integer, Collection<IPerson>>> byBirthYearAndNumberOfChildren = new HashMap<>();

    /**
     * Instantiates a new FemaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the FemaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the FemaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public FemaleCollection(AdvancableDate start, Date end, CompoundTimeUnit divisionSize) {
        super(start, end, divisionSize);

        for (AdvancableDate d = start; DateUtils.dateBefore(d, end); d = d.advanceTime(divisionSize)) {
            byBirthYearAndNumberOfChildren.put(d.getMonthDate(), new HashMap<>());
        }
    }

    /*
    -------------------- Specialised female methods --------------------
     */

    /**
     * Returns the highest birth order (number of children) among women in the specified year of birth.
     *
     * @param dateOfBirth the year of birth of the mothers in question
     * @return the highest birth order value
     */
    public int getHighestBirthOrder(AdvancableDate dateOfBirth, CompoundTimeUnit period) {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(period, getDivisionSize());

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        ArrayList<IPerson> people = new ArrayList<>();
        MonthDate divisionDate = dateOfBirth.getMonthDate();

        int highestBirthOrder = 0;

        for(int i = 0; i < divisionsInPeriod; i++) {

            Map<Integer, Collection<IPerson>> temp = byBirthYearAndNumberOfChildren.get(divisionDate);

            if(temp != null && MapUtils.getMax(temp.keySet()) > highestBirthOrder) {
                highestBirthOrder = MapUtils.getMax(temp.keySet());
            }

            // move on to the new division date until we've covered the required divisions
            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return highestBirthOrder;

    }

    /**
     * Gets the {@link Collection} of mothers born in the give year with the specified birth order (i.e. number of
     * children)
     *
     * @param date             the date
     * @param period           the period following the date to find people from
     * @param birthOrder       the number of children
     * @return the by number of children
     */
    public Collection<IPerson> getByDatePeriodAndBirthOrder(AdvancableDate date, CompoundTimeUnit period, Integer birthOrder) {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(period, getDivisionSize());

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        ArrayList<IPerson> people = new ArrayList<>();
        MonthDate divisionDate = date.getMonthDate();

        for(int i = 0; i < divisionsInPeriod; i++) {

            try {
                people.addAll(byBirthYearAndNumberOfChildren.get(divisionDate).get(birthOrder));
            } catch (NullPointerException e) {
                // If no data exists for the year or the given birth order in the given year, then there's no one to add
            }

            // move on to the new division date until we've covered the required divisions
            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return people;

    }

    /**
     * Gets a {@link Collection} size n of females born in the given year with the specified birth order. When returned
     * these individuals have been removed from the population structure and need to be re-added if they are still to
     * exist in the given population structure.
     *
     * For example, if you have removed these females as they are set to die, then adding them back into the population
     * would not be expected (rather you may add then to the population structure of dead people). However, if you have
     * removed them as to use them to birth new children then they should be re-added to the population structure and at
     * that point will be placed into the location corresponding to their new birth order.
     *
     * @param numberToRemove the number to remove
     * @param yearOfBirth    the year of birth of the female
     * @param birthOrder     the birth order (i.e. number of children)
     * @param currentDate    the current date
     * @return the collection the set of people who have been removed
     * @throws InsufficientNumberOfPeopleException the insufficient number of people exception
     */
    public Collection<IPerson> removeNPersons(int numberToRemove, AdvancableDate dateOfBirth, CompoundTimeUnit period,
                                              int birthOrder, MonthDate currentDate)
                                                            throws InsufficientNumberOfPeopleException {

        Collection<IPerson> toReturn = new ArrayList<>();

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(period, getDivisionSize());

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        ArrayList<IPerson> people = new ArrayList<>();
        MonthDate divisionDate = dateOfBirth.getMonthDate();

        ArrayList<MonthDate> reusableDivisions = new ArrayList<>();
        int numberToRemoveFromDivision = numberToRemove / divisionsInPeriod;

        for(int i = 0; i < divisionsInPeriod; i++) {

            try {
                removeNPeopleFromDivisionStartingOnDate(numberToRemoveFromDivision, divisionDate, birthOrder, currentDate);
            } catch (InsufficientNumberOfPeopleException e) {
                // TODO NEXT Writing code here
            }


            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return toReturn;

    }

    private Collection<IPerson> removeNPeopleFromDivisionStartingOnDate(int numberToRemove, MonthDate divisionDate, int birthOrder, MonthDate currentDate) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> people = new ArrayList<>();

        Collection<IPerson> toReturn = new ArrayList<>();

        if (numberToRemove == 0) {
            return people;
        }

        while (people.size() < numberToRemove) {

            LinkedList<IPerson> orderedBirthCohort = new LinkedList<>(byBirthYearAndNumberOfChildren.get(divisionDate).get(birthOrder));

            if(orderedBirthCohort.size() <= 0) {
                throw new InsufficientNumberOfPeopleException("Ran out of people");
            }


            IPerson p = orderedBirthCohort.removeFirst();
            if(countChildren(p) != birthOrder) {
                System.out.println("BOOM");
            }

            try {
                removePerson(p);

                if (p.noRecentChildren(currentDate, new CompoundTimeUnit(-9, TimeUnit.MONTH))) {
                    people.add(p);
                } else {
                    toReturn.add(p);
                }


            } catch (PersonNotFoundException e) {
                System.out.println("This really shouldn't be happening");
                toReturn.add(p);
            }

        }

        for(IPerson p : toReturn) {
            addPerson(p);
        }

        return people;
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<>();

        for (YearDate t : byBirthYearAndNumberOfChildren.keySet()) {
            for (Integer i : byBirthYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byBirthYearAndNumberOfChildren.get(t).get(i));
            }
        }

        return people;
    }

    @Override
    public Collection<IPerson> getByYear(Date yearOfBirth) {

        Collection<IPerson> people = new ArrayList<>();

        try {
            for (Integer i : byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate()).keySet()) {
                people.addAll(byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate()).get(i));
            }
        } catch (NullPointerException e) {
            // No need to do anything - we allow the method to return an empty list as no one was born in the year
        }

        return people;
    }

    @Override
    public void addPerson(IPerson person) {
        try {
            byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            // If years or birth order doesn't exist in map then add missing part of map and add in person
            try {
                byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).put(countChildren(person), new ArrayList<>());
                // If the year existed but the correct birth order did not
                byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                // If the year didn't exist in the map
                Map<Integer, Collection<IPerson>> temp = new HashMap<>();
                temp.put(countChildren(person), new ArrayList<>());
                temp.get(countChildren(person)).add(person);
                byBirthYearAndNumberOfChildren.put(person.getBirthDate().getYearDate(), temp);
            }
        }
    }

    @Override
    public void removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));

        // Removal of person AND test for removal (all in second clause of the if statement)
        if (people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in data structure");
        }
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    @Override
    public int getNumberOfPersons(Date yearOfBirth) {

        Map<Integer, Collection<IPerson>> temp = byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate());

        // No data about the given year is treated as no body born in that year
        if(temp == null) {
            return 0;
        } else {
            return MapUtils.countObjectsInCollectionsInMap(temp);
        }
    }

    @Override
    public Set<Date> getDivisionDates() {
        return null;
    }

    @Override
    public Set<YearDate> getYOBs() {
        return new HashSet<>(byBirthYearAndNumberOfChildren.keySet());
    }

    /*
    -------------------- Private helper methods --------------------
     */

    private Integer countChildren(IPerson person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

    public boolean verify() {

        boolean passed = true;

        for(YearDate y : byBirthYearAndNumberOfChildren.keySet()) {

            Map<Integer, Collection<IPerson>> birthCohort = byBirthYearAndNumberOfChildren.get(y);

            for(Integer birthOrder : birthCohort.keySet()) {

                Collection<IPerson> orderedBirthCohort = birthCohort.get(birthOrder);

                for(IPerson p : orderedBirthCohort) {
                    if(p.getBirthDate().getYear() != y.getYear()
                            || p.numberOfChildren() != birthOrder) {
                        passed = false;
                        System.out.println("F: " + p.getId() + " YOB: " + p.getBirthDate().getYear() + " ("
                                + y.getYear() + ") order: " + p.numberOfChildren() + " (" + birthOrder + ")");
                    }
                }

            }


        }


        return passed;
    }
}