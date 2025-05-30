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
 * Command line entry point to simulate a population model and analysis.
 * 
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
        System.out.println("Running simulation with " + pathToConfigFile.toAbsolutePath());

        OBDModel model = new OBDModel( config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false, 5);

        if (config.getOutputTables()) {
            performAnalysis(model, config);
        }

        model.getSummaryRow().outputSummaryRowToFile();
    }

    private static void performAnalysis(OBDModel model, Config config) throws IOException, StatsException {

        ProgramTimer statsTimer = new ProgramTimer();

        int value = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        double v = RCaller.getGeeglmV(config.getRunPath(), value);

        // This gives a human readable score
        String score = "good";
        if (v > 0 && v <= 10) {
            score = "okay";
        } else if (v > 30) {
            score = "bad";
        }

        System.out.println("Validation score: " + v + " (" + score + ")");

        model.getSummaryRow().setStatsRunTime(statsTimer.getRunTimeSeconds());
        model.getSummaryRow().setV(v);
    }
}
