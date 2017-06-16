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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities;


import uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names.FirstNameGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names.NameGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names.SurnameGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.BirthDateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.Person;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;

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

        population.getPopulationCounts().newPartnership();

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
