package events.death;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.YearDate;
import dateModel.dateSelection.DateSelector;
import dateModel.dateSelection.DeathDateSelector;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.PersonCollection;
import simulationEntities.population.dataStructure.Population;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
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

        Set<YearDate> yobs = ofSexLiving.getYOBs();

        for(YearDate yob : yobs) {

            if(DateUtils.dateBefore(yob, currentDate)) {

                Integer age = DateUtils.differenceInYears(yob, currentDate).getCount();
                int peopleOfAge = ofSexLiving.getByYear(yob).size();

                DataKey key = new DeathDataKey(age, peopleOfAge);
                double deathRate = desiredPopulationStatistics.getDeathRates(currentDate, sex).getCorrectingRate(key);

                deathRate = deathRate * consideredTimePeriod.toDecimalRepresentation();

                Integer numberToKill = new Long(Math.round(peopleOfAge * deathRate)).intValue();

                Collection<IPerson> peopleToKill = null;
                try {
                    peopleToKill = ofSexLiving.removeNPersons(numberToKill, yob, true);
                } catch (InsufficientNumberOfPeopleException e) {
                    throw new Error("Insufficient number of people to kill, - this has occured when killing a less " +
                            "than 1 proportion of a population");
                }

                int killed = killPeople(peopleToKill, config, currentDate, consideredTimePeriod, population);

                double appliedRate = killed / (double) peopleOfAge;

                appliedRate = appliedRate / consideredTimePeriod.toDecimalRepresentation();

                desiredPopulationStatistics.getDeathRates(currentDate, sex).returnAppliedRate(key, appliedRate);
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
