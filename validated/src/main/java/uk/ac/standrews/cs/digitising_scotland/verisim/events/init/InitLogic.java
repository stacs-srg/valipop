/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.events.init;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.EntityFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.selectionApproaches.SharedLogic;

import java.util.Random;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class InitLogic {

    private static Random randomNumberGenerator = new Random();

    public static final Logger log = LogManager.getLogger(InitLogic.class);

    private static int currentHypotheticalPopulationSize;

    private static CompoundTimeUnit initTimeStep;
    private static Date endOfInitPeriod;

    private static int numberOfBirthsInThisTimestep = 0;

    public static void setUpInitParameters(Config config, PopulationStatistics desiredPopulationStatistics) {

        currentHypotheticalPopulationSize = calculateStartingPopulationSize(config);
        log.info("Initial hypothetical population size set: " + currentHypotheticalPopulationSize);

        endOfInitPeriod = config.getTS().advanceTime(new CompoundTimeUnit(desiredPopulationStatistics.getOrderedBirthRates(config.getTS().getYearDate()).getLargestLabel().getValue(), TimeUnit.YEAR));
        log.info("End of Initialisation Period set: " + endOfInitPeriod.toString());

        initTimeStep = config.getSimulationTimeStep();

    }

    public static int handleInitPeople(Config config, AdvancableDate currentTime, Population population, PopulationStatistics ps) {

        // calculate hypothetical number of expected births
        int hypotheticalBirths = calculateChildrenToBeBorn(currentHypotheticalPopulationSize, config.getSetUpBR() * initTimeStep.toDecimalRepresentation());

        int shortFallInBirths = hypotheticalBirths - numberOfBirthsInThisTimestep;
        numberOfBirthsInThisTimestep = 0;

        // calculate hypothetical number of expected deaths
        int hypotheticalDeaths = calculateNumberToDie(currentHypotheticalPopulationSize, config.getSetUpDR() * initTimeStep.toDecimalRepresentation());

        // update hypothetical population
        currentHypotheticalPopulationSize = currentHypotheticalPopulationSize + hypotheticalBirths - hypotheticalDeaths;

        if(shortFallInBirths >= 0) {
            // add Orphan Children to the population
            for (int i = 0; i < shortFallInBirths; i++) {
                EntityFactory.formOrphanChild(currentTime, InitLogic.getTimeStep(), population, ps);
            }
        } else {
            double removeN = Math.abs(shortFallInBirths) / 2.0;
            int removeMales;
            int removeFemales;

            if(removeN % 1 != 0) {
                removeMales = (int) Math.ceil(removeN);
                removeFemales = (int) Math.floor(removeN);
            } else {
                removeMales = (int) removeN;
                removeFemales = (int) removeN;
            }

            try {
                for (int i = 0; i < removeMales; i++) {
                    population.getLivingPeople().getMales().removeNPersons(removeMales, currentTime, initTimeStep, true);
                }

                for (int i = 0; i < removeFemales; i++) {
                    population.getLivingPeople().getFemales().removeNPersons(removeFemales, currentTime, initTimeStep, true);
                }

            } catch (InsufficientNumberOfPeopleException e) {
                // Never should happen
                throw new Error();
            }

        }

        return shortFallInBirths;

    }

    public static void incrementBirthCount(int n) {
        numberOfBirthsInThisTimestep += n;
    }

    public static boolean inInitPeriod(Date currentTime) {
        return DateUtils.dateBeforeOrEqual(currentTime, endOfInitPeriod);
    }

    public static CompoundTimeUnit getTimeStep() {
        return initTimeStep;
    }

    private static int calculateStartingPopulationSize(Config config) {
        // Performs compound growth in reverse to work backwards from the target population to the
        return (int) (config.getT0PopulationSize() / Math.pow(config.getSetUpBR() - config.getSetUpDR() + 1, DateUtils.differenceInYears(config.getTS(), config.getT0()).getCount()));
    }

    public static int calculateChildrenToBeBorn(int sizeOfCohort, Double birthRate) {
        return SharedLogic.calculateNumberToHaveEvent(sizeOfCohort, birthRate);
    }

    public static int calculateNumberToDie(int people, Double deathRate) {
        return SharedLogic.calculateNumberToHaveEvent(people, deathRate);
    }

}
