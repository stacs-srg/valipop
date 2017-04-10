package events.death;

import config.Config;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.population.dataStructure.Population;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DeathLogic implements EventLogic {

    private static Logger log = LogManager.getLogger(DeathLogic.class);
    private static Random randomNumberGenerator = new Random();


    public void handleEvent(Config config,
                            AdvancableDate currentDate, CompoundTimeUnit deathTimeStep,
                            Population population, PopulationStatistics desiredPopulationStatistics) {

//        int deathCount = 0;
//
//        double yearForwardWeighting = 1;
//        if(currentDate.getMonth() == 1) {
//            yearForwardWeighting = 1.1;
//        }
//
//        Date trueCurrentDate = currentDate.advanceTime(deathTimeStep.negative());
//        // advanceTime(deathTimeStep.negative())
//
//        // handle deaths for the next year
//        // for each year of birth since tS
//        for (MonthDate yearOfBirth = config.getTS(); DateUtils.dateBeforeOrEqual(yearOfBirth, trueCurrentDate); yearOfBirth = yearOfBirth.advanceTime(1, YEAR)) {
//
//            int age = DateUtils.differenceInYears(yearOfBirth, currentDate).getCount();
//
//            // get count of people of given age
//            int numberOfMales = countMalesBornIn(population.getLivingPeople(), yearOfBirth);
//            int numberOfFemales = countFemalesBornIn(population.getLivingPeople(), yearOfBirth);
//
//            StatsKey maleKey = new StatsKey(age, numberOfMales);
//            StatsKey femaleKey = new StatsKey(age, numberOfFemales);
//
//            // DATA - get rate of death by age and gender
//            Double maleDeathRate = desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'm').getCorrectingRate(maleKey, config.getDeathTimeStep()) * yearForwardWeighting;
//
//            // EDIT put nQx s back in here
//
////            maleDeathRate = (1 * maleDeathRate) / (1 + (1 * 0.5 * maleDeathRate));
//
//            Double femaleDeathRate = desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'f').getCorrectingRate(femaleKey, config.getDeathTimeStep()) * yearForwardWeighting;
//
////            femaleDeathRate = (1 * femaleDeathRate) / (1 + (1 * 0.5 * femaleDeathRate));
//
//            // use DATA to calculate who to kill off
//            int malesToDie = calculateNumberToDie(numberOfMales, maleDeathRate);
//            int femalesToDie = calculateNumberToDie(numberOfFemales, femaleDeathRate);
//
//            if(malesToDie < 0) {
//                malesToDie = 0;
//            }
//
//            if(femalesToDie < 0) {
//                femalesToDie = 0;
//            }
//
//            deathCount += malesToDie;
//            deathCount += femalesToDie;
//
//            Collection<IPerson> deadMales;
//            Collection<IPerson> deadFemales;
//
//            try {
//                deadMales = removeMalesToDieFromPopulation(population.getLivingPeople(), yearOfBirth, malesToDie);
//                deadFemales = removeFemalesToDieFromPopulation(population.getLivingPeople(), yearOfBirth, femalesToDie);
//            } catch (InsufficientNumberOfPeopleException e) {
//                throw new Error("Insufficient number of people to kill, - this has occured when killing a less " +
//                        "than 1 proportion of a population");
//            }
//
////            System.out.println(currentDate.getYear() + " M yob " + yearOfBirth.getYear() + " nO " + numberOfMales + " @DR " + maleDeathRate + " res " + malesToDie + " act " + deadMales.size());
////            System.out.println(currentDate.getYear() + " F yob " + yearOfBirth.getYear() + " nO " + numberOfFemales + " @DR " + femaleDeathRate + " res " + femalesToDie + " act " + deadFemales.size());
//
//            // for each to be killed
//            for (IPerson m : deadMales) {
//                m.causeEventInTimePeriod(EventType.MALE_DEATH, currentDate, deathTimeStep);
//                population.getDeadPeople().addPerson(m);
//
//
//                Date dod = m.getDeathDate();
//                if(DateUtils.dateBeforeOrEqual(currentDate, dod)) {
//                    if(DateUtils.differenceInDays(currentDate, dod) != 0) {
//                        System.out.println("M After Current Date " + DateUtils.differenceInDays(currentDate, dod));
//                    }
//                }
//
//            }
//
//
//            for (IPerson f : deadFemales) {
//                f.causeEventInTimePeriod(EventType.FEMALE_DEATH, currentDate, deathTimeStep);
//                population.getDeadPeople().addPerson(f);
//
//                Date dod = f.getDeathDate();
//                if(DateUtils.dateBeforeOrEqual(currentDate, dod)) {
//                    if(DateUtils.differenceInDays(currentDate, dod) != 0) {
//                        System.out.println("F After Current Date " + DateUtils.differenceInDays(currentDate, dod));
//                    }
//                }
//
//            }
//
//            double appliedMaleRate = deadMales.size() / (double) numberOfMales;
//
//            double appliedFemaleRate = deadFemales.size() / (double) numberOfFemales;
//
////            if(Double.isNaN(appliedMaleRate)) {
////                 System.out.println(deadMales.size() + " : dS   |   nOM : " + numberOfMales);
////            }
////
////            if(Double.isNaN(appliedFemaleRate)) {
////                System.out.println(deadFemales.size() + " : dS   |   nOM : " + numberOfFemales);
////            }
//
//            if(numberOfMales > 0) {
//                desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'm').returnAchievedCount(maleKey, appliedMaleRate, config.getDeathTimeStep());
//            }
//
//            if(numberOfFemales > 0) {
//                desiredPopulationStatistics.getDeathRates(trueCurrentDate, 'f').returnAchievedCount(femaleKey, appliedFemaleRate, config.getDeathTimeStep());
//            }
//
//        }
//
//        log.info("Deaths handled: " + currentDate.toString() + " - " + deathCount);
////        System.out.println("Deaths handled: " + currentDate.toString() + " - " + deathCount);
////        return deathCount;

    }

//    private static Collection<IPerson> removeMalesToDieFromPopulation(PeopleCollection population, MonthDate yearOfBirth, int numberToDie) throws InsufficientNumberOfPeopleException {
//        return population.getMales().removeNPersons(numberToDie, yearOfBirth.getYearDate(), false);
//    }
//
//    private static Collection<IPerson> removeFemalesToDieFromPopulation(PeopleCollection population, MonthDate yearOfBirth, int numberToDie) throws InsufficientNumberOfPeopleException {
//        return population.getFemales().removeNPersons(numberToDie, yearOfBirth.getYearDate(), false);
//    }
//
//    private static int countFemalesBornIn(PeopleCollection people, MonthDate yearOfBirth) {
//        return people.getFemales().getByYear(yearOfBirth).size();
//    }
//
//    private static int countMalesBornIn(PeopleCollection people, MonthDate yearOfBirth) {
//        return people.getMales().getByYear(yearOfBirth).size();
//    }
//
//    private static int getAge(Date currentDate, Date birthDate) {
//        return DateUtils.differenceInYears(currentDate, birthDate).getCount();
//    }

    public static int calculateNumberToDie(int people, Double deathRate) {


        double toHaveEvent = people * deathRate;
        int flooredToHaveEvent = (int) toHaveEvent;
        toHaveEvent -= flooredToHaveEvent;

//        if (randomNumberGenerator.nextInt(100) < toHaveEvent * 100) {
//            flooredToHaveEvent++;
//        }

//        if (randomNumberGenerator.nextDouble() < toHaveEvent) {
//            flooredToHaveEvent++;
//        }

        // this is a random dice roll to see if the fraction of a has the event or not

        if(deathRate <= 0.001) {

            if (randomNumberGenerator.nextInt(100) < toHaveEvent * 100) {
                flooredToHaveEvent++;
            }

        } else {
//
//            if (toHaveEvent > 0.5) {
//                flooredToHaveEvent++;
//            }
            if (randomNumberGenerator.nextDouble() < toHaveEvent) {
                flooredToHaveEvent++;
            }
////        } else {
////            flooredToHaveEvent++;
        }
//
//        }

        if(deathRate.isNaN()) {
            System.out.println("NAN: thus toDie: " + flooredToHaveEvent);
        }

//        flooredToHaveEvent++;

        return flooredToHaveEvent;

    }

}
