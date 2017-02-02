package utils.implementedSimulations.orderedBirthDeathModel;

import config.Config;
import dateModel.DateUtils;
import dateModel.dateImplementations.ExactDate;
import dateModel.dateImplementations.MonthDate;
import events.UnsupportedEventType;
import events.birth.BirthLogic;
import events.death.DeathLogic;
import events.init.InitLogic;
import org.apache.logging.log4j.Logger;
import populationStatistics.recording.PopulationStatistics;
import populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import populationStatistics.validation.analytic.AnalyticsRunner;
import populationStatistics.validation.comparison.ComparativeAnalysis;
import populationStatistics.validation.summaryData.SummaryRow;
import simulationEntities.person.Person;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;
import utils.CustomLog4j;
import utils.ProcessArgs;
import utils.ProgramTimer;
import utils.fileUtils.FileUtils;
import utils.fileUtils.InvalidInputFileException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    public static Logger log;

    private static Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;

    private static PopulationCounts pc;
    private PeopleCollection population;
    private PeopleCollection deadPopulation;
    private MonthDate currentTime;


    public static void main(String[] args) {

        String[] pArgs = ProcessArgs.process(args);
        if(!ProcessArgs.check(pArgs)) {
            System.err.println("Incorrect arguments given");
            System.exit(1);
        }

        ProgramTimer timer = new ProgramTimer();

        OBDModel sim = null;

        try {
            sim = new OBDModel(pArgs[0], pArgs[2], FileUtils.getDateTime(), pArgs[1]);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config file");
            System.exit(1);
        } catch (InvalidInputFileException e) {
            System.err.println("Model failed due to an invalid formatting/content of input file, see message: ");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        PeopleCollection population = null;
        try {
            population = sim.runSimulation();
        } catch (InsufficientNumberOfPeopleException e) {
            System.err.println("Simulation run incomplete due to insufficient number of people in population to " +
                    "perform requested events");
            System.err.println(e.getMessage());
        }

        PrintStream resultsOutput;
        try {
            resultsOutput = new PrintStream(FileUtils.getDetailedResultsPath().toFile());
        } catch (FileNotFoundException e) {
            throw new Error("Detailed Results File has been moved since it's creation at the beginning of " +
                    "model run", e);
        }

        ComparativeAnalysis comparisonOfDesiredAndGenerated;
        try {
            comparisonOfDesiredAndGenerated = ComparativeAnalysis.performComparison(config, population, sim.desired);
            sim.summary = comparisonOfDesiredAndGenerated.outputResults(resultsOutput, sim.summary);
        } catch (UnsupportedEventType | IOException e) {
            System.err.println("Comparative analysis failed");
            System.err.println(e.getMessage());
        }

        AnalyticsRunner.runAnalytics(population, resultsOutput);

        sim.summary.setTotalPop(population.getNumberOfPeople());
        sim.summary.setRunTime(timer.getTimeMMSS());

        try {
            FileUtils.writeSummaryRowToSummaryFiles(sim.summary);
        } catch (IOException e) {
            System.err.println("Summary row could not be printed to summary files. See message: ");
            System.err.println(e.getMessage());
        }

        resultsOutput.close();


    }


    public OBDModel(String pathToConfigFile, String runPurpose, String startTime, String resultsPath) throws IOException, InvalidInputFileException {

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        log = CustomLog4j.setup(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath), this);
        config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);


        // Set up simulation parameters
        currentTime = config.getTS();
        pc = new PopulationCounts();
        population = new PeopleCollection(config.getTS(), config.getTE());
        deadPopulation = new PeopleCollection(config.getTS(), config.getTE());

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), runPurpose, startTime),
                startTime, runPurpose, config.getBirthTimeStep(), config.getDeathTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()));

    }

    public PeopleCollection runSimulation() throws InsufficientNumberOfPeopleException {


        while(DateUtils.dateBefore(currentTime, config.getTE())) {

            if (DateUtils.matchesInterval(currentTime, config.getBirthTimeStep())) {
                int births = BirthLogic.handleBirths(config, currentTime, desired, population, pc);
                InitLogic.incrementBirthCount(births);
            }

            // if deaths timestep
            if (DateUtils.matchesInterval(currentTime, config.getDeathTimeStep())) {
                DeathLogic.handleDeaths(config, currentTime, desired, population, deadPopulation, config.getDeathTimeStep());
            }



            if (InitLogic.inInitPeriod(currentTime) && DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep())) {
                InitLogic.handleInitPeople(config, currentTime, population, pc);
            }



            if(inSimDates()) {
                pc.updateMaxPopulation(population.getNumberOfPeople());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

            log.info("Time step completed " + currentTime.toString() + "    Population " + population.getNumberOfPersons());
            System.out.println("Time step completed " + currentTime.toString() + "    Population " + population.getNumberOfPersons());

        }



        return AggregatePersonCollectionFactory.makePeopleCollection(population, deadPopulation);
    }

    private boolean inSimDates() {
        return DateUtils.dateBefore(config.getT0(), currentTime);
    }

}
