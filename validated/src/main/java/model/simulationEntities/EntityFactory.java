package model.simulationEntities;

import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.PersonNotFoundException;
import model.dateSelection.BirthDateSelector;
import model.dateSelection.DateSelector;
import model.nameGeneration.FirstNameGenerator;
import model.nameGeneration.NameGenerator;
import model.nameGeneration.SurnameGenerator;
import model.simulationLogic.Simulation;
import model.simulationLogic.stochastic.PopulationCounts;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateClock;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EntityFactory {

    private static Random randomNumberGenerator = new Random();
    private static NameGenerator firstNameGenerator = new FirstNameGenerator();
    private static NameGenerator surnameGenerator = new SurnameGenerator();
    private static DateSelector birthDateSelector = new BirthDateSelector();


    public static IPartnership formNewChildrenInPartnership(int numberOfChildren, IPerson mother, DateClock currentDate,
                                                            CompoundTimeUnit birthTimeStep, PeopleCollection population) {

        IPartnership partnership = new Partnership(mother, currentDate);


        List<IPerson> children = new ArrayList<>(numberOfChildren);

        if(numberOfChildren == 0) {
            System.out.println("C A ZERO");
        }

        for(int c = 0; c < numberOfChildren; c++) {
            children.add(makePerson(currentDate, birthTimeStep, partnership, population));
        }

        partnership.addChildren(children);

        population.addPartnershipToIndex(partnership);

        return partnership;
    }

    // LATEST method
    public static IPartnership formNewPartnership(int numberOfChildren, IPerson mother, DateClock currentDate,
                                                            CompoundTimeUnit birthTimeStep, PeopleCollection population) {

        IPartnership partnership = new Partnership(mother, currentDate);

        try {
            population.removePerson(mother);
        } catch (PersonNotFoundException e) {
            throw new Error("Person not found in population - PeopleCollection has become inconsistent", e);
        }

        List<IPerson> children = new ArrayList<>(numberOfChildren);

        if(numberOfChildren == 0) {
            System.out.println("C B ZERO");
        }

        for(int c = 0; c < numberOfChildren; c++) {
            children.add(makePerson(currentDate, birthTimeStep, partnership, population));
        }

        partnership.addChildren(children);

        mother.recordPartnership(partnership);

        population.addPerson(mother);

        population.addPartnershipToIndex(partnership);

        return partnership;
    }

    public static IPerson formOrphanChild(DateClock currentDate, CompoundTimeUnit birthTimeStep, PeopleCollection population) {
        return makePerson(currentDate, birthTimeStep, null, population);
    }

    private static char getSex() {

        // TODO move over to a specified m to f ratio
        double sexBalance = Simulation.pc.getLivingSexRatio();

        if(sexBalance <= 1) {
            Simulation.pc.newMale();
            return 'M';
        } else {
            Simulation.pc.newFemale();
            return 'F';
        }
//
//        if (randomNumberGenerator.nextBoolean()) {
//            return 'M';
//        } else {
//            return 'F';
//        }

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
