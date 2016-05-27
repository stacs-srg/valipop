package model.simulationLogic;

import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.DesiredPopulationStatisticsFactory;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import model.*;
import datastructure.summativeStatistics.PopulationComposition;
import validation.ComparativeAnalysis;
import datastructure.summativeStatistics.generated.GeneratedPopulationCompositionFactory;
import config.Config;

import utils.time.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    private final static Path PATH_TO_CONFIG_FILE = Paths.get("/Users/tsd4/OneDrive/cs/PhD/population_model/src/main/resources/config/config.txt");
    private final static Config config = new Config(PATH_TO_CONFIG_FILE);

    public static Logger log = LogManager.getLogger(Simulation.class);

    PopulationStatistics desired;

    private PeopleCollection people;
    private PeopleCollection deadPeople;
    private DateClock currentTime;


    public Simulation() throws IOException {
        currentTime = config.getTS();

        people = new PeopleCollection(config.getTS(), config.getTE());
        deadPeople = new PeopleCollection(config.getTS(), config.getTE());

        // get desired population info
        desired = setUpSimData();

        InitLogic.setUpInitParameters(config, desired);

    }

    public static void main(String[] args) {

        Logger log = LogManager.getLogger("main");

        log.info("Program begins");

        Simulation sim = null;
        try {
            sim = new Simulation();
        } catch (IOException e) {
            log.fatal(e.getMessage() + " --- Will now exit");
            log.fatal(e.getStackTrace());
            e.printStackTrace();
            System.exit(2);
        }

        // run model
        IPopulation population = sim.makeSimulatedPopulation();

        // perform comparisons
        ComparativeAnalysis comparisonOfDesiredAndGenerated = sim.analyseGeneratedPopulation(population);

        // Check for statistical significant similarity between desired and generated population
        if (comparisonOfDesiredAndGenerated.passed()) {
            System.out.println("Generated population similarity to desired population is statistically significant");
        }


    }

    private static PopulationStatistics setUpSimData() throws IOException {
        return DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);
    }



    public ComparativeAnalysis analyseGeneratedPopulation(IPopulation generatedPopulation) {
        // get comparable statistics for generate population
        PopulationComposition generatedPopulationComposition = GeneratedPopulationCompositionFactory.createGeneratedPopulationComposition(generatedPopulation);

        // compare desired and generated population
        ComparativeAnalysis comparisonOfDesiredAndGenerated = new ComparativeAnalysis(desired, generatedPopulationComposition);

        return comparisonOfDesiredAndGenerated;

    }

    private IPopulation makeSimulatedPopulation() {

        // INFO: at this point all the desired population statistics have been made available
        log.info("Simulation begins");

        // start utils.time progression
        // for each utils.time step from T Start to T End
        while (DateUtils.dateBefore(currentTime, config.getTE())) {

            // at every min timestep
            // clear out dead people

            // if deaths timestep
            if (DateUtils.matchesInterval(currentTime, config.getDeathTimeStep())) {
                DeathLogic.handleDeaths(config, currentTime, desired, people, deadPeople);
            }

            // if births timestep
            if (DateUtils.matchesInterval(currentTime, config.getBirthTimeStep())) {
                int births = BirthLogic.handleBirths(config, currentTime, desired, people);
                InitLogic.incrementBirthCount(births);
            }

            if (InitLogic.inInitPeriod(currentTime) && DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep())) {
                InitLogic.handleInitPeople(config, currentTime, people);
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());
            log.info("Time step completed " + currentTime.toString() + "    Population " + people.getNumberOfPersons());

        }

        return people;
    }









}
