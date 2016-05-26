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

    private static int birthCount = 0;

    public static void setUpInitParameters(Config config, PopulationStatistics desiredPopulationStatistics) {

        currentHypotheticalPopulationSize = (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.gettS(), config.getT0()).getCount()));
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfInitPeriod = config.gettS().advanceTime(new CompoundTimeUnit(desiredPopulationStatistics.getOrderedBirthRates(config.gettS().getYearDate()).getMaxRowLabelValue().getValue(), TimeUnit.YEAR));
        log.info("End of Initialisation Period set: " + endOfInitPeriod.toString());

        // ALTERNATIVE APPROACH
        // calculate desired birth rate to achieve seed population at Time 0, to do this:
        // for each population growth rates before Time 0 working backwards to Time start
        // apply the compound negative of the growth rate since the previous growth rate to the seed population desired size
        // GROWTH_RATES = intended growth rates for times before Time 0
        // end for

        // store the calculated start population as PRESENT_POPULATION_COUNT

    }

    public static void handleInitPeople(Config config, DateClock currentTime, PeopleCollection people) {

        // calculate yearly birth target, using:

        // deaths
        int hypotheticalDeaths = DeathLogic.calculateNumberToDie(currentHypotheticalPopulationSize, config.getSetUpDR());

        // births
        int hypotheticalBirths = BirthLogic.calculateChildrenToBeBorn(currentHypotheticalPopulationSize, config.getSetUpBR());
        int shortFallInBirths = hypotheticalBirths - birthCount;
        birthCount = 0;

        // update hypothetical population
        currentHypotheticalPopulationSize = currentHypotheticalPopulationSize + hypotheticalBirths - hypotheticalDeaths;

        // add Orphan Children to the population
        for (int i = 0; i < shortFallInBirths; i++) {
            // TODO need to vary birth date in time period (i.e. the previous year)
            people.addPerson(PersonFactory.formOrphanChild(currentTime));
        }

        log.info("Current Date: " + currentTime.toString() + "   Init Period | Met short fall in births with orphan children: " + shortFallInBirths);

    }

    public static void incrementBirthCount(int n) {
        birthCount += n;
    }

    public static boolean inInitPeriod(DateClock currentTime) {
        return DateUtils.dateBefore(currentTime, endOfInitPeriod);
    }

    public static CompoundTimeUnit getTimeStep() {
        return initTimeStep;
    }

}
