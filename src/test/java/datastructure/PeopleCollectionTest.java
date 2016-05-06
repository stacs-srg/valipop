package datastructure;

import model.Person;
import model.interfaces.populationModel.IPerson;
import model.time.DateClock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollectionTest {

    PeopleCollection living;
    PeopleCollection dead;

    IPerson m1;
    IPerson m2;
    IPerson m3;

    IPerson f1;
    IPerson f2;
    IPerson f3;

    DateClock start;

    @Before
    public void setup() {
        living = new PeopleCollection();
        dead = new PeopleCollection();

        start = new DateClock(1, 1600);

        m1 = new Person('m', start, null);
        m2 = new Person('m', start, null);
        m3 = new Person('m', start, null);

        f1 = new Person('f', start, null);
        f2 = new Person('f', start, null);
        f3 = new Person('f', start, null);

        living.addPerson(m1);
        living.addPerson(m2);
        living.addPerson(m3);
        living.addPerson(f1);
        living.addPerson(f2);
        living.addPerson(f3);

    }

    @Test
    public void t() {

    }
    @Test
    public void peopleInCorrectPlace() {
        // are people added present in the correct place
        // for males
        Collection<IPerson> males = living.getMales().getByYear(start);
        assertTrue(males.contains(m1));
        assertTrue(males.contains(m2));

        // TODO keep writing these tests
        assertTrue(males.contains(m3));

        // for females
    }

    // can we move females (with births)
        // are they in the new place
        // and not in the old place


    // can we kill someone
        // are they in the new place
        // and not in the old place


    // access with various date methods
        // year date
        // date instant
        // date clock

    // do the get all methods get us everyone
        // for males
        // for females

    // do the get by year methods get us everyone
        // for males
        // for females



}
