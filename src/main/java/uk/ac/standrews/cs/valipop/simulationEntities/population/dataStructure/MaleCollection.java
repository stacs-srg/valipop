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

import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.*;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.MisalignedTimeDivisionError;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.util.*;

/**
 * The class MaleCollection is a concrete instance of the PersonCollection class.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    // this is by year of birth
    private final Map<MonthDate, Collection<IPerson>> byYear = new TreeMap<>();

    /**
     * Instantiates a new MaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the MaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the MaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    public MaleCollection(AdvanceableDate start, ValipopDate end, CompoundTimeUnit divisionSize) {
        super(start, end, divisionSize);

        for (AdvanceableDate d = start; DateUtils.dateBeforeOrEqual(d, end); d = d.advanceTime(divisionSize)) {
            byYear.put(d.getMonthDate(), new ArrayList<>());
        }
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<>();

        for (Map.Entry<MonthDate, Collection<IPerson>> persons : byYear.entrySet()) {
            people.addAll(persons.getValue());
        }

        return people;
    }

    @Override
    public Collection<IPerson> getAllPersonsBornInTimePeriod(AdvanceableDate firstDate, CompoundTimeUnit timePeriod) {

        Collection<IPerson> people = new ArrayList<>();

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), timePeriod);

        if (divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError("");
        }

        MonthDate divisionDate = firstDate.getMonthDate();

        // for all the division dates
        for (int i = 0; i < divisionsInPeriod; i++) {

            try {
                people.addAll(byYear.get(divisionDate));
            } catch (NullPointerException e) {
                // No need to do anything - we allow the method to return an empty list as no one was born in the year
            }

            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return people;
    }

    @Override
    public Collection<IPerson> getAllPersonsAliveInTimePeriod(AdvanceableDate firstDate, CompoundTimeUnit timePeriod, CompoundTimeUnit maxAge) {

        CompoundTimeUnit tP = DateUtils.combineCompoundTimeUnits(timePeriod, maxAge);

        Collection<IPerson> peopleBorn = getAllPersonsBornInTimePeriod(firstDate.advanceTime(maxAge.negative()), tP);

        Collection<IPerson> peopleAlive = new ArrayList<>();

        for (IPerson p : peopleBorn) {
            if (p.diedAfter(firstDate)) {
                peopleAlive.add(p);
            }
        }

        return peopleAlive;
    }

    @Override
    public void addPerson(IPerson person) {
        MonthDate divisionDate = resolveDateToCorrectDivisionDate(person.getBirthDate());

        try {
            byYear.get(divisionDate).add(person);
        } catch (NullPointerException e) {
            // If the year didn't exist in the map
            byYear.put(divisionDate, new ArrayList<>());
            byYear.get(divisionDate).add(person);
        }
    }

    @Override
    public void removePerson(IPerson person) {

        Collection<IPerson> people = byYear.get(resolveDateToCorrectDivisionDate(person.getBirthDate()));

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
    public int getNumberOfPersons(AdvanceableDate firstDate, CompoundTimeUnit timePeriod) {

        int count = 0;

        int divisionsInPeriod = DateUtils.calcSubTimeUnitsInTimeUnit(getDivisionSize(), timePeriod);

        if (divisionsInPeriod <= 0) {
            throw new MisalignedTimeDivisionError("");
        }

        MonthDate divisionDate = firstDate.getMonthDate();

        // for all the division dates
        for (int i = 0; i < divisionsInPeriod; i++) {

            try {
                count += byYear.get(firstDate.getMonthDate()).size();
            } catch (NullPointerException e) {
                // No need to do anything - we allow the method to return an empty list as no one was born in the year
            }

            divisionDate = divisionDate.advanceTime(getDivisionSize());
        }

        return count;
    }

    @Override
    public Set<AdvanceableDate> getDivisionDates() {
        return new TreeSet<>(byYear.keySet());
    }
}
