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
import uk.ac.standrews.cs.valipop.simulationEntities.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents a population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Population {

    // TODO rationalise OBDModel, Population, PersonCollection, PeopleCollection, IPersonCollection, Collection<IPerson>

    private final PeopleCollection livingPeople;
    private final PeopleCollection deadPeople;
    private final PeopleCollection emigrants;

    private final PopulationCounts populationCounts;
    private final Config config;

    public Population(final Config config) {

        this.config = config;

        // TODO avoid statics.
        Person.resetIds();
        Partnership.resetIds();

        livingPeople = new PeopleCollection(config, config.getInitialisationStart(), config.getSimulationEnd(), config.getSimulationTimeStep(), "living");
        deadPeople = new PeopleCollection(config, config.getInitialisationStart(), config.getSimulationEnd(), config.getSimulationTimeStep(), "dead");
        emigrants = new PeopleCollection(config, config.getInitialisationStart(), config.getSimulationEnd(), config.getSimulationTimeStep(), "emigrants");

        populationCounts = new PopulationCounts();
    }

    public PeopleCollection getPeople() {

        return combine(combine(livingPeople, deadPeople), emigrants);
    }

    public PeopleCollection getPeople(final LocalDate first, final LocalDate last, final Period maxAge) {

        final PeopleCollection result = new PeopleCollection(config, first, last, Period.ofYears(1), "combined");

        final Period period = Period.between(first, last);

        for (final IPerson person : livingPeople.getPeopleAliveInTimePeriod(first, period, maxAge))
            result.add(person);

        for (final IPerson person : deadPeople.getPeopleAliveInTimePeriod(first, period, maxAge))
            result.add(person);

        return result;
    }

    public PeopleCollection getLivingPeople() {
        return livingPeople;
    }

    public PeopleCollection getDeadPeople() {
        return deadPeople;
    }

    public PeopleCollection getEmigrants() {
        return emigrants;
    }

    public PopulationCounts getPopulationCounts() {
        return populationCounts;
    }

    private static PeopleCollection combine(final PeopleCollection collection1, final PeopleCollection collection2) {

        final LocalDate earlierStart = earlierDate(collection1.getStartDate(), collection2.getStartDate());
        final LocalDate laterEnd = laterDate(collection1.getEndDate(), collection2.getEndDate());

        final PeopleCollection cloned1 = collection1.clone();
        final PeopleCollection cloned2 = collection2.clone();

        cloned1.setStartDate(earlierStart);
        cloned1.setEndDate(laterEnd);

        for (final IPerson person : cloned2)
            cloned1.add(person);

        for (final IPartnership partnership : cloned2.getPartnerships())
            cloned1.add(partnership);

        return cloned1;
    }

    private static LocalDate earlierDate(final LocalDate date1, final LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    private static LocalDate laterDate(final LocalDate date1, final LocalDate date2) {
        return date1.isBefore(date2) ? date2 : date1;
    }
}
