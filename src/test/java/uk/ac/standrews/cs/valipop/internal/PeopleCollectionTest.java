/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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
package uk.ac.standrews.cs.valipop.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollectionTest {

    private PopulationStatistics ps;

    @BeforeEach
    public void setUpPopulationStatistics() {
        final Config config = new Config(
                LocalDate.of(1, 1, 1),
                LocalDate.of(200, 1, 1),
                LocalDate.of(300, 1, 1),
                0,
                Paths.get("src/test/resources/valipop/distributions"),
                Config.DEFAULT_RESULTS_SAVE_PATH, "PEOPLE_COLLECTION_TEST",
                Config.DEFAULT_RESULTS_SAVE_PATH).setDeterministic(true);

        ps = new PopulationStatistics(config);
    }

    @Test
    public void peopleInCorrectYearCornerCases() {

        final LocalDate s = LocalDate.of(0, 1, 1);
        final LocalDate e = LocalDate.of(3000, 1, 1);

        final Period y = Period.ofYears(1);

        final PeopleCollection living = new PeopleCollection(s, e, y, "");

        final LocalDate b1 = LocalDate.of(1900, 1, 1);
        final LocalDate b2 = LocalDate.of(1900, 1, 2);
        final LocalDate b3 = LocalDate.of(1900, 12, 31);
        final LocalDate b4 = LocalDate.of(1901, 1, 1);

        final Person m1 = new Person(SexOption.MALE, b1, null, ps, false);
        final Person m2 = new Person(SexOption.MALE, b2, null, ps, false);
        final Person m3 = new Person(SexOption.MALE, b3, null, ps, false);
        final Person m4 = new Person(SexOption.MALE, b4, null, ps, false);

        final Person f1 = new Person(SexOption.FEMALE, b1, null, ps, false);
        final Person f2 = new Person(SexOption.FEMALE, b2, null, ps, false);
        final Person f3 = new Person(SexOption.FEMALE, b3, null, ps, false);
        final Person f4 = new Person(SexOption.FEMALE, b4, null, ps, false);

        living.add(m1);
        living.add(m2);
        living.add(m3);
        living.add(m4);
        living.add(f1);
        living.add(f2);
        living.add(f3);
        living.add(f4);

        // are people added present in the correct place
        // for females
        final Collection<IPerson> females = living.getFemales().getByDatePeriodAndBirthOrder(LocalDate.of(1900, 1, 1), y, 0);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
        assertFalse(females.contains(f4));

        final Collection<IPerson> males = living.getMales().getPeopleBornInTimePeriod(LocalDate.of(1900, 1, 1), y);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));
        assertFalse(males.contains(m4));
    }

    @Test
    public void peopleInByYearAndBirthsCorrectPlace() {

        final LocalDate s = LocalDate.of(0, 1, 1);
        final LocalDate e = LocalDate.of(3000, 1, 1);

        final Period y = Period.ofYears(1);

        final PeopleCollection living = new PeopleCollection(s, e, y,"");

        final LocalDate start = LocalDate.of(1600, 1, 1);

        final Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.add(m1);
        living.add(m2);
        living.add(m3);
        living.add(f1);
        living.add(f2);
        living.add(f3);

        // are people added present in the correct place
        // for females
        final Collection<IPerson> females = living.getFemales().getByDatePeriodAndBirthOrder(start, y, 0);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
    }

    @Test
    public void peopleInByYearCorrectPlace() {

        final LocalDate s = LocalDate.of(0, 1, 1);
        final LocalDate e = LocalDate.of(3000, 1, 1);

        final Period y = Period.ofYears(1);
        final PeopleCollection living = new PeopleCollection(s, e, y,"");

        final LocalDate start = LocalDate.of(1600, 1, 1);

        final Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.add(m1);
        living.add(m2);
        living.add(m3);
        living.add(f1);
        living.add(f2);
        living.add(f3);

        // are people added present in the correct place
        // for males
        final Collection<IPerson> males = living.getMales().getPeopleBornInTimePeriod(start, y);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females

        final Collection<IPerson> females = living.getFemales().getPeopleBornInTimePeriod(start, y);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
    }

    @Test
    public void peopleInByGetAllCorrectPlace() {

        final LocalDate s = LocalDate.of(0, 1, 1);
        final LocalDate e = LocalDate.of(3000, 1, 1);

        final Period y = Period.ofYears(1);
        final PeopleCollection living = new PeopleCollection(s, e, y,"");

        final LocalDate start = LocalDate.of(1600, 1, 1);

        final Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        final Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        final Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.add(m1);
        living.add(m2);
        living.add(m3);
        living.add(f1);
        living.add(f2);
        living.add(f3);

        // are people added present in the correct place
        // for males
        final Collection<IPerson> males = living.getMales().getPeople();
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females
        final Collection<IPerson> females = living.getFemales().getPeople();
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));

        final Collection<IPerson> all = living.getPeople();
        assertTrue(all.contains(m1));
        assertTrue(all.contains(m2));
        assertTrue(all.contains(m3));
        assertTrue(all.contains(f1));
        assertTrue(all.contains(f2));
        assertTrue(all.contains(f3));
    }

    @Test
    public void femaleGivesBirthMoveOfBirthCountPosition() throws PersonNotFoundException {

        final LocalDate s = LocalDate.of(0, 1, 1);
        final LocalDate e = LocalDate.of(3000, 1, 1);

        final Period y = Period.ofYears(1);
        final PeopleCollection living = new PeopleCollection(s, e, y,"");

        final LocalDate start = LocalDate.of(1600, 1, 1);

        final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);

        final Person m1 = new Person(SexOption.MALE, start, null, ps, false);

        final Person c1 = new Person(SexOption.MALE, start.plus(19, ChronoUnit.YEARS), null, ps, false);
        final Person c2 = new Person(SexOption.FEMALE, start.plus(25, ChronoUnit.YEARS), null, ps, false);
        final Person c3 = new Person(SexOption.MALE, start.plus(32, ChronoUnit.YEARS), null, ps, false);

        living.add(f1);

        // can we move females (with births) - single birth

        living.remove(f1);

        final Partnership p1 = new Partnership(m1, f1);
        p1.setPartnershipDate(c1.getBirthDate());
        p1.addChildren(Collections.singletonList(c1));
        m1.recordPartnership(p1);
        f1.recordPartnership(p1);
        living.add(c1);

        living.add(f1);

        // are they in the new place
        Collection<IPerson> people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate(), y, 1);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate(), y, 0);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getPeople();
        assertTrue(people.contains(c1));

        living.remove(f1);

        // can we move females (with births) - twin births
        p1.addChildren(Arrays.asList(c2, c3));

        living.add(c2);
        living.add(c3);

        living.add(f1);

        // are they in the new place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate(), y, 3);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate(), y, 1);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getPeople();
        assertTrue(people.contains(c2));
        assertTrue(people.contains(c3));
    }

    @Test
    public void removeNonExistentFemaleFromEmptyCollection() throws PersonNotFoundException {

        assertThrows(PersonNotFoundException.class, () -> {
            final LocalDate s = LocalDate.of(0, 1, 1);
            final LocalDate e = LocalDate.of(3000, 1, 1);

            final Period y = Period.ofYears(1);
            final PeopleCollection living = new PeopleCollection(s, e, y,"");

            final LocalDate start = LocalDate.of(1600, 1, 1);

            final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
            living.remove(f1);
        });
    }

    @Test
    public void removeNonExistentFemale() throws PersonNotFoundException {

        assertThrows(PersonNotFoundException.class, () -> {
            final LocalDate s = LocalDate.of(0, 1, 1);
            final LocalDate e = LocalDate.of(3000, 1, 1);

            final Period y = Period.ofYears(1);
            final PeopleCollection living = new PeopleCollection(s, e, y,"");

            final LocalDate start = LocalDate.of(1600, 1, 1);

            final Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
            final Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);

            living.add(f2);
            living.remove(f1);
        });
    }

    @Test
    public void removeNonExistentMaleFromEmptyCollection() throws PersonNotFoundException {

        assertThrows(PersonNotFoundException.class, () -> {
            final LocalDate s = LocalDate.of(0, 1, 1);
            final LocalDate e = LocalDate.of(3000, 1, 1);

            final Period y = Period.ofYears(1);
            final PeopleCollection living = new PeopleCollection(s, e, y,"");

            final LocalDate start = LocalDate.of(1600, 1, 1);

            final Person m1 = new Person(SexOption.MALE, start, null, ps, false);
            living.remove(m1);
        });
    }

    @Test
    public void removeNonExistentMale() throws PersonNotFoundException {

        assertThrows(PersonNotFoundException.class, () -> {
            final LocalDate s = LocalDate.of(0, 1, 1);
            final LocalDate e = LocalDate.of(3000, 1, 1);

            final Period y = Period.ofYears(1);
            final PeopleCollection living = new PeopleCollection(s, e, y,"");

            final LocalDate start = LocalDate.of(1600, 1, 1);

            final Person m1 = new Person(SexOption.MALE, start, null, ps, false);
            final Person m2 = new Person(SexOption.MALE, start, null, ps, false);

            living.add(m2);
            living.remove(m1);
        });
    }
}
