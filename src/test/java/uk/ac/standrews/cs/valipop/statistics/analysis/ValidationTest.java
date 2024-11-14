package uk.ac.standrews.cs.valipop.statistics.analysis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import uk.ac.standrews.cs.valipop.implementations.StatsException;
import uk.ac.standrews.cs.valipop.utils.RCaller;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
@RunWith(Parameterized.class)
public class ValidationTest {
    private Path tableDirectory;
    double expectedV;

    public ValidationTest(Path tableDirectory, double expectedV) {
        this.tableDirectory = tableDirectory;
        this.expectedV = expectedV;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> tables() {
        Path baseDir = Path.of("src/test/resources/valipop/validation");

        return Arrays.asList(new Object[][] {
            { baseDir.resolve("test1"), 0.0 },
            { baseDir.resolve("test2"), 17.0 },
            { baseDir.resolve("test3"), 0.0 },
            { baseDir.resolve("test4"), 0.0 },
            { baseDir.resolve("test5"), 0.0 },
            { baseDir.resolve("test6"), 61.0 },
        });
    }

    // Given model results, the R program should always generate the same V vlaue
    @Test
    public void test() throws IOException, StatsException {
        int maxBirthingAge = 55;
        LocalDateTime time = LocalDateTime.now();

        double v = RCaller.getGeeglmV(
            "geeglm",
            tableDirectory,
            maxBirthingAge,
            time
        );

        assertEquals(v, expectedV, 1e-10);
    }
}
