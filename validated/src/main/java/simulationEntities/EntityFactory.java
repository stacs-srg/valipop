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
import simulationEntities.population.dataStructure.Population;

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
                                                            CompoundTimeUnit birthTimeStep, Population population) {

        IPartnership partnership = new Partnership(father, mother, currentDate);
        population.getLivingPeople().addPartnershipToIndex(partnership);

        IPerson child = makePerson(currentDate, birthTimeStep, partnership, population);

        partnership.addChildren(Collections.singletonList(child));

        return partnership;
    }

    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson mother, MonthDate currentDate,
                                                            CompoundTimeUnit birthTimeStep, Population population) {

        IPartnership partnership = new Partnership(mother, currentDate);

        List<IPerson> children = new ArrayList<>(numberOfChildren);

        // This ensures twins are born on the same day
        Date childrenBirthDate = null;

        for(int c = 0; c < numberOfChildren; c++) {
            IPerson child;
            if(childrenBirthDate == null) {
                child = makePerson(currentDate, birthTimeStep, partnership, population);
                childrenBirthDate = child.getBirthDate();
            } else {
                child = makePerson(childrenBirthDate, partnership, population);
            }
            children.add(child);
        }

        partnership.addChildren(children);

        population.getLivingPeople().addPartnershipToIndex(partnership);

        return partnership;
    }



    public static IPerson formOrphanChild(MonthDate currentDate, CompoundTimeUnit birthTimeStep, Population population) {
        return makePerson(currentDate, birthTimeStep, null, population);
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

    public static IPerson makePerson(Date birthDate, IPartnership parentsPartnership, Population population) {

        Person person = new Person(getSex(population.getPopulationCounts()), birthDate, parentsPartnership);

        // OZGUR - this is where your stuff is currently being called from
        person.setFirstName(firstNameGenerator.getName(person));
        person.setSurname(surnameGenerator.getName(person));


        population.getLivingPeople().addPerson(person);

        return person;

    }

    public static IPerson makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnership parentsPartnership, Population population) {

        return makePerson(birthDateSelector.selectDate(currentDate, birthTimeStep), parentsPartnership, population);

    }
}
