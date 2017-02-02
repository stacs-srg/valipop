package utils.implementedSimulations.orderedBirthDeathModel;

import config.Config;
import dateModel.DateUtils;
import dateModel.dateImplementations.ExactDate;
import dateModel.dateImplementations.MonthDate;
import dateModel.exceptions.UnsupportedDateConversion;
import events.UnsupportedEventType;
import events.init.InitLogic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import populationStatistics.recording.PopulationStatistics;
import populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import populationStatistics.validation.comparison.ComparativeAnalysis;
import populationStatistics.validation.exceptions.StatisticalManipulationCalculationError;
import populationStatistics.validation.summaryData.SummaryRow;
import simulationEntities.EntityFactory;
import simulationEntities.person.Person;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.PeopleCollection;
import utils.CustomLog4j;
import utils.ProcessArgs;
import utils.fileUtils.FileUtils;

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
    private PeopleCollection people;
    private PeopleCollection deadPeople;
    private MonthDate currentTime;


    public static void main(String[] args) {

        String[] pArgs = ProcessArgs.process(args);
        if(!ProcessArgs.check(pArgs)) {
            System.err.println("Incorrect arguments given");
            System.exit(1);
        }

        OBDModel sim = null;

        try {
            sim = new OBDModel(pArgs[0], pArgs[2], FileUtils.getDateTime(), pArgs[1]);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config file");
            System.exit(1);
        }

        PeopleCollection population = new PeopleCollection(config.getTS(), config.getTE());
        population.addPerson(new Person('m', new ExactDate(1,1, config.getTS().getYear())));
        population.addPerson(new Person('f', new ExactDate(1,1, config.getTE().getYear())));

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
        } catch (UnsupportedEventType | StatisticalManipulationCalculationError | IOException e) {
            System.err.println("Comparitive analysis failed");
            System.err.println(e.getMessage());
        }


    }


    public OBDModel(String pathToConfigFile, String runPurpose, String startTime, String resultsPath) throws IOException {

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);

        log = CustomLog4j.setup(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath), this);

        config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);


        // Set up simulation parameters
        currentTime = config.getTS();

        pc = new PopulationCounts();
        people = new PeopleCollection(config.getTS(), config.getTE());
        deadPeople = new PeopleCollection(config.getTS(), config.getTE());

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), runPurpose, startTime),
                startTime, runPurpose, config.getBirthTimeStep(), config.getDeathTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()));

    }

}
