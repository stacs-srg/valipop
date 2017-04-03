package events.init;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.MonthDate;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;
import events.birth.BirthLogic;
import events.death.DeathLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.EntityFactory;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.Population;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InitLogic {

    public static Logger log = LogManager.getLogger(InitLogic.class);

    private static int currentHypotheticalPopulationSize;

    private static CompoundTimeUnit initTimeStep = new CompoundTimeUnit(1, TimeUnit.YEAR);
//    private static CompoundTimeUnit initTimeStep = new CompoundTimeUnit(6, TimeUnit.MONTH);
    private static Date endOfInitPeriod;

    private static int numberOfBirthsInThisTimestep = 0;

    public static void setUpInitParameters(Config config, PopulationStatistics desiredPopulationStatistics) {

        currentHypotheticalPopulationSize = calculateStartingPopulationSize(config);
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfInitPeriod = config.getTS().advanceTime(new CompoundTimeUnit(desiredPopulationStatistics.getOrderedBirthRates(config.getTS().getYearDate()).getLargestLabel().getValue(), TimeUnit.YEAR));
        log.info("End of Initialisation Period set: " + endOfInitPeriod.toString());

    }

    public static void handleInitPeople(Config config, MonthDate currentTime, Population population) {

        // calculate hypothetical number of expected births
        int hypotheticalBirths = BirthLogic.calculateChildrenToBeBorn(currentHypotheticalPopulationSize, config.getSetUpBR());

        int shortFallInBirths = hypotheticalBirths - numberOfBirthsInThisTimestep;
        numberOfBirthsInThisTimestep = 0;

        // calculate hypothetical number of expected deaths
        int hypotheticalDeaths = DeathLogic.calculateNumberToDie(currentHypotheticalPopulationSize, config.getSetUpDR());

        // update hypothetical population
        currentHypotheticalPopulationSize = currentHypotheticalPopulationSize + hypotheticalBirths - hypotheticalDeaths;

        // add Orphan Children to the population
        for (int i = 0; i < shortFallInBirths; i++) {
            // TODO need to vary birth date in time period (i.e. the previous year)
            EntityFactory.formOrphanChild(currentTime, InitLogic.getTimeStep(), population);
        }

        log.info("Current Date: " + currentTime.toString() + "   Init Period | Met short fall in births with orphan children: " + shortFallInBirths);
//        System.out.println("Current Date: " + currentTime.toString() + "   Init Period | Met short fall in births with orphan children: " + shortFallInBirths);

    }

    public static void incrementBirthCount(int n) {
        numberOfBirthsInThisTimestep += n;
    }

    public static boolean inInitPeriod(MonthDate currentTime) {
        return DateUtils.dateBefore(currentTime, endOfInitPeriod);
    }

    public static CompoundTimeUnit getTimeStep() {
        return initTimeStep;
    }

    private static int calculateStartingPopulationSize(Config config) {
        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.getTS(), config.getT0()).getCount()));
    }

    public static int getCurrentHypotheticalPopulationSize() {
        return currentHypotheticalPopulationSize;
    }
}
