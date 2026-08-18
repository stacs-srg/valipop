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
package uk.ac.standrews.cs.valipop.conversion.population;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.ac.standrews.cs.valipop.Config.POPULATION_EXPORT_DIR_NAME;
import static uk.ac.standrews.cs.valipop.population.OBDModel.EXPORT_CHARSET;
import static uk.ac.standrews.cs.valipop.population.PopulationPropertiesTest.makePopulation;

/**
 * These tests check that when various populations are generated, and exported in various formats, then the files contain the expected content.
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 * @author Graham Kirby
 */
public class PopulationExportTest {

    public static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/conversion/population/export");

    // Graphviz files can be checked for validity at: https://magjac.com/graphviz-visual-editor/
    // GeoJSON files can be checked for validity at: https://geojsonlint.com

    private static final List<Arguments> configurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-200-graphviz.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-300-graphviz.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-200-geojson.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-300-geojson.config"))
    );

    private static final List<Arguments> slowConfigurations = List.of(
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-1K-graphviz.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-5K-graphviz.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-1K-geojson.config")),
        makePopulation(TEST_RESOURCE_DIR.resolve("1855-2016-initial-5K-geojson.config"))
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void populationExportedAsExpected(final IPersonCollection population) throws IOException, NoSuchAlgorithmException {

        checkPopulationExportedAsExpected(population);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void populationExportedAsExpectedSlow(final IPersonCollection population) throws IOException, NoSuchAlgorithmException {

        checkPopulationExportedAsExpected(population);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void checkPopulationExportedAsExpected(final IPersonCollection population) throws IOException, NoSuchAlgorithmException {

        final Config config = population.getConfig();

        final String hashAlgorithmName = config.get("hash_algorithm_name");
        final String expectedHash = config.get("expected_hash");

        final Path populationExportDirPath = config.getRunPath().resolve(POPULATION_EXPORT_DIR_NAME);
        final String populationExportFileName = OBDModel.POPULATION_EXPORT_FILENAME + "." + config.getPopulationExportFormat().getFileSuffix();

        checkPopulationExportedAsExpected(populationExportDirPath.resolve(populationExportFileName), hashAlgorithmName, expectedHash);
    }

    private static void checkPopulationExportedAsExpected(final Path exportFilePath, final String hashAlgorithmName, final String expectedHash) throws IOException, NoSuchAlgorithmException {

        // Read line by line rather than reading all bytes directly, to give consistent newline encoding on all platforms.
        final byte[] bytes = String.join("\n", Files.readAllLines(exportFilePath, EXPORT_CHARSET)).getBytes(EXPORT_CHARSET);

        final String actualHash = Base64.getEncoder().encodeToString(MessageDigest.getInstance(hashAlgorithmName).digest(bytes));

        assertEquals(expectedHash, actualHash, "Checking exported population from " + exportFilePath);
    }

    protected static void assertThatFilesHaveSameContent(final Path path1, final Path path2) throws IOException {

        try (final BufferedReader reader1 = Files.newBufferedReader(path1, EXPORT_CHARSET); final BufferedReader reader2 = Files.newBufferedReader(path2, EXPORT_CHARSET)) {

            String line1;

            while ((line1 = reader1.readLine()) != null) {
                final String line2 = reader2.readLine();
                assertEquals(line1, line2);
            }

            assertNull(reader2.readLine());
        }
    }
}
