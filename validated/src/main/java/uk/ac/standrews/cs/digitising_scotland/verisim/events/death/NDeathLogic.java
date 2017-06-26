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
package uk.ac.standrews.cs.digitising_scotland.verisim.events.death;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.DeathDateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PersonCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NDeathLogic implements EventLogic {

    private DateSelector deathDateSelector = new DeathDateSelector();

    // Move from year to sim date and time step
    public void handleEvent(Config config,
                            AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population, PopulationStatistics desiredPopulationStatistics) {

        handleDeathsForSex('M', config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);
        handleDeathsForSex('F', config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);

    }

    public static int tKilled = 0;

    private void handleDeathsForSex(char sex, Config config,
                                    AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                                    Population population, PopulationStatistics desiredPopulationStatistics) {

        int killedAtTS = 0;

        PersonCollection ofSexLiving = getLivingPeopleOfSex(sex, population);
        Iterator<AdvancableDate> divDates = ofSexLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvancableDate divDate;
        // For each division in the population data store upto the current date
        while(divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate, currentDate).getCount();
            int peopleOfAge = ofSexLiving.getNumberOfPersons(divDate, consideredTimePeriod);

            // gets death rate for people of age at the current date
            StatsKey key = new DeathStatsKey(age, peopleOfAge, consideredTimePeriod, currentDate, sex);
            DeterminedCount determinedCount = desiredPopulationStatistics.getDeterminedCount(key);

            // Calculate the appropriate number to kill and then kill
            Integer numberToKill = ((SingleDeterminedCount) determinedCount).getDeterminedCount();

            Collection<IPersonExtended> peopleToKill;
            try {
                peopleToKill =
                        ofSexLiving.removeNPersons(numberToKill, divDate, consideredTimePeriod, true);
            } catch (InsufficientNumberOfPeopleException e) {
                throw new Error("Insufficient number of people to kill, - this has occured when selecting a less " +
                        "than 1 proportion of a population");
            }

            int killed = killPeople(peopleToKill, desiredPopulationStatistics, currentDate, consideredTimePeriod, population);
            killedAtTS += killed;

            // Returns the number killed to the distribution manager
            determinedCount.setFufilledCount(killed);
            desiredPopulationStatistics.returnAchievedCount(determinedCount);
        }

        tKilled += killedAtTS;
        System.out.print(killedAtTS + "\t");
    }


    private int killPeople(Collection<IPersonExtended> people,
                            PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population) {

        int killed = 0;

        for(IPersonExtended person : people) {

            // choose date of death
            Date deathDate = deathDateSelector.selectDate(person, desiredPopulationStatistics, currentDate, consideredTimePeriod);

            // execute death
            if(person.recordDeath(deathDate, population)) {
                killed++;
            }

            // move person to correct place in data structure
            population.getDeadPeople().addPerson(person);

        }

        return killed;
    }

    private PersonCollection getLivingPeopleOfSex(char sex, Population population) {
        PersonCollection ofSexLiving;

        if(Character.toLowerCase(sex) == 'm') {
            ofSexLiving = population.getLivingPeople().getMales();
        } else {
            ofSexLiving = population.getLivingPeople().getFemales();
        }

        return ofSexLiving;
    }

}
