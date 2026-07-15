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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.ac.standrews.cs.valipop.Config.RECORDS_EXPORT_DIR_NAME;
import static uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordType.*;

/**
 * These tests check that when various populations are generated, and records exported, then the expected numbers of records are created with the expected content.
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 * @author Graham Kirby
 */
public class RecordExportTest {

    private static final String HASH_ALGORITHM_NAME = "MD5";
    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/export/records");

    private static final List<Arguments> configurations = List.of(
        Arguments.of("1855-1973-initial-10K.config"),
        Arguments.of("1855-1973-initial-10K-no-recovery.config"),
        Arguments.of("1850-1900-initial-100K.config")
    );

    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of("1850-2025-initial-100K.config")
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void recordsGeneratedAsExpected(final String configPath) throws IOException, NoSuchAlgorithmException {

        runTest(configPath);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void recordsGeneratedAsExpectedSlow(final String configPath) throws IOException, NoSuchAlgorithmException {

        runTest(configPath);
    }

    private static void runTest(final String configPath) throws IOException, NoSuchAlgorithmException {

        final Config config = new Config(TEST_RESOURCE_DIR.resolve(configPath));
        final OBDModel model = new OBDModel(config);

        model.runSimulation();
        model.analyseAndOutputPopulation(false);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        final String expectedBirthHash = config.get("expected_birth_records_hash");
        final String expectedDeathHash = config.get("expected_death_records_hash");
        final String expectedMarriageHash = config.get("expected_marriage_records_hash");

        final int expectedBirthRecordCount = Integer.parseInt(config.get("expected_birth_records_count"));
        final int expectedDeathRecordCount = Integer.parseInt(config.get("expected_death_records_count"));
        final int expectedMarriageRecordCount = Integer.parseInt(config.get("expected_marriage_records_count"));

        final Path recordsExportDirPath = config.getRunPath().resolve(RECORDS_EXPORT_DIR_NAME);

        check(recordsExportDirPath.resolve(BIRTH_RECORDS_FILENAME), expectedBirthHash, expectedBirthRecordCount);
        check(recordsExportDirPath.resolve(DEATH_RECORDS_FILENAME), expectedDeathHash, expectedDeathRecordCount);
        check(recordsExportDirPath.resolve(MARRIAGE_RECORDS_FILENAME), expectedMarriageHash, expectedMarriageRecordCount);
    }

    private static void check(final Path recordsFilePath, final String expectedHash, final int expectedRecordCount) throws IOException, NoSuchAlgorithmException {

        final List<String> lines = Files.readAllLines(recordsFilePath);
        assertEquals(expectedRecordCount, lines.size());

        final byte[] bytes = Files.readAllBytes(recordsFilePath);
        final String actualHash = Base64.getEncoder().encodeToString(MessageDigest.getInstance(HASH_ALGORITHM_NAME).digest(bytes));

        assertEquals(expectedHash, actualHash, "Checking records from " + recordsFilePath);
    }
}
