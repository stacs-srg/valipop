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

    private static final Path TEST_RESOURCE_DIR = Path.of("src/test/resources/valipop/config/validation");

    private static final List<Arguments> configurations = List.of(
        Arguments.of("init-start-after-simulation-start.config", "simulation_start"),
        Arguments.of("simulation-start-after-end.config", "simulation_end"),
        Arguments.of("invalid-init-date.config", "initialisation_start"),
        Arguments.of("invalid-target-population-size.config", "target_initial_population_size"),
        Arguments.of("negative-target-population-size.config", "target_initial_population_size"),
        Arguments.of("invalid-time-step.config", "simulation_time_step"),
        Arguments.of("invalid-recovery-factor.config", "recovery_factor"),
        Arguments.of("invalid-geography-factor.config", "over_sized_geography_factor"),
        Arguments.of("invalid-record-export-format.config", "record_export_format"),
        Arguments.of("invalid-population-export-format.config", "population_export_format"),
        Arguments.of("missing-simulation-start.config", "simulation_start"),
        Arguments.of("missing-simulation-end.config", "simulation_end"),
        Arguments.of("invalid-config.config", "Illegal line")
    );

    @ParameterizedTest
    @FieldSource("configurations")
    public void shouldThrowIllegalArgument(final String configPath, final String errorMessage) {

        final Exception e = assertThrows(IllegalArgumentException.class, () -> new Config(TEST_RESOURCE_DIR.resolve(configPath)));

        assertTrue(e.getMessage().contains(errorMessage));
    }
}
