/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.MisalignedTimeDivisionError;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.utils.MapUtils;

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
    private final TreeMap<MonthDate, TreeMap<Integer, Collection<IPersonExtended>>> byBirthYearAndNumberOfChildren = new TreeMap<>();

    /**
     * Instantiates a new FemaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the FemaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the FemaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    public FemaleCollection(AdvancableDate start, uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date end, CompoundTimeUnit divisionSize) {
        super(start, end, divisionSize);

        for (AdvancableDate d = start; DateUtils.dateBeforeOrEqual(d, end); d = d.advanceTime(divisionSize)) {
            byBirthYearAndNumberOfChildren.put(d.getMonthDate(), new TreeMap<>());
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

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        MonthDate divisionDate = dateOfBirth.getMonthDate();

        int highestBirthOrder = 0;

        for(int i = 0; i < divisionsInPeriod; i++) {

            Map<Integer, Collection<IPersonExtended>> temp = byBirthYearAndNumberOfChildren.get(divisionDate);

            if(temp != null && MapUtils.getMax(temp.keySet()) > highestBirthOrder) {
                highestBirthOrder = MapUtils.getMax(temp.keySet());
            }

            // move on to the new division date until we've covered the required divisions
            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return highestBirthOrder;

    }

    public Set<Integer> getBirthOrdersInDivision(AdvancableDate dateOfBirth, CompoundTimeUnit period) {
        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        MonthDate divisionDate = dateOfBirth.getMonthDate();

        HashSet<Integer> orders = new HashSet<>();

        for(int i = 0; i < divisionsInPeriod; i++) {

            Map<Integer, Collection<IPersonExtended>> temp = byBirthYearAndNumberOfChildren.get(divisionDate);
            orders.addAll(temp.keySet());


            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return orders;

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
    public Collection<IPersonExtended> getByDatePeriodAndBirthOrder(AdvancableDate date, CompoundTimeUnit period, Integer birthOrder) {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if(divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionError();
        }

        ArrayList<IPersonExtended> people = new ArrayList<>();
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

    public Collection<IPersonExtended> getByDatePeriodAndBirthOrder(AdvancableDate date, CompoundTimeUnit period, IntegerRange birthOrder) {

        Integer highestBirthOrder = getHighestBirthOrder(date, period);

        if(!birthOrder.isPlus()) {
            highestBirthOrder = birthOrder.getMax();
        }

        Collection<IPersonExtended> people = new ArrayList<>();

        for(Integer i = birthOrder.getMin(); i <= highestBirthOrder; i++) {
            people.addAll(getByDatePeriodAndBirthOrder(date, period, i));
        }

        return people;
    }

    // TODO remove this method? Snarf the javadoc first!
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
     * @param dateOfBirth    the year of birth of the female
     * @param birthOrder     the birth order (i.e. number of children)
     * @param currentDate    the current date
     * @return the collection the set of people who have been removed
     * @throws InsufficientNumberOfPeopleException the insufficient number of people exception
     */
    @SuppressWarnings("Duplicates")
    public Collection<IPersonExtended> removeNPersons(int numberToRemove, AdvancableDate dateOfBirth, CompoundTimeUnit period,
                                                      int birthOrder, MonthDate currentDate)
                                                            throws InsufficientNumberOfPeopleException {


        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if(divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError("");
        }

        ArrayList<IPersonExtended> people = new ArrayList<>();
        MonthDate divisionDate = dateOfBirth.getMonthDate();

        LinkedList<MonthDate> reusableDivisions = new LinkedList<>();

        // find all the division dates
        for(int i = 0; i < divisionsInPeriod; i++) {
            reusableDivisions.add(divisionDate);
            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        // this by design rounds down
        int numberToRemoveFromDivision = (numberToRemove - people.size()) / reusableDivisions.size();

        // check variables to decide when to recalculate number to remove from each division at the current iteration
        int numberOfReusableDivisions = reusableDivisions.size();
        int divisionsUsed = 0;

        while(people.size() < numberToRemove) {

            if(reusableDivisions.isEmpty()) {
                throw new InsufficientNumberOfPeopleException("Not enought people in timeperiod to meet request of" +
                        numberToRemove + " females from " + dateOfBirth.toString() + " and following time period " +
                        period.toString());
            }

            // If every division has been sampled at the current level then and we are still short of people then we
            // need to recalculate the number to take from each division
            if(divisionsUsed == numberOfReusableDivisions) {
                // Reset check variables
                numberOfReusableDivisions = reusableDivisions.size();
                divisionsUsed = 0;

                double tempNumberToRemoveFromDivisions = (numberToRemove - people.size()) / (double) reusableDivisions.size();
                if(tempNumberToRemoveFromDivisions < 1) {
                    // in the case where we are down to the last couple of people (defined by the current number of
                    // reusable divisions minus 1) we proceed to remove 1 person from each interval in turn until we
                    // reach the required number of people to be removed.
                    numberToRemoveFromDivision = (int) Math.ceil(tempNumberToRemoveFromDivisions);
                } else {
                    numberToRemoveFromDivision = (int) tempNumberToRemoveFromDivisions;
                }
            }

            // dequeue division
            MonthDate consideredDivision = reusableDivisions.removeFirst();
            divisionsUsed ++;

            Collection<IPersonExtended> selectedPeople = removeNPeopleFromDivisionStartingOnDate(numberToRemoveFromDivision, consideredDivision, birthOrder, currentDate);
            people.addAll(selectedPeople);

            // if more people in division keep note incase of shortfall in other divisions
            if(selectedPeople.size() >= numberToRemoveFromDivision) {
                // enqueue division is still containing people
                reusableDivisions.addLast(consideredDivision);
            }

        }

        return people;

    }

    private Collection<IPersonExtended> removeNPeopleFromDivisionStartingOnDate(int numberToRemove, MonthDate divisionDate, int birthOrder, MonthDate currentDate) {

        // The selected people
        Collection<IPersonExtended> selectedPeople = new ArrayList<>();

        // Those unable to give birth at this time
        Collection<IPersonExtended> unusablePeople = new ArrayList<>();

        if (numberToRemove == 0) {
            return selectedPeople;
        }

        LinkedList<IPersonExtended> orderedBirthCohort = new LinkedList<>(getAllPeopleFromDivision(divisionDate).get(birthOrder));

        while (selectedPeople.size() < numberToRemove) {

            if(orderedBirthCohort.size() <= 0) {
                return selectedPeople;
            }

            IPersonExtended p = orderedBirthCohort.removeFirst();

            try {
                removePerson(p);

                if (p.noRecentChildren(currentDate, new CompoundTimeUnit(-9, TimeUnit.MONTH))) {
                    selectedPeople.add(p);
                } else {
                    unusablePeople.add(p);
                }


            } catch (PersonNotFoundException e) {
                log.error("This really shouldn't be happening");
                unusablePeople.add(p);
            }

        }

        // Add people unable to give birth at this time to the population structure
        for(IPersonExtended p : unusablePeople) {
            addPerson(p);
        }

        return selectedPeople;
    }

    private Map<Integer, Collection<IPersonExtended>> getAllPeopleFromDivision(AdvancableDate divisionDate) {

        try {
            return byBirthYearAndNumberOfChildren.get(divisionDate.getMonthDate());
        } catch (NullPointerException e) {
            if(checkDateAlignmentToDivisions(divisionDate)) {
                // If division date is resonable but no people exist in it yet
                return new HashMap<>();
            } else {
                throw new MisalignedTimeDivisionError("Date provided to underlying population structure does not align");
            }
        }
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPersonExtended> getAll() {

        Collection<IPersonExtended> people = new ArrayList<>();

        // By birth Year
        for (Map.Entry<MonthDate, TreeMap<Integer, Collection<IPersonExtended>>> t : byBirthYearAndNumberOfChildren.entrySet()) {
            // By number of children
            for (Map.Entry<Integer, Collection<IPersonExtended>> i : t.getValue().entrySet()) {
                people.addAll(i.getValue());
            }
        }

        return people;
    }

    @Override
    public Collection<IPersonExtended> getAllPersonsBornInTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod) {

        Collection<IPersonExtended> people = new ArrayList<>();

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), timePeriod);

        if(divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError("");
        }

        MonthDate divisionDate = firstDate.getMonthDate();

        // for all the division dates
        for(int i = 0; i < divisionsInPeriod; i++) {

            try {
                Map<Integer, Collection<IPersonExtended>> mapForDiv;
                // for each birth order
                for (Integer j : (mapForDiv = getAllPeopleFromDivision(firstDate)).keySet()) {
                    people.addAll(mapForDiv.get(j));
                }
            } catch (NullPointerException e) {
                // No need to do anything - we allow the method to return an empty list as no one was born in the year
            }

            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return people;
    }

    @Override
    public void addPerson(IPersonExtended person) {
        try {
            byBirthYearAndNumberOfChildren.get(
                    resolveDateToCorrectDivisionDate(person.getBirthDate_ex())).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            // If years or birth order doesn't exist in map then add missing part of map and add in person
            try {
                byBirthYearAndNumberOfChildren.get(
                        resolveDateToCorrectDivisionDate(person.getBirthDate_ex())).put(countChildren(person), new ArrayList<>());
                // If the year existed but the correct birth order did not
                byBirthYearAndNumberOfChildren.get(
                        resolveDateToCorrectDivisionDate(person.getBirthDate_ex())).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                // If the year didn't exist in the map
                TreeMap<Integer, Collection<IPersonExtended>> temp = new TreeMap<>();
                temp.put(countChildren(person), new ArrayList<>());
                temp.get(countChildren(person)).add(person);
                byBirthYearAndNumberOfChildren.put(resolveDateToCorrectDivisionDate(person.getBirthDate_ex()), temp);
            }
        }
    }

    @Override
    public void removePerson(IPersonExtended person) throws PersonNotFoundException {
        Collection<IPersonExtended> people = byBirthYearAndNumberOfChildren.get(
                resolveDateToCorrectDivisionDate(person.getBirthDate_ex())).get(countChildren(person));

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
    public int getNumberOfPersons(AdvancableDate firstDate, CompoundTimeUnit timePeriod) {

        int count = 0;

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), timePeriod);

        if(divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError("");
        }

        MonthDate divisionDate = firstDate.getMonthDate();

        // for all the division dates
        for(int i = 0; i < divisionsInPeriod; i++) {

            try {
                Map<Integer, Collection<IPersonExtended>> mapForDiv;
                // for each birth order
                for (Integer j : (mapForDiv = getAllPeopleFromDivision(firstDate)).keySet()) {
                    count += mapForDiv.get(j).size();
                }
            } catch (NullPointerException e) {
                // No need to do anything - we allow the method to return an empty list as no one was born in the year
            }

            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return count;
    }

    @Override
    public TreeSet<AdvancableDate> getDivisionDates() {
        return new TreeSet<>(byBirthYearAndNumberOfChildren.keySet());
    }


    /*
    -------------------- Private helper methods --------------------
     */

    private Integer countChildren(IPersonExtended person) {

        int count = 0;

        for (IPartnershipExtended partnership : person.getPartnerships_ex()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

}