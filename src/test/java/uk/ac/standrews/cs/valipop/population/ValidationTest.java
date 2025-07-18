/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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
package uk.ac.standrews.cs.valipop.population;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.utils.ContingencyTableValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 */
public class ValidationTest {

    // TODO add tests that generate contingency tables on the fly.

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/validation");
    public static final double DELTA = 1e-10;

    @SuppressWarnings("unused")
    private static final List<Arguments> configurations = List.of(
        Arguments.of(TEST_RESOURCE_DIR.resolve("test4"), 59.0)
    );

    @SuppressWarnings("unused")
    private static final List<Arguments> slowConfigurations = List.of(
        Arguments.of(TEST_RESOURCE_DIR.resolve("test1"), 3.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test2"), 0.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test3"), 64.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test5"), 25.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test6"), 0.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test7"), 65.0),
        Arguments.of(TEST_RESOURCE_DIR.resolve("test8"), 14.0)
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void runFastValidation(final Path workingDirectory, final double expectedScore) throws IOException {

        runValidation(workingDirectory, expectedScore);
    }

    @ParameterizedTest
    @FieldSource("slowConfigurations")
    @Tag("slow")
    public void runSlowValidation(final Path workingDirectory, final double expectedScore) throws IOException {

        runValidation(workingDirectory, expectedScore);
    }

    private static void runValidation(final Path workingDirectory, final double expectedScore) throws IOException {

        final Config config = new Config(workingDirectory.resolve("config.txt"));
        final int startYear = config.getT0().getYear();
        final int endYear = config.getTE().getYear();

        final double score = new ContingencyTableValidator(workingDirectory, 55, startYear, endYear).getValidationScore();

        assertEquals(expectedScore, score, DELTA);
    }
}
