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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;
import uk.ac.standrews.cs.valipop.utils.ContingencyTableValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *  These tests check that a new population can be generated and statistically validated as expected.
 *
 * @author Graham Kirby
 */
public class ValidationEndToEndTest {

    public static final int MAX_ACCEPTABLE_VALIDATION_SCORE = 20;
    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/validation");

    private static final List<Arguments> endToEndConfigurations = List.of(
        Arguments.of("endToEnd/1855-1973-initial-10K.config")
    );

    @ParameterizedTest
    @FieldSource("endToEndConfigurations")
    @Tag("slow")
    public void endToEndValidation(final String configPath) throws IOException {

        final Config config = new Config(TEST_RESOURCE_DIR.resolve(configPath));

        final OBDModel model = new OBDModel(config);
        model.runSimulation();

        final int score = new ContingencyTableValidator(config).getValidationScore();

        assertTrue(score <= MAX_ACCEPTABLE_VALIDATION_SCORE);
    }
}
