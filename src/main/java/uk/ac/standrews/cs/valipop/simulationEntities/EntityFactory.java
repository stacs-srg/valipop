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
package uk.ac.standrews.cs.valipop.simulationEntities;


import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.BirthDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EntityFactory {

    private static BirthDateSelector birthDateSelector = new BirthDateSelector();

    public static IPartnershipExtended formNewChildrenInPartnership(int numberOfChildren, IPersonExtended father, IPersonExtended mother, AdvancableDate currentDate,
                                                                    CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps) throws PersonNotFoundException {

        try {
            population.getLivingPeople().removePerson(mother);
            population.getLivingPeople().removePerson(father);
        } catch (PersonNotFoundException e) {
            throw new PersonNotFoundException("Could not remove parents for population positon update when creating " +
                    "new partnership");
        }

        IPartnershipExtended partnership = new Partnership(father, mother);

        List<IPersonExtended> children = new ArrayList<>(numberOfChildren);

        // This ensures twins are born on the same day
        Date childrenBirthDate = null;

        for(int c = 0; c < numberOfChildren; c++) {
            IPersonExtended child;
            if(childrenBirthDate == null) {
                child = makePerson(currentDate, birthTimeStep, partnership, population, ps);
                childrenBirthDate = child.getBirthDate_ex();
            } else {
                child = makePerson(childrenBirthDate, partnership, population, ps);
            }
            children.add(child);
        }

        partnership.setPartnershipDate(childrenBirthDate);
        partnership.addChildren(children);

        population.getPopulationCounts().newPartnership();

        population.getLivingPeople().addPartnershipToIndex(partnership);

        mother.recordPartnership(partnership);
        father.recordPartnership(partnership);

        population.getLivingPeople().addPerson(mother);
        population.getLivingPeople().addPerson(father);

        return partnership;
    }



    public static IPersonExtended formOrphanChild(AdvancableDate currentDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps) {
        return makePerson(currentDate, birthTimeStep, null, population, ps);
    }

    private static char getSex(PopulationCounts pc, PopulationStatistics ps, Date currentDate) {

        double sexBalance = pc.getAllTimeSexRatio();

        if(sexBalance < ps.getMaleProportionOfBirths(currentDate)) {

            pc.newMale();
            return 'M';
        } else {

            pc.newFemale();
            return 'F';
        }

    }

    public static IPersonExtended makePerson(Date birthDate, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps) {

        Person person = new Person(getSex(population.getPopulationCounts(), ps, birthDate), birthDate, parentsPartnership, ps);

        population.getLivingPeople().addPerson(person);

        return person;

    }

    public static IPersonExtended makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps) {

        return makePerson(birthDateSelector.selectDate(currentDate, birthTimeStep, ps.getRandomGenerator()), parentsPartnership, population, ps);

    }
}
