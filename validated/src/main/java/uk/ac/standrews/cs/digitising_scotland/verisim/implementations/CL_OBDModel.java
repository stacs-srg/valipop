package uk.ac.standrews.cs.digitising_scotland.verisim.implementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProcessArgs;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;

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
        } catch (InvalidInputFileException | IOException | Error e) {
            OBDModel.log.error(e.getMessage());
        }

    }

    public static OBDModel runOBDModel(String pathToConfigFile, String resultsPath, String runPurpose) throws Error, InvalidInputFileException, IOException {
        String startTime = FileUtils.getDateTime();

        Config config;
        try {
            OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, resultsPath);
            config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);
        } catch (IOException e) {
            String message = "Error in pre-initilisation phase - see logs";
            throw new Error(message);
        }

        try {
            OBDModel model = new OBDModel(startTime, config);
            model.runSimulation();
            model.analyseAndOutputPopulation();
            return model;
        } catch (IOException e) {
            String message = "Model failed due to Input/Output exception, check that this program has " +
                    "permission to read or write on disk. Also, check supporting input files are present at location " +
                    "specified in config file : " + e.getMessage();
            throw new IOException(message, e);
        } catch (InvalidInputFileException e) {
            String message = "Model failed due to an invalid formatting/content of input file, see message: " + e.getMessage();
            throw new InvalidInputFileException(message, e);
        }
    }


}
