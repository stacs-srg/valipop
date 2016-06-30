package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.IPerson;
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
                                   PeopleCollection livingPopulation, PeopleCollection deadPopulation) throws InsufficientNumberOfPeopleException {

        int deathCount = 0;

        // handle deaths for the next year
        // for each year of birth since tS
        for (DateClock yearOfBirth = config.getTS(); DateUtils.dateBefore(yearOfBirth, currentDate); yearOfBirth = yearOfBirth.advanceTime(1, YEAR)) {

            // get count of people of given age
            int numberOfMales = countMalesBornIn(livingPopulation, yearOfBirth);
            int numberOfFemales = countFemalesBornIn(livingPopulation, yearOfBirth);

            // DATA - get rate of death by age and gender
            Double maleDeathRate = desiredPopulationStatistics.getDeathRates(currentDate, 'm').getData(getAge(currentDate, yearOfBirth)) * config.getDeathTimeStep().toDecimalRepresentation();
            Double femaleDeathRate = desiredPopulationStatistics.getDeathRates(currentDate, 'f').getData(getAge(currentDate, yearOfBirth)) * config.getDeathTimeStep().toDecimalRepresentation();

            // use DATA to calculate who to kill off
            int malesToDie = calculateNumberToDie(numberOfMales, maleDeathRate);
            int femalesToDie = calculateNumberToDie(numberOfFemales, femaleDeathRate);

            deathCount += malesToDie;
            deathCount += femalesToDie;

            Collection<IPerson> deadMales = removeMalesToDieFromPopulation(livingPopulation, yearOfBirth, malesToDie);
            Collection<IPerson> deadFemales = removeFemalesToDieFromPopulation(livingPopulation, yearOfBirth, femalesToDie);

            // for each to be killed
            for (IPerson m : deadMales) {
                // TODO execute death at a time in the past year
                m.recordDeath(currentDate);
                deadPopulation.addPerson(m);
            }

            for (IPerson f : deadFemales) {
                // TODO execute death at a time in the past year
                f.recordDeath(currentDate);
                deadPopulation.addPerson(f);
            }

        }

        log.info("Deaths handled: " + currentDate.toString() + " - " + deathCount);
        System.out.println("Deaths handled: " + currentDate.toString() + " - " + deathCount);
        return deathCount;

    }

    private static Collection<IPerson> removeMalesToDieFromPopulation(PeopleCollection population, DateClock yearOfBirth, int numberToDie) throws InsufficientNumberOfPeopleException {
        return population.getMales().removeNPersons(numberToDie, yearOfBirth.getYearDate());
    }

    private static Collection<IPerson> removeFemalesToDieFromPopulation(PeopleCollection population, DateClock yearOfBirth, int numberToDie) throws InsufficientNumberOfPeopleException {
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
