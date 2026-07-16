/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.export;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.ac.standrews.cs.valipop.Config.POPULATION_EXPORT_DIR_NAME;

/**
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 * @author Graham Kirby
 */
public abstract class PopulationExportTest {

    // Files can be checked for validity at: https://geojsonlint.com

    public static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/export/population");

    static void checkPopulationExportedAsExpected(final String configPath) throws IOException, NoSuchAlgorithmException {

        final Config config = new Config(TEST_RESOURCE_DIR.resolve(configPath));
        final OBDModel model = new OBDModel(config);

        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        final String hashAlgorithmName = config.get("hash_algorithm_name");
        final String expectedHash = config.get("expected_hash");

        final Path populationExportDirPath = config.getRunPath().resolve(POPULATION_EXPORT_DIR_NAME);
        final String populationExportFileName = OBDModel.POPULATION_EXPORT_FILENAME + "." + config.getPopulationExportFormat().getFileSuffix();

        checkPopulationExportedAsExpected(populationExportDirPath.resolve(populationExportFileName), hashAlgorithmName, expectedHash);
    }

    private static void checkPopulationExportedAsExpected(final Path recordsFilePath, final String hashAlgorithmName, final String expectedHash) throws IOException, NoSuchAlgorithmException {

        final byte[] bytes = Files.readAllBytes(recordsFilePath);
        final String actualHash = Base64.getEncoder().encodeToString(MessageDigest.getInstance(hashAlgorithmName).digest(bytes));

        assertEquals(expectedHash, actualHash, "Checking exported population from " + recordsFilePath);
    }

    protected static void assertThatFilesHaveSameContent(final Path path1, final Path path2) throws IOException {

        try (final BufferedReader reader1 = Files.newBufferedReader(path1, StandardCharsets.UTF_8); final BufferedReader reader2 = Files.newBufferedReader(path2, StandardCharsets.UTF_8)) {

            String line1;

            while ((line1 = reader1.readLine()) != null) {
                final String line2 = reader2.readLine();
                assertEquals(line1, line2);
            }

            assertNull(reader2.readLine());
        }
    }
}
