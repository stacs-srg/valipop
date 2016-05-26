package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.DateClock;
import utils.time.DateUtils;
import utils.time.TimeUnit;

import java.util.Collection;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathLogic {

    private static Logger log = LogManager.getLogger(DeathLogic.class);


    public static int handleDeaths(Config config, DateClock currentTime, PopulationStatistics desiredPopulationStatistics,
                              PeopleCollection people, PeopleCollection deadPeople) {

        int deathCount = 0;

        // handle deaths for the next year
        // for each year of birth since tS
        for (DateClock d = config.gettS(); DateUtils.dateBefore(d, currentTime); d = d.advanceTime(1, TimeUnit.YEAR)) {

            // get count of people of given age
            int males = people.getMales().getByYear(d).size();
            int females = people.getFemales().getByYear(d).size();

            // DATA - get rate of death by age and gender
            Double maleDeathRate = desiredPopulationStatistics.getDeathRates(currentTime.getYearDate(), 'm').getData(currentTime.getYear() - d.getYear());
            Double femaleDeathRate = desiredPopulationStatistics.getDeathRates(currentTime.getYearDate(), 'f').getData(currentTime.getYear() - d.getYear());

            // use data to calculate who to kill off
            int malesToDie = calculateNumberToDie(males, maleDeathRate);
            int femalesToDie = calculateNumberToDie(females, femaleDeathRate);

            deathCount += malesToDie;
            deathCount += femalesToDie;

            Collection<Person> deadMales = people.getMales().removeNPersons(malesToDie, d.getYearDate());
            Collection<Person> deadFemales = people.getFemales().removeNPersons(femalesToDie, d.getYearDate());

            // for each to be killed
            for (Person m : deadMales) {
                // TODO execute death at a time in the next year
                m.recordDeath(currentTime);
                deadPeople.addPerson(m);
            }

            for (Person f : deadFemales) {
                // TODO execute death at a time in the next year
                f.recordDeath(currentTime);
                deadPeople.addPerson(f);
            }

        }

        log.info("Deaths handled: " + currentTime.toString() + " - " + deathCount);
        return deathCount;

    }

    public static int calculateNumberToDie(int people, Double deathRate) {
        return SharedLogic.calculateNumberToHaveEvent(people, deathRate);
    }

}
