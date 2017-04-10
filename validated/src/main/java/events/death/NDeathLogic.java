package events.death;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateSelection.DateSelector;
import dateModel.dateSelection.DeathDateSelector;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.PersonCollection;
import simulationEntities.population.dataStructure.Population;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import utils.specialTypes.dataKeys.DataKey;
import utils.specialTypes.dataKeys.DeathDataKey;

import java.util.Collection;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NDeathLogic implements EventLogic {

    DateSelector deathDateSelector = new DeathDateSelector();

    // Move from year to sim date and time step
    public void handleEvent(Config config,
                             AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                             Population population, PopulationStatistics desiredPopulationStatistics) {

        handleDeathsForSex('M', config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);
        handleDeathsForSex('F', config, currentDate, consideredTimePeriod, population, desiredPopulationStatistics);

    }

    private void handleDeathsForSex(char sex, Config config,
                                    AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                                    Population population, PopulationStatistics desiredPopulationStatistics) {

        PersonCollection ofSexLiving;

        if(Character.toLowerCase(sex) == 'm') {
            ofSexLiving = population.getLivingPeople().getMales();
        } else {
            ofSexLiving = population.getLivingPeople().getFemales();
        }

        Set<AdvancableDate> divDates = ofSexLiving.getDivisionDates();

        // For each division in the population data store
        for(AdvancableDate divDate : divDates) {

            // Up until the current dat
            if(DateUtils.dateBeforeOrEqual(divDate, currentDate)) {

                // Calcs age of people in the selected division at the current date
                Integer age = DateUtils.differenceInYears(divDate, currentDate).getCount();
                int peopleOfAge = ofSexLiving.getNumberOfPersons(divDate, consideredTimePeriod);

                // gets death rate for people of age at the current date
                DataKey key = new DeathDataKey(age, peopleOfAge);
                double deathRate = desiredPopulationStatistics.getDeathRates(currentDate, sex).getCorrectingRate(key, consideredTimePeriod);


                // Calculate the appropriate number to kill and then kill
                Integer numberToKill = (int) Math.round(peopleOfAge * deathRate);

                Collection<IPerson> peopleToKill;
                try {
                    peopleToKill = ofSexLiving.removeNPersons(numberToKill, divDate, consideredTimePeriod, true);
                } catch (InsufficientNumberOfPeopleException e) {
                    throw new Error("Insufficient number of people to kill, - this has occured when killing a less " +
                            "than 1 proportion of a population");
                }

                int killed = killPeople(peopleToKill, config, currentDate, consideredTimePeriod, population);

                // Calculates achieved death rate and returns this to the distribution manager
                double appliedRate = 0;
                if(peopleOfAge != 0) {
                    appliedRate = killed / (double) peopleOfAge;
                }

                desiredPopulationStatistics.getDeathRates(currentDate, sex).returnAppliedRate(key, appliedRate, consideredTimePeriod);

            }
        }


    }


    private int killPeople(Collection<IPerson> people,
                            Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population) {

        int killed = 0;

        for(IPerson person : people) {

            // choose date of death
            Date deathDate = deathDateSelector.selectDate(person, config, currentDate, consideredTimePeriod);

            // execute death
            if(person.recordDeath(deathDate, population)) {
                killed++;
            }

            // move person to correct place in data structure
            population.getDeadPeople().addPerson(person);

        }

        return killed;
    }

}
