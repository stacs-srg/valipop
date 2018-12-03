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
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * The class MaleCollection is a concrete instance of the PersonCollection class.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    // this is by year of birth
    private final Map<LocalDate, Collection<IPerson>> byYear = new TreeMap<>();

    /**
     * Instantiates a new MaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the MaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the MaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    public MaleCollection(LocalDate start, LocalDate end, Period divisionSize) {

        super(start, end, divisionSize);

        for (LocalDate d = start; !d.isAfter( end); d = d.plus(divisionSize)) {
            byYear.put(d, new ArrayList<>());
        }
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<>();

        for (Collection<IPerson> persons : byYear.values()) {
            people.addAll(persons);
        }

        return people;
    }

    @Override
    void addPeople(Collection<IPerson> people, LocalDate divisionDate) {

        Collection<IPerson> collection = byYear.get(divisionDate);
        if (collection != null) {
            people.addAll(collection);
        }
    }

    @Override
    public void addPerson(IPerson person) {

        LocalDate divisionDate = resolveDateToCorrectDivisionDate(person.getBirthDate());

        if (byYear.containsKey(divisionDate)) {
            byYear.get(divisionDate).add(person);

        } else {

            final List<IPerson> newList = new ArrayList<>();
            newList.add(person);
            byYear.put(divisionDate, newList);
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
    public int getNumberOfPersons(LocalDate firstDate, Period timePeriod) {

        return getAllPersonsBornInTimePeriod(firstDate, timePeriod).size();
    }

    @Override
    public Set<LocalDate> getDivisionDates() {
        return new TreeSet<>(byYear.keySet());
    }
}
