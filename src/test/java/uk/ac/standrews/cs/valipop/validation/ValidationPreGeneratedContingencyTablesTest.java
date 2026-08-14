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
package uk.ac.standrews.cs.valipop.validation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.ContingencyTableValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.standrews.cs.valipop.Config.CONTINGENCY_TABLES_DIR_NAME;

/**
 *  These tests check that when statistical validation is run against various pre-generated contingency tables, then
 *  the validation scores are as expected.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 * @author Graham Kirby
 */
public class ValidationPreGeneratedContingencyTablesTest {

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/validation");

    private static final List<Arguments> configurations = List.of(
        Arguments.of("case1/case1.config")
    );

    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of("case2/case2.config"),
        Arguments.of("case3/case3.config"),
        Arguments.of("case4/case4.config"),
        Arguments.of("case5/case5.config"),
        Arguments.of("case6/case6.config"),
        Arguments.of("case7/case7.config"),
        Arguments.of("case8/case8.config")
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void validationProducesExpectedScore(final String configPath) throws IOException {

        checkValidation(configPath);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void validationProducesExpectedScoreSlow(final String configPath) throws IOException {

        checkValidation(configPath);
    }

    private static void checkValidation(final String configPath) throws IOException {

        final Config config = new Config(TEST_RESOURCE_DIR.resolve(configPath));
        final double expectedScore = Double.parseDouble(config.get("expected_validation_score"));

        try (final Stream<Path> paths = Files.list(TEST_RESOURCE_DIR.resolve(configPath).getParent().resolve(CONTINGENCY_TABLES_DIR_NAME)).sorted()) {

            for (final Path contingency_table_path : paths.toList())
                Files.copy(contingency_table_path, config.getRunPath().resolve(CONTINGENCY_TABLES_DIR_NAME).resolve(contingency_table_path.getFileName()));
        }

        final ContingencyTableValidator validator = new ContingencyTableValidator(config);
        validator.validate();
        final int score = validator.getValidationScore();

        assertEquals(expectedScore, score);
    }
}
