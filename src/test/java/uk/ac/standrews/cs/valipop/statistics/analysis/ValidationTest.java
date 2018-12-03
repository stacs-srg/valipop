package uk.ac.standrews.cs.valipop.statistics.analysis;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.implementations.StatsException;
import uk.ac.standrews.cs.valipop.utils.RCaller;
import uk.ac.standrews.cs.valipop.utils.fileUtils.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Year;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValidationTest {

    @Test
    @Ignore
    public void test() throws IOException, StatsException {

        String runPurpose = "validation-testing";
        String resultsPath = "src/test/resources/results/general-structure-testing/";
        String pathToConfigFile = "src/main/resources/valipop/config/scot/config.txt";

        String startTime = FileUtils.getDateTime();

        OBDModel.setUpFileStructureAndLogs(runPurpose, startTime, resultsPath);
        Config config = new Config(Paths.get(pathToConfigFile), runPurpose, startTime);

        OBDModel model = new OBDModel(startTime, config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        String run_path_string = FileUtils.getRunPath().toString();

        int value = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        double v = RCaller.getGeeglmV("geeglm", run_path_string, run_path_string, value, model.getSummaryRow().getStartTime());

        assertEquals(v, 0.0, 1e-10);
    }
}
