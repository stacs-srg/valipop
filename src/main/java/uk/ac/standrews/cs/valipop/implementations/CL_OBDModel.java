package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.RCaller;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CL_OBDModel {

    public static void main(String[] args) throws IOException, StatsException {
        // Expects 3 args: path to config file, results path, run purpose

        String[] pArgs = ProcessArgs.process(args, "STANDARD");

        if (ProcessArgs.check(pArgs, "STANDARD")) {

            String pathToConfigFile = pArgs[0];
            String resultsPath = pArgs[1];
            String runPurpose = pArgs[2];

            runOBDModel(pathToConfigFile, resultsPath, runPurpose);

        } else {
            System.err.println("Incorrect arguments given");
        }
    }

    public static OBDModel runOBDModel(String pathToConfigFile, String resultsPath, String runPurpose) throws IOException, PreEmptiveOutOfMemoryWarning, StatsException {

        String startTime = FileUtils.getDateTime();

        OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, resultsPath);
        Config config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);

        OBDModel model = new OBDModel(startTime, config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        if (config.getOutputTables()) {
            performAnalysis(model);
        }

        model.getSummaryRow().outputSummaryRowToFile();

        return model;
    }

    private static void performAnalysis(OBDModel model) throws IOException, StatsException {

        ProgramTimer statsTimer = new ProgramTimer();

        String run_path_string = FileUtils.getRunPath().toString();

        int value = model.getDesiredPopulationStatistics().getOrderedBirthRates(new YearDate(0)).getLargestLabel().getValue();

        double v = RCaller.getGeeglmV("geeglm", run_path_string, run_path_string, value, model.getSummaryRow().getStartTime());

        model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());
        model.getSummaryRow().setV(v);
    }
}
