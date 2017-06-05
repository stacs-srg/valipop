package events.death;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateSelection.DateSelector;
import dateModel.dateSelection.DeathDateSelector;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.PersonCollection;
import simulationEntities.population.dataStructure.Population;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;

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

            Collection<IPerson> peopleToKill;
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


    private int killPeople(Collection<IPerson> people,
                            PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population) {

        int killed = 0;

        for(IPerson person : people) {

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
