package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.RCaller;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CL_OBDModel {

    public static void main(String[] args) {
        // Expects 3 args: path to config file, results path, run purpose

        String[] pArgs = ProcessArgs.process(args, "STANDARD");
        if(!ProcessArgs.check(pArgs, "STANDARD")) {
            System.err.println("Incorrect arguments given");
            throw new Error("Incorrect arguments given");
        }

        String pathToConfigFile = pArgs[0];
        String resultsPath = pArgs[1];
        String runPurpose = pArgs[2];

        try {
            runOBDModel(pathToConfigFile, resultsPath, runPurpose);
        } catch (InvalidInputFileException | IOException | Error | PreEmptiveOutOfMemoryWarning | StatsException e) {
            System.err.println(e.getMessage());
        }
    }

    public static OBDModel runOBDModel(String pathToConfigFile, String resultsPath, String runPurpose) throws Error, InvalidInputFileException, IOException, PreEmptiveOutOfMemoryWarning, StatsException {
        String startTime = FileUtils.getDateTime();

        Config config;
        try {
            OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, resultsPath);
            config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);
        } catch (IOException e) {
            String message = "Error in pre-initialisation phase - see logs";
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new Error(message);
        }

        OBDModel model;
        try {
            model = new OBDModel(startTime, config);
            model.runSimulation();
            model.analyseAndOutputPopulation(false);

            if(config.getOutputTables()) {
                ProgramTimer statsTimer = new ProgramTimer();

                double v = RCaller.getGeeglmV(
                        FileUtils.getRunPath().toString(),
                        FileUtils.getRunPath().toString(),
                        FileUtils.getContingencyTablesPath().toString(),
                        model.getDesiredPopulationStatistics().getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue(), model.getSummaryRow().getStartTime());

                model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());
                model.getSummaryRow().setV(v);
            }

            model.getSummaryRow().outputSummaryRowToFile();

            return model;
        } catch (IOException e) {
            String message = "Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config file : " + e.getMessage();
            throw new IOException(message, e);
        } catch (InvalidInputFileException | InconsistentWeightException e) {
            String message = "Model failed due to an invalid formatting/content of input file, see message: " + e.getMessage();
            throw new InvalidInputFileException(message, e);
        }
    }
}
