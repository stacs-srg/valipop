package model.simulationLogic;

import config.Config;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.PersonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.time.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InitLogic {

    public static Logger log = LogManager.getLogger(InitLogic.class);

    private static int currentHypotheticalPopulationSize;

    private static CompoundTimeUnit initTimeStep = new CompoundTimeUnit(1, TimeUnit.YEAR);
    private static Date endOfInitPeriod;

    private static int numberOfBirthsInThisTimestep = 0;

    public static void setUpInitParameters(Config config, PopulationStatistics desiredPopulationStatistics) {

        currentHypotheticalPopulationSize = calculateStartingPopulationSize(config);
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfInitPeriod = config.getTS().advanceTime(new CompoundTimeUnit(desiredPopulationStatistics.getOrderedBirthRates(config.getTS().getYearDate()).getMaxRowLabelValue().getValue(), TimeUnit.YEAR));
        log.info("End of Initialisation Period set: " + endOfInitPeriod.toString());

    }

    public static void handleInitPeople(Config config, DateClock currentTime, PeopleCollection people) {

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
            PersonFactory.formOrphanChild(currentTime, people);
        }

        log.info("Current Date: " + currentTime.toString() + "   Init Period | Met short fall in births with orphan children: " + shortFallInBirths);
        System.out.println("Current Date: " + currentTime.toString() + "   Init Period | Met short fall in births with orphan children: " + shortFallInBirths);

    }

    public static void incrementBirthCount(int n) {
        numberOfBirthsInThisTimestep += n;
    }

    public static boolean inInitPeriod(DateClock currentTime) {
        return DateUtils.dateBefore(currentTime, endOfInitPeriod);
    }

    public static CompoundTimeUnit getTimeStep() {
        return initTimeStep;
    }

    private static int calculateStartingPopulationSize(Config config) {
        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.getTS(), config.getT0()).getCount()));
    }

}
