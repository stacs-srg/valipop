/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.simulationEntities.dataStructure;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * The class MaleCollection is a concrete instance of the PersonCollection class.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    private final TreeMap<LocalDate, TreeSet<IPerson>> byYear = new TreeMap<>();
    private int size;

    /**
     * Instantiates a new MaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the MaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the MaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    public MaleCollection(final Config config, final LocalDate start, final LocalDate end, final Period divisionSize, final String description) {

        super(config, start, end, divisionSize, description);

        for (LocalDate date = start; !date.isAfter(end); date = date.plus(divisionSize))
            byYear.put(date, new TreeSet<>());
    }

    @Override
    public Collection<IPerson> getPeople() {

        final Collection<IPerson> people = new ArrayList<>();

        for (final Collection<IPerson> peopleForYear : byYear.values())
            people.addAll(peopleForYear);

        return people;
    }

    @Override
    void addPeople(final Collection<IPerson> people, final LocalDate divisionDate) {

        final Collection<IPerson> collection = byYear.get(divisionDate);

        if (collection != null)
            people.addAll(collection);
    }

    @Override
    public void add(final IPerson person) {

        final LocalDate divisionDate = getStartOfDivisionContaining(person.getBirthDate());

        byYear.putIfAbsent(divisionDate, new TreeSet<>());

        final TreeSet<IPerson> peopleForYear = byYear.get(divisionDate);
        if (peopleForYear.contains(person))
            throw new RuntimeException("Collection already contains person: " + person);

        peopleForYear.add(person);

        size++;
    }

    @Override
    public void remove(final IPerson person) {

        final TreeSet<IPerson> peopleForYear = byYear.get(getStartOfDivisionContaining(person.getBirthDate()));

        if (peopleForYear == null || !peopleForYear.remove(person))
            throw new PersonNotFoundException("Specified person not found in data structure");

        size--;
    }

    @Override
    public int getNumberOfPeople() {
        return size;
    }

    @Override
    public int getNumberOfPeople(final LocalDate firstDate, final Period timePeriod) {

        return getPeopleBornInTimePeriod(firstDate, timePeriod).size();
    }

    @Override
    public Set<LocalDate> getDivisionDates() {
        return byYear.keySet();
    }
}
