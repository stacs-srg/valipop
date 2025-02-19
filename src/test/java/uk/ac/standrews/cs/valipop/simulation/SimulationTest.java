package uk.ac.standrews.cs.valipop.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;

import static uk.ac.standrews.cs.utilities.FileManipulation.FILE_CHARSET;

@RunWith(Parameterized.class)
public class SimulationTest {
    private Path tableDirectory;
    private Path configFile;

    private static Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/validation");
    private static String TABLES_DIR_NAME = "tables";
    private static String CONFIG_NAME = "config.txt";

    private static String[] TABLE_NAMES = new String[] {
        "death-CT.csv",
        "mb-CT.csv",
        "ob-CT.csv",
        "part-CT.csv",
        "sep-CT.csv"
    };

    public SimulationTest(Path tableDirectory, Path configFile) {
        this.tableDirectory = tableDirectory;
        this.configFile = configFile;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestCases() {
        // Creates two paths, [<test_dir>/<RECORDS_DIR_NAME>, <test_dir>/<CONFIG_NAME>]
        return Arrays
            .asList(new Path[] {
                TEST_RESOURCE_DIR.resolve("test1"),
                TEST_RESOURCE_DIR.resolve("test2"),
                TEST_RESOURCE_DIR.resolve("test3"),
                TEST_RESOURCE_DIR.resolve("test4"),
                TEST_RESOURCE_DIR.resolve("test5"),
                TEST_RESOURCE_DIR.resolve("test6"),
                TEST_RESOURCE_DIR.resolve("test7"),
                TEST_RESOURCE_DIR.resolve("test8")
            })
            .stream()
            .map((path) -> new Object[] {
                path.resolve(TABLES_DIR_NAME),
                path.resolve(CONFIG_NAME)
            })
            .toList();
    }

    @Test
    public void test() throws IOException {
        System.out.println("Testing with " + tableDirectory + ", " + configFile);

        Config config = new Config(configFile);
        OBDModel model = new OBDModel(config);
        model.runSimulation();
        model.analyseAndOutputPopulation(false, 5);

        // Ensure the actual tables are the same as the expected
        for (String tableName : TABLE_NAMES) {
            Path expectedPath = tableDirectory.resolve(tableName);
            Path actualPath = config.getRunPath().resolve(TABLES_DIR_NAME).resolve(tableName);

            assertThatFilesHaveSameContent(expectedPath, actualPath);
        }
        System.out.println(Thread.currentThread().getName());
    }

    private static void assertThatFilesHaveSameContent(final Path path1, final Path path2) throws IOException {

        try (BufferedReader reader1 = Files.newBufferedReader(path1, FILE_CHARSET); BufferedReader reader2 = Files.newBufferedReader(path2, FILE_CHARSET)) {

            String line1;

            while ((line1 = reader1.readLine()) != null) {
                String line2 = reader2.readLine();
                assertEquals(line1, line2);
            }

            assertNull(reader2.readLine());
        }
    }
}
