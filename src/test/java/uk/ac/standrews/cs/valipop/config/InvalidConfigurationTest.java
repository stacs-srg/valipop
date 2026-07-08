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
package uk.ac.standrews.cs.valipop.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import uk.ac.standrews.cs.valipop.Config;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests various erroneous configs to check they are handled correctly
 *
 * @author Daniel Brathagen (dbrathagen@gmail.com)
 */
public class InvalidConfigurationTest {

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/config/error");

    private static List<Arguments> configurations = List.of(
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-1.txt"), "simulation_start" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-2.txt"), "simulation_end" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-3.txt"), "initialisation_start" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-4.txt"), "target_initial_population_size" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-5.txt"), "target_initial_population_size" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-6.txt"), "simulation_time_step" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-7.txt"), "recovery_factor" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-8.txt"), "over_sized_geography_factor" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-9.txt"), "record_export_format" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-10.txt"), "population_export_format" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-11.txt"), "initialisation_start" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-12.txt"), "simulation_start" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-13.txt"), "simulation_end"),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-14.txt"), "target_initial_population_size" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-15.txt"), "input_distributions_path" ),
        Arguments.of(TEST_RESOURCE_DIR.resolve("config-16.txt"), "Illegal line" ));

    @ParameterizedTest
    @FieldSource("configurations")
    public void shouldThrowIllegalArgument
        (final Path configPath, final String errorOption) {

        final Exception e = assertThrows(IllegalArgumentException.class, () -> new Config(configPath));

        assertTrue(e.getMessage().contains(errorOption));
    }
}
