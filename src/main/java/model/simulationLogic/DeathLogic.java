package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.DateClock;
import utils.time.DateUtils;

import java.util.Collection;

import static utils.time.TimeUnit.YEAR;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathLogic {

    private static Logger log = LogManager.getLogger(DeathLogic.class);


    public static int handleDeaths(Config config, DateClock currentDate, PopulationStatistics desiredPopulationStatistics,
                              PeopleCollection livingPopulation, PeopleCollection deadPopulation) {

        int deathCount = 0;

        // handle deaths for the next year
        // for each year of birth since tS
        for (DateClock yearOfBirth = config.getTS(); DateUtils.dateBefore(yearOfBirth, currentDate); yearOfBirth = yearOfBirth.advanceTime(1, YEAR)) {

            // get count of people of given age
            int numberOfMales = countMalesBornIn(livingPopulation, yearOfBirth);
            int numberOfFemales = countFemalesBornIn(livingPopulation, yearOfBirth);

            // DATA - get rate of death by age and gender
            Double maleDeathRate = desiredPopulationStatistics.getDeathRates(currentDate, 'm').getData(getAge(currentDate, yearOfBirth));
            Double femaleDeathRate = desiredPopulationStatistics.getDeathRates(currentDate, 'f').getData(getAge(currentDate, yearOfBirth));

            // use DATA to calculate who to kill off
            int malesToDie = calculateNumberToDie(numberOfMales, maleDeathRate);
            int femalesToDie = calculateNumberToDie(numberOfFemales, femaleDeathRate);

            deathCount += malesToDie;
            deathCount += femalesToDie;

            Collection<Person> deadMales = removeMalesToDieFromPopulation(livingPopulation, yearOfBirth, malesToDie);
            Collection<Person> deadFemales = removeFemalesToDieFromPopulation(livingPopulation, yearOfBirth, femalesToDie);

            // for each to be killed
            for (Person m : deadMales) {
                // TODO execute death at a time in the next year
                m.recordDeath(currentDate);
                deadPopulation.addPerson(m);
            }

            for (Person f : deadFemales) {
                // TODO execute death at a time in the next year
                f.recordDeath(currentDate);
                deadPopulation.addPerson(f);
            }

        }

        log.info("Deaths handled: " + currentDate.toString() + " - " + deathCount);
        System.out.println("Deaths handled: " + currentDate.toString() + " - " + deathCount);
        return deathCount;

    }

    private static Collection<Person> removeMalesToDieFromPopulation(PeopleCollection population, DateClock yearOfBirth, int numberToDie) {
        return population.getMales().removeNPersons(numberToDie, yearOfBirth.getYearDate());
    }

    private static Collection<Person> removeFemalesToDieFromPopulation(PeopleCollection population, DateClock yearOfBirth, int numberToDie) {
        return population.getFemales().removeNPersons(numberToDie, yearOfBirth.getYearDate());
    }

    private static int countFemalesBornIn(PeopleCollection people, DateClock yearOfBirth) {
        return people.getFemales().getByYear(yearOfBirth).size();
    }

    private static int countMalesBornIn(PeopleCollection people, DateClock yearOfBirth) {
        return people.getMales().getByYear(yearOfBirth).size();
    }

    private static int getAge(DateClock currentDate, DateClock birthDate) {
        return DateUtils.differenceInYears(currentDate, birthDate).getCount();
    }

    public static int calculateNumberToDie(int people, Double deathRate) {
        return SharedLogic.calculateNumberToHaveEvent(people, deathRate);
    }

}
