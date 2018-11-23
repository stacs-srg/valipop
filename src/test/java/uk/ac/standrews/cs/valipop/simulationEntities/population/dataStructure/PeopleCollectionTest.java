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


import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollectionTest {

    PopulationStatistics ps;

    @Before
    public void setUpPopulationStatistics() {
        Config config = new Config(new MonthDate(1,1), new MonthDate(1,100),
                new MonthDate(1,200), 0, 0, 0, null,
                "src/test/resources/valipop/test-pop", "", "",
                0, 0, true, 0, 0, 0,
                0, new CompoundTimeUnit(1, TimeUnit.YEAR), RecordFormat.NONE, null, 0, true);
        // use config to make make ps
        ps = new PopulationStatistics(config);
    }

    @Test
    public void peopleInCorrectYearCornerCases() {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);

        PeopleCollection living = new PeopleCollection(s, e, y);

        ExactDate b1 = new ExactDate(1,1,1900);
        ExactDate b2 = new ExactDate(2,1,1900);
        ExactDate b3 = new ExactDate(31,12,1900);
        ExactDate b4 = new ExactDate(1,1,1901);

        Person m1 = new Person(SexOption.MALE, b1, null, ps, false);
        Person m2 = new Person(SexOption.MALE, b2, null, ps, false);
        Person m3 = new Person(SexOption.MALE, b3, null, ps, false);
        Person m4 = new Person(SexOption.MALE, b4, null, ps, false);

        Person f1 = new Person(SexOption.FEMALE, b1, null, ps, false);
        Person f2 = new Person(SexOption.FEMALE, b2, null, ps, false);
        Person f3 = new Person(SexOption.FEMALE, b3, null, ps, false);
        Person f4 = new Person(SexOption.FEMALE, b4, null, ps, false);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(m4);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);
        living.addPerson(f4);

        // are people added present in the correct place
        // for females
        Collection<IPerson> females = living.getFemales().getByDatePeriodAndBirthOrder(new YearDate(1900), y, 0);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
        assertFalse(females.contains(f4));

        Collection<IPerson> males = living.getMales().getAllPersonsBornInTimePeriod(new YearDate(1900), y);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));
        assertFalse(males.contains(m4));
    }

    @Test
    public void peopleInByYearAndBirthsCorrectPlace() {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);

        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

        // are people added present in the correct place
        // for females
        Collection<IPerson> females = living.getFemales().getByDatePeriodAndBirthOrder(start, y, 0);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
    }

    @Test
    public void peopleInByYearCorrectPlace()  {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

        // are people added present in the correct place
        // for males
        Collection<IPerson> males = living.getMales().getAllPersonsBornInTimePeriod(start, y);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females

        Collection<IPerson> females = living.getFemales().getAllPersonsBornInTimePeriod(start, y);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));
    }

    @Test
    public void peopleInByGetAllCorrectPlace()  {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        Person m2 = new Person(SexOption.MALE, start, null, ps, false);
        Person m3 = new Person(SexOption.MALE, start, null, ps, false);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f3 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

        // are people added present in the correct place
        // for males
        Collection<IPerson> males = living.getMales().getAll();
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females
        Collection<IPerson> females = living.getFemales().getAll();
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));

        Collection<IPerson> all = living.getAll();
        assertTrue(all.contains(m1));
        assertTrue(all.contains(m2));
        assertTrue(all.contains(m3));
        assertTrue(all.contains(f1));
        assertTrue(all.contains(f2));
        assertTrue(all.contains(f3));
    }

    @Test
    public void femaleGivesBirthMoveOfBirthCountPosition() throws PersonNotFoundException  {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);

        Person c1 = new Person(SexOption.MALE, start.advanceTime(19, TimeUnit.YEAR), null, ps, false);
        Person c2 = new Person(SexOption.FEMALE, start.advanceTime(25, TimeUnit.YEAR), null, ps, false);
        Person c3 = new Person(SexOption.MALE, start.advanceTime(32, TimeUnit.YEAR), null, ps, false);

        living.addPerson(f1);

        // can we move females (with births) - single birth

        living.removePerson(f1);

        Partnership p1 = new Partnership(m1, f1, c1.getBirthDate());
        p1.addChildren(Collections.singletonList(c1));
        m1.recordPartnership(p1);
        f1.recordPartnership(p1);
        living.addPerson(c1);

        living.addPerson(f1);

        // are they in the new place
        Collection<IPerson> people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate().getYearDate(), y, 1);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate().getYearDate(), y, 0);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getAll();
        assertTrue(people.contains(c1));


        living.removePerson(f1);

        // can we move females (with births) - twin births
        p1.addChildren(Arrays.asList(c2, c3));

        living.addPerson(c2);
        living.addPerson(c3);

        living.addPerson(f1);

        // are they in the new place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate().getYearDate(), y, 3);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate().getYearDate(), y, 1);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getAll();
        assertTrue(people.contains(c2));
        assertTrue(people.contains(c3));
    }

    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentFemaleFromEmptyCollection() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        living.removePerson(f1);
    }

    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentFemale() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person f1 = new Person(SexOption.FEMALE, start, null, ps, false);
        Person f2 = new Person(SexOption.FEMALE, start, null, ps, false);

        living.addPerson(f2);
        living.removePerson(f1);
    }

    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentMaleFromEmptyCollection() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        living.removePerson(m1);
    }

    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentMale() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person(SexOption.MALE, start, null, ps, false);
        Person m2 = new Person(SexOption.MALE, start, null, ps, false);

        living.addPerson(m2);
        living.removePerson(m1);
    }
}
