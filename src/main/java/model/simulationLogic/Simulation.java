package model.simulationLogic;

import analytic.ChildrenAnalytics;
import analytic.DeathAnalytics;
import analytic.MarriageAnalytics;
import analytic.PopulationAnalytics;
import datastructure.population.AggregatePersonCollectionFactory;
import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.population.PeopleCollection;
import datastructure.summativeStatistics.desired.DesiredPopulationStatisticsFactory;
import datastructure.summativeStatistics.desired.PopulationStatistics;
import datastructure.summativeStatistics.generated.GeneratedPopulationComposition;
import datastructure.summativeStatistics.generated.UnsupportedEventType;
import model.*;
import datastructure.summativeStatistics.PopulationComposition;
import validation.ComparativeAnalysis;
import config.Config;

import utils.time.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    private static Path PATH_TO_CONFIG_FILE;
    private static Config config;

    public static Logger log = LogManager.getLogger(Simulation.class);

    PopulationStatistics desired;

    private PeopleCollection people;
    private PeopleCollection deadPeople;
    private DateClock currentTime;


    public Simulation(String pathToConfigFile) throws IOException, UnsupportedDateConversion, InvalidPathException {

        config = new Config(Paths.get(pathToConfigFile));

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

            sim = new Simulation(args[0]);

        } catch (IOException | UnsupportedDateConversion | InvalidPathException e) {
            log.fatal(e.getMessage() + " --- Will now exit");
            log.fatal(e.getStackTrace());
            e.printStackTrace();
            System.exit(2);
        }

        // run model

        PeopleCollection population = null;
        try {

            population = sim.makeSimulatedPopulation();

        } catch (UnsupportedDateConversion e1) {
            log.fatal(e1.getMessage() + " --- Will now exit");
            log.fatal(e1.getStackTrace());
        }

        PrintStream resultsOutput;

        try {

            File f = Paths.get("." + File.separator + config.getSavePathSummary() + File.separator + "summaryResults" + System.currentTimeMillis() + ".txt").toAbsolutePath().normalize().toFile();
            resultsOutput = new PrintStream(f);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            resultsOutput = System.out;
        }

        // perform comparisons
        ComparativeAnalysis comparisonOfDesiredAndGenerated;
        try {

            comparisonOfDesiredAndGenerated = sim.analyseGeneratedPopulation(population, config);
            comparisonOfDesiredAndGenerated.outputResults(resultsOutput);

        } catch (IOException | StatisticalManipulationCalculationError | UnsupportedEventType | UnsupportedDateConversion e) {
            e.printStackTrace();
        }


        sim.desired.getOrderedBirthRates(config.getT0()).outputResults(resultsOutput);


        try {

            runAnalytics(population, resultsOutput);

        } catch (Exception e) {
            log.info("Analytics run failed");
            e.printStackTrace();
        }

        resultsOutput.close();

    }

    private static void runAnalytics(IPopulation population, PrintStream resultsOutput) throws Exception {
        new PopulationAnalytics(population, resultsOutput).printAllAnalytics();
        new ChildrenAnalytics(population, resultsOutput).printAllAnalytics();
        new DeathAnalytics(population, resultsOutput).printAllAnalytics();
        new MarriageAnalytics(population, resultsOutput).printAllAnalytics();
    }

    private static PopulationStatistics setUpSimData() throws IOException {
        return DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);
    }


    public ComparativeAnalysis analyseGeneratedPopulation(PeopleCollection generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException, UnsupportedEventType {
        // get comparable statistics for generate population
        PopulationComposition generatedPopulationComposition = new GeneratedPopulationComposition(config.getT0(), config.getTE(), generatedPopulation);

        // compare desired and generated population
        ComparativeAnalysis comparisonOfDesiredAndGenerated = new ComparativeAnalysis(desired, generatedPopulationComposition, config.getT0(), config.getTE());

        comparisonOfDesiredAndGenerated.runAnalysis(generatedPopulation, config);


        return comparisonOfDesiredAndGenerated;

    }

    private PeopleCollection makeSimulatedPopulation() throws UnsupportedDateConversion {

        // INFO: at this point all the desired population statistics have been made available
        log.info("Simulation begins");

        // start utils.time progression
        // for each utils.time step from T Start to T End
        try {

            while (DateUtils.dateBefore(currentTime, config.getTE())) {

                // at every min timestep
                // clear out dead people

                // if births timestep
                if (DateUtils.matchesInterval(currentTime, config.getBirthTimeStep())) {
                    int births = BirthLogic.handleBirths(config, currentTime, desired, people);
                    InitLogic.incrementBirthCount(births);
                }

                // if deaths timestep
                if (DateUtils.matchesInterval(currentTime, config.getDeathTimeStep())) {
                    DeathLogic.handleDeaths(config, currentTime, desired, people, deadPeople, config.getDeathTimeStep());
                }

                if (InitLogic.inInitPeriod(currentTime) && DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep())) {
                    InitLogic.handleInitPeople(config, currentTime, people);
                }

                currentTime = currentTime.advanceTime(config.getSimulationTimeStep());
                log.info("Time step completed " + currentTime.toString() + "    Population " + people.getNumberOfPersons());
                System.out.println("Time step completed " + currentTime.toString() + "    Population " + people.getNumberOfPersons());

            }

        } catch (InsufficientNumberOfPeopleException e) {
            log.fatal(e.getMessage());
        }

        return AggregatePersonCollectionFactory.makePeopleCollection(people, deadPeople);
    }
}
