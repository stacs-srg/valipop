package model;

import datastructure.population.PeopleCollection;
import model.nameGeneration.FirstNameGenerator;
import model.nameGeneration.NameGenerator;
import model.nameGeneration.SurnameGenerator;
import utils.time.Date;
import utils.time.DateClock;

import java.util.Collections;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonFactory {

    private static Random randomNumberGenerator = new Random();
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();

    public static IPartnership formNewChildInPartnership(IPerson father, IPerson mother, DateClock birthDate, PeopleCollection population) {

        IPartnership partnership = new Partnership(father, mother, birthDate);
        population.addPartnershipToIndex(partnership);

        IPerson child = makePerson(birthDate, partnership, population);

        partnership.addChildren(Collections.singletonList(child));

        return partnership;
    }

    public static IPerson formOrphanChild(DateClock birthDate, PeopleCollection population) {
        return makePerson(birthDate, null, population);
    }

    private static char getSex() {

        // TODO move over to a specified m to f ratio

        if (randomNumberGenerator.nextBoolean()) {
            return 'M';
        } else {
            return 'F';
        }

    }

    public static IPerson makePerson(Date birthDate, IPartnership parentsPartnership, PeopleCollection population) {

        Person person = new Person(getSex(), birthDate, parentsPartnership);

        // OZGUR - this is where you're stuff is currently being called from
        person.setFirstName(firstNameGenerator.getName(person));
        person.setSurname(surnameGenerator.getName(person));

        population.addPerson(person);

        return person;
    }
}
