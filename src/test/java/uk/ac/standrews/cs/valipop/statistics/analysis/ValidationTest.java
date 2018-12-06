package uk.ac.standrews.cs.valipop.statistics.analysis;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.implementations.StatsException;
import uk.ac.standrews.cs.valipop.utils.RCaller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ValidationTest {

    @Ignore
    @Test
    public void test() throws IOException, StatsException {

        String runPurpose = "validation-testing";
        Path resultsPath = Paths.get("src/test/resources/results/validation-test/");
        Path pathToConfigFile = Paths.get("src/test/resources/valipop/validation-test-config.txt");

        Config config = new Config(pathToConfigFile).setRunPurpose(runPurpose).setResultsSavePath(resultsPath);

        OBDModel model = new OBDModel( config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        int value = model.getDesiredPopulationStatistics().getOrderedBirthRates(Year.of(0)).getLargestLabel().getValue();

        double v = RCaller.getGeeglmV("geeglm", config.getRunPath(), value, config.getStartTime());

        assertEquals(v, 0.0, 1e-10);
    }
}
