package simulationEntities;


import annotations.names.FirstNameGenerator;
import annotations.names.NameGenerator;
import annotations.names.SurnameGenerator;
import dateModel.Date;
import dateModel.dateImplementations.MonthDate;
import dateModel.dateSelection.BirthDateSelector;
import dateModel.dateSelection.DateSelector;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.partnership.IPartnership;
import simulationEntities.partnership.Partnership;
import simulationEntities.person.IPerson;
import simulationEntities.person.Person;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EntityFactory {

    private static Random randomNumberGenerator = new Random();
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();
    private static DateSelector birthDateSelector = new BirthDateSelector();

    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson father, IPerson mother, MonthDate currentDate,
                                                            CompoundTimeUnit birthTimeStep, PeopleCollection population, PopulationCounts pc) {

        IPartnership partnership = new Partnership(father, mother, currentDate);
        population.addPartnershipToIndex(partnership);

        IPerson child = makePerson(currentDate, birthTimeStep, partnership, population, pc);

        partnership.addChildren(Collections.singletonList(child));

        return partnership;
    }

    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson mother, MonthDate currentDate,
                                                         CompoundTimeUnit birthTimeStep, PeopleCollection population, PopulationCounts pc) {

        IPartnership partnership = new Partnership(mother, currentDate);


        List<IPerson> children = new ArrayList<>(numberOfChildren);

        for(int c = 0; c < numberOfChildren; c++) {
            children.add(makePerson(currentDate, birthTimeStep, partnership, population, pc));
        }

        partnership.addChildren(children);

        population.addPartnershipToIndex(partnership);

        return partnership;
    }

    public static IPerson formOrphanChild(MonthDate currentDate, CompoundTimeUnit birthTimeStep, PeopleCollection population, PopulationCounts pc) {
        return makePerson(currentDate, birthTimeStep, null, population, pc);
    }

    private static char getSex(PopulationCounts pc) {

        // TODO move over to a specified m to f ratio

        double sexBalance = pc.getLivingSexRatio();

        if(sexBalance <= 1) {

            pc.newMale();
            return 'M';
        } else {

            pc.newFemale();
            return 'F';
        }

//        if (randomNumberGenerator.nextBoolean()) {
//            return 'M';
//        } else {
//            return 'F';
//        }

    }

    public static IPerson makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnership parentsPartnership, PeopleCollection population, PopulationCounts pc) {

        Person person = new Person(getSex(pc), birthDateSelector.selectDate(currentDate, birthTimeStep), parentsPartnership);

        // OZGUR - this is where you're stuff is currently being called from
        person.setFirstName(firstNameGenerator.getName(person));
        person.setSurname(surnameGenerator.getName(person));


        population.addPerson(person);

        return person;
    }
}
