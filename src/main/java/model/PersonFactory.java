package model;

import datastructure.population.PeopleCollection;
import utils.time.DateClock;

import java.util.Collections;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonFactory {

    private static Random randomNumberGenerator = new Random();

    public static IPartnership formNewChildInPartnership(Person mother, DateClock birthDate, PeopleCollection people) {

        Partnership partnership = new Partnership(null, mother, birthDate);

        Person child = new Person(getSex(), birthDate, partnership);
        partnership.addChildren(Collections.singletonList(child));
        people.addPerson(child);
        return partnership;
    }

    public static Person formOrphanChild(DateClock birthDate) {
        return new Person(getSex(), birthDate);
    }


    private static char getSex() {

        // TODO move over to a specified m to f ratio

        if (randomNumberGenerator.nextBoolean()) {
            return 'm';
        } else {
            return 'f';
        }

    }
}
