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
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollectionTest {

    Logger log = LogManager.getLogger(PeopleCollectionTest.class);

    @Before
    public void setUpPopulationStatistics() throws InconsistentWeightException, IOException, InvalidInputFileException {
        // TODO Make a config instance
        Config config = null;

        // use config to make make ps
        DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);
    }

    @Test
    public void peopleInByYearAndBirthsCorrectPlace() {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);

        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person('m', start, null);
        Person m2 = new Person('m', start, null);
        Person m3 = new Person('m', start, null);

        Person f1 = new Person('f', start, null);
        Person f2 = new Person('f', start, null);
        Person f3 = new Person('f', start, null);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

        // are people added present in the correct place
        // for females
        Collection<IPersonExtended> females = living.getFemales().getByDatePeriodAndBirthOrder(start, y, 0);
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

        Person m1 = new Person('m', start, null);
        Person m2 = new Person('m', start, null);
        Person m3 = new Person('m', start, null);

        Person f1 = new Person('f', start, null);
        Person f2 = new Person('f', start, null);
        Person f3 = new Person('f', start, null);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);


        // are people added present in the correct place
        // for males
        Collection<IPersonExtended> males = living.getMales().getAllPersonsBornInTimePeriod(start, y);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females

        Collection<IPersonExtended> females = living.getFemales().getAllPersonsBornInTimePeriod(start, y);
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

        Person m1 = new Person('m', start, null);
        Person m2 = new Person('m', start, null);
        Person m3 = new Person('m', start, null);

        Person f1 = new Person('f', start, null);
        Person f2 = new Person('f', start, null);
        Person f3 = new Person('f', start, null);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

        // are people added present in the correct place
        // for males
        Collection<IPersonExtended> males = living.getMales().getAll();
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females
        Collection<IPersonExtended> females = living.getFemales().getAll();
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));

        Collection<IPersonExtended> all = living.getAll();
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

        Person f1 = new Person('f', start, null);

        Person m1 = new Person('m', start, null);

        Person c1 = new Person('m', start.advanceTime(19, TimeUnit.YEAR), null);
        Person c2 = new Person('f', start.advanceTime(25, TimeUnit.YEAR), null);
        Person c3 = new Person('m', start.advanceTime(32, TimeUnit.YEAR), null);

        living.addPerson(f1);

        // can we move females (with births) - single birth

        living.removePerson(f1);

        Partnership p1 = new Partnership(m1, f1, c1.getBirthDate_ex());
        p1.addChildren(Collections.singletonList(c1));
        m1.recordPartnership(p1);
        f1.recordPartnership(p1);
        living.addPerson(c1);

        living.addPerson(f1);

        // are they in the new place
        Collection<IPersonExtended> people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate_ex().getYearDate(), y, 1);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate_ex().getYearDate(), y, 0);
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
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate_ex().getYearDate(), y, 3);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByDatePeriodAndBirthOrder(m1.getBirthDate_ex().getYearDate(), y, 1);
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

        Person f1 = new Person('f', start, null);
        living.removePerson(f1);

    }


    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentFemale() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person f1 = new Person('f', start, null);
        Person f2 = new Person('f', start, null);

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

        Person m1 = new Person('m', start, null);
        living.removePerson(m1);

    }


    @Test(expected = PersonNotFoundException.class)
    public void removeNonExistentMale() throws PersonNotFoundException {

        MonthDate s = new MonthDate(1, 0);
        MonthDate e = new MonthDate(1, 3000);

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        PeopleCollection living = new PeopleCollection(s, e, y);

        MonthDate start = new MonthDate(1, 1600);

        Person m1 = new Person('m', start, null);
        Person m2 = new Person('m', start, null);

        living.addPerson(m2);

        living.removePerson(m1);


    }


}
