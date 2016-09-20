package model.simulationEntities;

import datastructure.population.PeopleCollection;
import model.dateSelection.BirthDateSelector;
import model.dateSelection.DateSelector;
import model.nameGeneration.FirstNameGenerator;
import model.nameGeneration.NameGenerator;
import model.nameGeneration.SurnameGenerator;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateClock;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonFactory {

    private static Random randomNumberGenerator = new Random();
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();
    private static DateSelector birthDateSelector = new BirthDateSelector();

    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson father, IPerson mother, DateClock currentDate,
                                                         CompoundTimeUnit birthTimeStep, PeopleCollection population) {

        IPartnership partnership = new Partnership(father, mother, currentDate);
        population.addPartnershipToIndex(partnership);

        IPerson child = makePerson(currentDate, birthTimeStep, partnership, population);

        partnership.addChildren(Collections.singletonList(child));

        return partnership;
    }

    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson mother, DateClock currentDate,
                                                         CompoundTimeUnit birthTimeStep, PeopleCollection population) {

        IPartnership partnership = new Partnership(mother, currentDate);
        population.addPartnershipToIndex(partnership);

        List<IPerson> children = new ArrayList<>(numberOfChildren);

        for(int c = 0; c < numberOfChildren; c++) {
            children.add(makePerson(currentDate, birthTimeStep, partnership, population));
        }

        partnership.addChildren(children);

        return partnership;
    }

    public static IPerson formOrphanChild(DateClock currentDate, CompoundTimeUnit birthTimeStep, PeopleCollection population) {
        return makePerson(currentDate, birthTimeStep, null, population);
    }

    private static char getSex() {

        // TODO move over to a specified m to f ratio

        if (randomNumberGenerator.nextBoolean()) {
            return 'M';
        } else {
            return 'F';
        }

    }

    public static IPerson makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnership parentsPartnership, PeopleCollection population) {

        Person person = new Person(getSex(), birthDateSelector.selectDate(currentDate, birthTimeStep), parentsPartnership);

        // OZGUR - this is where you're stuff is currently being called from
        person.setFirstName(firstNameGenerator.getName(person));
        person.setSurname(surnameGenerator.getName(person));


        population.addPerson(person);

        return person;
    }
}
