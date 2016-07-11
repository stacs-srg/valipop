package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.IPerson;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.CompoundTimeUnit;
import utils.time.Date;
import utils.time.DateClock;
import utils.time.DateUtils;

import java.util.Collection;
import java.util.Random;

import static utils.time.TimeUnit.YEAR;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathLogic {

    private static Logger log = LogManager.getLogger(DeathLogic.class);
    private static Random randomNumberGenerator = new Random();


    public static int handleDeaths(Config config, DateClock currentDate, PopulationStatistics desiredPopulationStatistics,
                                   PeopleCollection livingPopulation, PeopleCollection deadPopulation, CompoundTimeUnit deathTimeStep) throws InsufficientNumberOfPeopleException {

        int deathCount = 0;

        Date trueCurrentDate = currentDate.advanceTime(deathTimeStep.negative());
        // advanceTime(deathTimeStep.negative())

        // handle deaths for the next year
        // for each year of birth since tS
        for (DateClock yearOfBirth = config.getTS(); DateUtils.dateBefore(yearOfBirth, trueCurrentDate); yearOfBirth = yearOfBirth.advanceTime(1, YEAR)) {

            // get count of people of given age
            int numberOfMales = countMalesBornIn(livingPopulation, yearOfBirth);
            int numberOfFemales = countFemalesBornIn(livingPopulation, yearOfBirth);

            // DATA - get rate of death by age and gender
            Double maleDeathRate = desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'm').getData(getAge(trueCurrentDate, yearOfBirth)) * config.getDeathTimeStep().toDecimalRepresentation();

//            maleDeathRate = (1 * maleDeathRate) / (1 + (1 * 0.5 * maleDeathRate));

            Double femaleDeathRate = desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'f').getData(getAge(trueCurrentDate, yearOfBirth)) * config.getDeathTimeStep().toDecimalRepresentation();

//            femaleDeathRate = (1 * femaleDeathRate) / (1 + (1 * 0.5 * femaleDeathRate));

            // use DATA to calculate who to kill off
            int malesToDie = calculateNumberToDie(numberOfMales, maleDeathRate);
            int femalesToDie = calculateNumberToDie(numberOfFemales, femaleDeathRate);


            deathCount += malesToDie;
            deathCount += femalesToDie;

            Collection<IPerson> deadMales = removeMalesToDieFromPopulation(livingPopulation, yearOfBirth, malesToDie);
            Collection<IPerson> deadFemales = removeFemalesToDieFromPopulation(livingPopulation, yearOfBirth, femalesToDie);

//            System.out.println(currentDate.getYear() + " M yob " + yearOfBirth.getYear() + " nO " + numberOfMales + " @DR " + maleDeathRate + " res " + malesToDie + " act " + deadMales.size());
//            System.out.println(currentDate.getYear() + " F yob " + yearOfBirth.getYear() + " nO " + numberOfFemales + " @DR " + femaleDeathRate + " res " + femalesToDie + " act " + deadFemales.size());

            // for each to be killed
            for (IPerson m : deadMales) {
                m.causeDeathInTimePeriod(currentDate, deathTimeStep);
                deadPopulation.addPerson(m);


                Date dod = m.getDeathDate();
                if(DateUtils.dateBefore(currentDate, dod)) {
                    if(DateUtils.differenceInDays(currentDate, dod) != 0) {
                        System.out.println("M After Current Date " + DateUtils.differenceInDays(currentDate, dod));
                    }
                }

            }


            for (IPerson f : deadFemales) {
                f.causeDeathInTimePeriod(currentDate, deathTimeStep);
                deadPopulation.addPerson(f);

                Date dod = f.getDeathDate();
                if(DateUtils.dateBefore(currentDate, dod)) {
                    if(DateUtils.differenceInDays(currentDate, dod) != 0) {
                        System.out.println("F After Current Date " + DateUtils.differenceInDays(currentDate, dod));
                    }
                }

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

    private static int getAge(Date currentDate, Date birthDate) {
        return DateUtils.differenceInYears(currentDate, birthDate).getCount();
    }

    public static int calculateNumberToDie(int people, Double deathRate) {

        double toHaveEvent = people * deathRate;
        int flooredToHaveEvent = (int) toHaveEvent;
        toHaveEvent -= flooredToHaveEvent;

        // this is a random dice roll to see if the fraction of a has the event or not

        if(deathRate <= 0.001) {

            if (randomNumberGenerator.nextInt(100) < toHaveEvent * 100) {
                flooredToHaveEvent++;
            }

        } else {

            if (toHaveEvent > 0.5) {
                flooredToHaveEvent++;
            }
//            if (randomNumberGenerator.nextDouble() < toHaveEvent) {
//                flooredToHaveEvent++;
//            }
//        } else {
//            flooredToHaveEvent++;
//        }

        }

        return flooredToHaveEvent;

    }

}
