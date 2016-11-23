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
import datastructure.summativeStatistics.PopulationComposition;
import model.simulationEntities.IPopulation;
import org.apache.logging.log4j.core.appender.FileAppender;
import utils.FileUtils;
import validation.ComparativeAnalysis;
import config.Config;

import utils.time.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.SummaryRow;
import validation.utils.StatisticalManipulationCalculationError;


import java.io.*;
import java.nio.file.*;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Simulation {

    private static Path PATH_TO_CONFIG_FILE;
    private static Config config;

    public static Logger log;

    PopulationStatistics desired;

    private PeopleCollection people;
    private PeopleCollection deadPeople;
    private DateClock currentTime;

    private SummaryRow summary;


    public Simulation(String pathToConfigFile, String runPurpose, String startTime, String resultsPath) throws IOException, UnsupportedDateConversion {

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        System.setProperty("logFilename", FileUtils.pathToLogDir(runPurpose, startTime, resultsPath).toString());
        log = LogManager.getLogger(Simulation.class);

        config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);

        currentTime = config.getTS();

        people = new PeopleCollection(config.getTS(), config.getTE());
        deadPeople = new PeopleCollection(config.getTS(), config.getTE());

        // get desired population info
        desired = setUpSimData();

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), runPurpose, startTime),
                startTime, runPurpose, config.getBirthTimeStep(), config.getDeathTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()));

    }

    public static void main(String[] args) {

        long runStartTime = System.nanoTime();

//        Logger log = LogManager.getLogger("main");
//        SimpleLayout layout = new SimpleLayout();
//        FileAppender appender = FileAppender.createAppender("your filename");
//        logger.addAppender(appender);

//        log.info("Program begins");

        Simulation sim = null;
        String runPurpose = null;
        String startTime = FileUtils.getDateTime();
        String resultsPath = null;
        String configPath = null;

        try {


            try {
                configPath = args[0];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("No config file given as 1st arg");
            }

            try {
                resultsPath = args[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("No results write path given as 2nd arg");
            }

            try {
                runPurpose = args[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                runPurpose = "unstated";
            }

            sim = new Simulation(configPath, runPurpose, startTime, resultsPath);

        } catch (IOException e) {
            System.err.println("Error creating directory structure - " + e.getMessage());
        } catch (UnsupportedDateConversion | InvalidPathException e) {
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

            File f = Paths.get(config.getResultsSavePath().toString(), runPurpose, startTime, "detailed-results-" + startTime + ".txt").toFile();
            resultsOutput = new PrintStream(f);

        } catch (IOException e) {
            log.info("Failed to set up summary results output stream - will output to standard out instead");
            resultsOutput = System.out;
        }

        // perform comparisons
        ComparativeAnalysis comparisonOfDesiredAndGenerated;
        try {

            comparisonOfDesiredAndGenerated = sim.analyseGeneratedPopulation(population, config);
            sim.summary = comparisonOfDesiredAndGenerated.outputResults(resultsOutput, sim.summary);

        } catch (IOException | StatisticalManipulationCalculationError | UnsupportedEventType | UnsupportedDateConversion e) {
            e.printStackTrace();
        }


//        sim.desired.getOrderedBirthRates(config.getT0()).outputResults(resultsOutput);



        try {

            runAnalytics(population, resultsOutput);
            sim.summary.setTotalPop(population.getNumberOfPeople());

        } catch (Exception e) {
            log.info("Analytics run failed");
            e.printStackTrace();
        }

        long runEndTime = System.nanoTime();
        double runTime = (runEndTime - runStartTime) / Math.pow(10, 9);
        int minutes = (int) (runTime / 60);
        int seconds = (int) (runTime % 60);
        String rT = minutes + ":" + seconds;

        sim.summary.setRunTime(rT);

        try {
            Files.write(Paths.get(config.getResultsSavePath().toString(), "global-results-summary.csv"),
                    sim.summary.toSeperatedString(',').getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.write(Paths.get(config.getResultsSavePath().toString(), runPurpose, runPurpose + "-results-summary.csv"),
                    sim.summary.toSeperatedString(',').getBytes(),
                    StandardOpenOption.APPEND);
//            File purpose = Paths.get(config.getResultsSavePath().toString(), runPurpose, runPurpose + "-results-summary.csv").toFile();
//            PrintStream purposeStream = new PrintStream(purpose);
//            purposeStream.println(sim.summary.toSeperatedString(','));
//            purposeStream.close();
        } catch (IOException e) {
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

        boolean inSimDates = false;
        int maxPop = 0;

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

                int currentPop = people.getNumberOfPersons();
                currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

                if(inSimDates && (currentPop > maxPop)) {
                    maxPop = currentPop;
                }

                if(!inSimDates && DateUtils.dateBefore(config.getT0(), currentTime)) {
                    inSimDates = true;
                    summary.setStartPop(currentPop);
                }

                log.info("Time step completed " + currentTime.toString() + "    Population " + people.getNumberOfPersons());
//                System.out.println("Time step completed " + currentTime.toString() + "    Population " + people.getNumberOfPersons());

            }

            summary.setPeakPop(maxPop);
            summary.setEndPop(people.getNumberOfPersons());

        } catch (InsufficientNumberOfPeopleException e) {
            log.fatal(e.getMessage());
        }

        return AggregatePersonCollectionFactory.makePeopleCollection(people, deadPeople);
    }
}
