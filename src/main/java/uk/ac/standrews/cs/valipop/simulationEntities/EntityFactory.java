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
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection.MarriageDateSelector;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EntityFactory {

    public static MarriageDateSelector marriageDateSelector = new MarriageDateSelector();

    private static DateSelector birthDateSelector = new DateSelector();

    public static IPartnershipExtended formNewChildrenInPartnership(int numberOfChildren, IPersonExtended father, IPersonExtended mother, AdvanceableDate currentDate,
                                                                    CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps, boolean illegitimate, boolean marriedAtBirth) throws PersonNotFoundException {

        try {
            population.getLivingPeople().removePerson(mother);
            population.getLivingPeople().removePerson(father);

        } catch (PersonNotFoundException e) {
            throw new PersonNotFoundException("Could not remove parents for population position update when creating new partnership");
        }

        IPartnershipExtended partnership = new Partnership(father, mother);

        List<IPersonExtended> children = new ArrayList<>(numberOfChildren);

        // This ensures twins are born on the same day
        ExactDate childrenBirthDate = null;
        IPersonExtended aChild = null;

        // the loop here allows for the multiple children in pregnancies
        for (int c = 0; c < numberOfChildren; c++) {
            IPersonExtended child;
            if (childrenBirthDate == null) {
                // Make first child
                child = makePerson(currentDate, birthTimeStep, partnership, population, ps, illegitimate);
            } else {
                // Make subsequent children
                child = makePerson(childrenBirthDate, partnership, population, ps, illegitimate);
            }
            childrenBirthDate = child.getBirthDate_ex();
            children.add(child);

            aChild = child;
        }

        if (marriedAtBirth) {

            // nth child - then previous child birth date - NOT possible here, this is a method for new partnerships
            // first child - then death or divorce of previous spouses or coming of age

            // for mother
            Date motherLastPrevPartneringEvent = mother.getDateOfLastLegitimatePartnershipEventBeforeDate(childrenBirthDate);

            // for father
            Date fatherLastPrevPartneringEvent = father.getDateOfLastLegitimatePartnershipEventBeforeDate(childrenBirthDate);

            Date earliestPossibleMarriageDate = DateUtils.getLatestDate(motherLastPrevPartneringEvent, fatherLastPrevPartneringEvent);

            if (DateUtils.dateBefore(earliestPossibleMarriageDate, childrenBirthDate)) {
                // if there is a tenable marriage date then select it
                partnership.setMarriageDate(marriageDateSelector.selectDate(earliestPossibleMarriageDate, childrenBirthDate, ps.getRandomGenerator()));
                aChild.setMarriageBaby(true);
            } else {
                partnership.setMarriageDate(null);
            }

        } else {
            partnership.setMarriageDate(null);
        }

        partnership.setPartnershipDate(childrenBirthDate);
        partnership.addChildren(children);

        population.getLivingPeople().addPartnershipToIndex(partnership);

        mother.recordPartnership(partnership);
        father.recordPartnership(partnership);

//        System.out.println("re-added mother: " + mother.getId());
//        System.out.println("re-added father: " + father.getId());

        // re-insert parents into population, this allows their position in the data structure to be updated
        population.getLivingPeople().addPerson(mother);
        population.getLivingPeople().addPerson(father);

        return partnership;
    }

    public static IPersonExtended formOrphanChild(AdvanceableDate currentDate, CompoundTimeUnit birthTimeStep, Population population, PopulationStatistics ps) {
        return makePerson(currentDate, birthTimeStep, null, population, ps);
    }

    private static char getSex(PopulationCounts pc, PopulationStatistics ps, Date currentDate) {

        double sexBalance = pc.getAllTimeSexRatio();

        if (sexBalance < ps.getMaleProportionOfBirths(currentDate)) {

            pc.newMale();
            return 'M';
        } else {

            pc.newFemale();
            return 'F';
        }
    }

    public static IPersonExtended makePerson(Date birthDate, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps, boolean illegitimate) {

        Person person = new Person(getSex(population.getPopulationCounts(), ps, birthDate), birthDate, parentsPartnership, ps, illegitimate);

        population.getLivingPeople().addPerson(person);

        return person;
    }

    public static IPersonExtended makePerson(Date birthDate, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps) {

        return makePerson(birthDate, parentsPartnership, population, ps, false);
    }

    public static IPersonExtended makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps) {

        return makePerson(birthDateSelector.selectDate(currentDate, birthTimeStep, ps.getRandomGenerator()), parentsPartnership, population, ps);
    }

    public static IPersonExtended makePerson(Date currentDate, CompoundTimeUnit birthTimeStep, IPartnershipExtended parentsPartnership, Population population, PopulationStatistics ps, boolean illegitimate) {

        return makePerson(birthDateSelector.selectDate(currentDate, birthTimeStep, ps.getRandomGenerator()), parentsPartnership, population, ps, illegitimate);
    }
}
