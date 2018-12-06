package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.ProcessArgs;
import uk.ac.standrews.cs.valipop.utils.ProgramTimer;
import uk.ac.standrews.cs.valipop.utils.RCaller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CL_OBDModel {

    public static void main(String[] args) throws IOException, StatsException {

        String[] pArgs = ProcessArgs.process(args, "STANDARD");

        if (ProcessArgs.check(pArgs, "STANDARD")) {

            runOBDModel(Paths.get(pArgs[0]));

        } else {
            System.err.println("Incorrect arguments given");
        }
    }

    public static void runOBDModel(Path pathToConfigFile) throws IOException, PreEmptiveOutOfMemoryWarning, StatsException {

        Config config = new Config(pathToConfigFile);

        OBDModel model = new OBDModel( config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        if (config.getOutputTables()) {
            performAnalysis(model, config);
        }

        model.getSummaryRow().outputSummaryRowToFile();
    }

    private static void performAnalysis(OBDModel model, Config config) throws IOException, StatsException {

        ProgramTimer statsTimer = new ProgramTimer();

        int value = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        double v = RCaller.getGeeglmV("geeglm", config.getRunPath(), value, model.getSummaryRow().getStartTime());

        model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());
        model.getSummaryRow().setV(v);
    }
}
