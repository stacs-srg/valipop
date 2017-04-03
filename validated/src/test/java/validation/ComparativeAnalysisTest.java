package validation;

import config.Config;
import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import utils.FileUtils;
import validation.utils.StatisticalManipulationCalculationError;
import org.junit.Assert;
import org.junit.Test;
import utils.InputFileReader;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ComparativeAnalysisTest {

    @Test
    public void testKaplanMeierOnKnownData() throws StatisticalManipulationCalculationError, IOException {

        Config config = new Config(Paths.get("./src/test/resources/validation/config.txt"), "junit", FileUtils.getDateTime());

        OneDimensionDataDistribution expected = InputFileReader.readIn1DDataFile(Paths.get("./src/test/resources/validation_test_data/test_expected_data.txt"));
        OneDimensionDataDistribution observed = InputFileReader.readIn1DDataFile(Paths.get("./src/test/resources/validation_test_data/test_observed_data.txt"));

        double p = ComparativeAnalysis.runKaplanMeier(EventType.MALE_DEATH, expected, observed, config).getPValue();

        Assert.assertTrue(0.76 < p || p < 0.77);

    }

}
