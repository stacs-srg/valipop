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

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.utils.MapUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.MisalignedTimeDivisionException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;


/**
 * The FemaleCollection is a specialised concrete implementation of a PersonCollection. The implementation offers an
 * additional layer of division below the year of birth level which divides females out into separate collections based
 * on how many children they have had.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection extends PersonCollection {

    private final Map<LocalDate, Map<Integer, Collection<IPerson>>> byBirthYearAndNumberOfChildren = new TreeMap<>();

    /**
     * Instantiates a new FemaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the FemaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the FemaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    FemaleCollection(LocalDate start, LocalDate end, Period divisionSize) {
        super(start, end, divisionSize);

        for (LocalDate d = start; !d.isAfter( end); d = d.plus(divisionSize)) {
            byBirthYearAndNumberOfChildren.put(d, new TreeMap<>());
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
    private int getHighestBirthOrder(LocalDate dateOfBirth, Period period) {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if (divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionException();
        }

        LocalDate divisionDate = dateOfBirth;

        int highestBirthOrder = 0;

        for (int i = 0; i < divisionsInPeriod; i++) {

            Map<Integer, Collection<IPerson>> temp = byBirthYearAndNumberOfChildren.get(divisionDate);

            if (temp != null && MapUtils.getMax(temp.keySet()) > highestBirthOrder) {
                highestBirthOrder = MapUtils.getMax(temp.keySet());
            }

            // move on to the new division date until we've covered the required divisions
            divisionDate = divisionDate.plus(getDivisionSize());
        }

        return highestBirthOrder;
    }

    /**
     * Gets the {@link Collection} of mothers born in the given year with the specified birth order (i.e. number of
     * children)
     *
     * @param date       the date
     * @param period     the period following the date to find people from
     * @param birthOrder the number of children
     * @return the by number of children
     */
    Collection<IPerson> getByDatePeriodAndBirthOrder(LocalDate date, Period period, Integer birthOrder) {

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), period);

        if (divisionsInPeriod == -1) {
            throw new MisalignedTimeDivisionException();
        }

        ArrayList<IPerson> people = new ArrayList<>();
        LocalDate divisionDate = date;

        for (int i = 0; i < divisionsInPeriod; i++) {

            try {
                people.addAll(byBirthYearAndNumberOfChildren.get(divisionDate).get(birthOrder));
            } catch (NullPointerException e) {
                // If no data exists for the year or the given birth order in the given year, then there's no one to add
            }

            // move on to the new division date until we've covered the required divisions
            divisionDate = divisionDate.plus(getDivisionSize());
        }

        return people;
    }

    public Collection<IPerson> getByDatePeriodAndBirthOrder(LocalDate date, Period period, IntegerRange birthOrder) {

        int highestBirthOrder = getHighestBirthOrder(date, period);

        if (!birthOrder.isPlus()) {
            highestBirthOrder = birthOrder.getMax();
        }

        Collection<IPerson> people = new ArrayList<>();

        for (int i = birthOrder.getMin(); i <= highestBirthOrder; i++) {
            people.addAll(getByDatePeriodAndBirthOrder(date, period, i));
        }

        return people;
    }


    private Map<Integer, Collection<IPerson>> getAllPeopleFromDivision(LocalDate divisionDate) {

        LocalDate date = divisionDate;

        if (byBirthYearAndNumberOfChildren.containsKey(date)) {
            return byBirthYearAndNumberOfChildren.get(date);

        } else {
            if (checkDateAlignmentToDivisions(divisionDate)) {
                // Division date is reasonable but no people exist in it yet
                return new HashMap<>();
            } else {
                throw new MisalignedTimeDivisionException("Date provided to underlying population structure does not align");
            }
        }
    }

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<>();

        for (Map<Integer, Collection<IPerson>> map : byBirthYearAndNumberOfChildren.values()) {
            for (Collection<IPerson> collection : map.values()) {
                people.addAll(collection);
            }
        }

        return people;
    }

    @Override
    void addPeople(Collection<IPerson> people, LocalDate divisionDate) {

        for (Collection<IPerson> collection : getAllPeopleFromDivision(divisionDate).values()) {
            people.addAll(collection);
        }
    }

    @Override
    public void addPerson(IPerson person) {

        final LocalDate divisionDate = resolveDateToCorrectDivisionDate(person.getBirthDate());
        final int numberOfChildren = countChildren(person);

        final List<IPerson> newList = new ArrayList<>();
        newList.add(person);

        if (byBirthYearAndNumberOfChildren.containsKey(divisionDate)) {

            final Map<Integer, Collection<IPerson>> map = byBirthYearAndNumberOfChildren.get(divisionDate);

            if (map.containsKey(numberOfChildren)) {
                map.get(numberOfChildren).add(person);

            } else {
                map.put(numberOfChildren, newList);
            }
        } else {

            Map<Integer, Collection<IPerson>> newMap = new TreeMap<>();
            newMap.put(numberOfChildren, newList);
            byBirthYearAndNumberOfChildren.put(divisionDate, newMap);
        }
    }

    @Override
    public void removePerson(IPerson person) {

        Collection<IPerson> people = byBirthYearAndNumberOfChildren.get(resolveDateToCorrectDivisionDate(person.getBirthDate())).get(countChildren(person));

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
    public int getNumberOfPersons(LocalDate firstDate, Period timePeriod) {

        return getAllPersonsBornInTimePeriod(firstDate, timePeriod).size();
    }

    @Override
    public Set<LocalDate> getDivisionDates() {
        return new TreeSet<>(byBirthYearAndNumberOfChildren.keySet());
    }

    /*
    -------------------- Private helper methods --------------------
     */

    private int countChildren(IPerson person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;
    }
}