/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.simulation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;

/**
 * E2E tests which compares ValiPop generated record checksums 
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 */
@RunWith(Parameterized.class)
public class SimulationTest {
    private Path configPath;
    private String expectedBirthHash;
    private String expectedDeathHash;
    private String expectedMarriageHash;

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/config/simulation");
    private static final String RECORD_DIR = "records";

    private static final String[] RECORD_NAMES = new String[] {
        "birth_records.csv",
        "death_records.csv",
        "marriage_records.csv",
    };

    public SimulationTest(Path configPath, String expectedBirthHash, String expectedDeathHash, String expectedMarriageHash) {
        this.configPath = configPath;
        this.expectedBirthHash = expectedBirthHash;
        this.expectedDeathHash = expectedDeathHash;
        this.expectedMarriageHash = expectedMarriageHash;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestCases() {
        return Arrays
            .asList(new Object[][] {
                new Object[] { TEST_RESOURCE_DIR.resolve("config-1.txt"), "+wEc08pgIweIC4advvfcIw==", "DWj48GAsFtJS/PX4c+59dA==", "D0jUpvTWOpCVGYPRbc4dQw==" },
                new Object[] { TEST_RESOURCE_DIR.resolve("config-2.txt"), "mEkjBgtyHFMqTZz02r9H4w==", "fhE/MQV8AkzKMtaLcA7rQA==", "jjcDTaoYW4OizssyCtRnTA==" },
                new Object[] { TEST_RESOURCE_DIR.resolve("config-3.txt"), "VJw8XWYYWzyn3OCs5/wcgA==", "3nKYMFhMxJ1rLhfNYEv/zA==", "QKw8S3qci40qr/ofUNvoJg==" },
                new Object[] { TEST_RESOURCE_DIR.resolve("config-4.txt"), "FdGj7mPJh9jBk9LGZuhGCA==", "9wdMQtsoGhVGTDH/T49QpQ==", "FxXNt+HHl+xek5Qw6kozsA==" },
                new Object[] { TEST_RESOURCE_DIR.resolve("config-5.txt"), "tH6c5imlN3nv/vp0ZhmCVw==", "Owol4GIFt32ObEncvdfiRg==", "if9r3nzExsMTQE+PgF4FAw==" },
                new Object[] { TEST_RESOURCE_DIR.resolve("config-6.txt"), "U31QuVvgN6kKOzWlCyGrrw==", "O3PZzHiBHuK1asQ2zkjxZg==", "gKHm0hFySLLlHi4SCKOyIg==" }

                // Test requires too much memory, and may not be suitable for all test runners
                //new Object[] { TEST_RESOURCE_DIR.resolve("config-7.txt"), "GuD6akCQtgAa/ZoCziBvDg==", "Is89232rJo7izldr1nppOA==", "hcV9bi58JUkDaJl+Ioe3mw==" }
            });
    }

    @Test
    public void test() throws IOException, NoSuchAlgorithmException {
        System.out.println("Testing with " +  configPath.toString());

        Config config = new Config(configPath);
        OBDModel model = new OBDModel(config);
        model.runSimulation();
        model.analyseAndOutputPopulation(true, 5);

        // Calculate MD5 hash of output records and compare
        for (String record : RECORD_NAMES) {
            Path recordPath = config.getRunPath().resolve(RECORD_DIR).resolve(record);
            byte[] bytes = Files.readAllBytes(recordPath);

            String actualHash = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(bytes));

            switch (record) {
                case "birth_records.csv":
                    assertEquals("Comparing " + record + " of " + configPath.getFileName(), expectedBirthHash, actualHash);
                    break;
                case "death_records.csv":
                    assertEquals("Comparing " + record + " of " + configPath.getFileName(), expectedDeathHash, actualHash);
                    break;
                case "marriage_records.csv":
                    assertEquals("Comparing " + record + " of " + configPath.getFileName(), expectedMarriageHash, actualHash);
                    break;
            }
        }
    }
}
