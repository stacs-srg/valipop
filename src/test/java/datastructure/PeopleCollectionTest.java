package datastructure;

import model.Partnership;
import model.Person;
import model.interfaces.populationModel.IPerson;
import model.time.DateClock;
import model.time.DateInstant;
import model.time.TimeUnit;
import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollectionTest {

    Logger log = LogManager.getLogger(PeopleCollectionTest.class);

    @Test
    public void peopleInByYearAndBirthsCorrectPlace() {

        PeopleCollection living = new PeopleCollection();
        PeopleCollection dead = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);

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
        Collection<IPerson> females = living.getFemales().getByNumberOfChildren(start, 0);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));

    }

    @Test
    public void peopleInByYearCorrectPlace() {

        PeopleCollection living = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);

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
        Collection<IPerson> males = living.getMales().getByYear(start);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));
        assertTrue(males.contains(m3));

        // for females

        Collection<IPerson> females = living.getFemales().getByYear(start);
        assertTrue(females.contains(f1));
        assertTrue(females.contains(f2));
        assertTrue(females.contains(f3));


    }

    @Test
    public void peopleInByGetAllCorrectPlace() {

        PeopleCollection living = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);

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
    public void femaleGivesBirthMoveOfBirthCountPosition() {

        PeopleCollection living = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);

        Person f1 = new Person('f', start, null);

        Person m1 = new Person('m', start, null);

        Person c1 = new Person('m', start.advanceTime(19, TimeUnit.YEAR), null);
        Person c2 = new Person('f', start.advanceTime(25, TimeUnit.YEAR), null);
        Person c3 = new Person('m', start.advanceTime(32, TimeUnit.YEAR), null);

        living.addPerson(f1);

        // can we move females (with births) - single birth

        Partnership p1 = new Partnership(m1, f1);
        p1.addChildren(Collections.singletonList(c1));
        m1.recordPartnership(p1);
        f1.recordPartnership(p1);
        living.addPerson(c1);

        living.updatePerson(f1, 1);

        // are they in the new place
        Collection<IPerson> people = living.getFemales().getByNumberOfChildren(m1.getBirthDate().getYearDate(), 1);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByNumberOfChildren(m1.getBirthDate().getYearDate(), 0);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getAll();
        assertTrue(people.contains(c1));


        // can we move females (with births) - twin births
        p1.addChildren(Arrays.asList(c2, c3));

        living.addPerson(c2);
        living.addPerson(c3);

        living.updatePerson(f1, 2);

        // are they in the new place
        people = living.getFemales().getByNumberOfChildren(m1.getBirthDate().getYearDate(), 3);
        assertTrue(people.contains(f1));

        // and not in the old place
        people = living.getFemales().getByNumberOfChildren(m1.getBirthDate().getYearDate(), 1);
        assertFalse(people.contains(f1));

        // check for children
        people = living.getAll();
        assertTrue(people.contains(c2));
        assertTrue(people.contains(c3));

    }

    @Test
    public void personIsCorrectlyRelocatedAfterDeath() {

        PeopleCollection living = new PeopleCollection();
        PeopleCollection dead = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);

        Person m1 = new Person('m', start, null);

        living.addPerson(m1);

        // can we kill someone
        m1.recordDeath(start.advanceTime(90, TimeUnit.YEAR));
        living.removePerson(m1);
        dead.addPerson(m1);

        // and not in the old place
        assertFalse(living.getAll().contains(m1));

        // are they in the new place
        assertTrue(dead.getAll().contains(m1));

    }

    @Test
    public void accessWithVariousDateTypes() {

        PeopleCollection living = new PeopleCollection();

        DateClock start = new DateClock(1, 1600);
        DateInstant startI = new DateInstant(7, 1, 1600);
        YearDate startY = new YearDate(1600);

        Person f1 = new Person('f', start, null);

        Person m1 = new Person('m', startI, null);
        Person m2 = new Person('m', startY, null);

        living.addPerson(f1);

        living.addPerson(m1);
        living.addPerson(m2);

        Collection<IPerson> males = living.getMales().getByYear(startY);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));

        // for females

        Collection<IPerson> females = living.getFemales().getByYear(startI);
        assertTrue(females.contains(f1));

    }

}
