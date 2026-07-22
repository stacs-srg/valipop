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

import org.junit.jupiter.params.provider.Arguments;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;
import uk.ac.standrews.cs.valipop.simulationEntities.IPersonCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public abstract class TestCases {

    public static final int T_S = 1599;
    public static final int T_0 = 1855;
    public static final int T_E = 2016;

    public static final int SEED_FOR_DETERMINISTIC_RUN = 841584;

    public static final Path DISTRIBUTIONS_PATH = Path.of("src/test/resources/valipop/distributions");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Arguments makeTestConfiguration(final int initialPopulationSize) {

        try {
            return Arguments.of(generatePopulation(initialPopulationSize));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static IPersonCollection generatePopulation(final int initialPopulationSize) throws IOException {

        final Path tempDir;
        try {
            tempDir = Files.createTempDirectory("valipopTests");
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final Config config = new Config(
            LocalDate.of(T_S, 1, 1),
            LocalDate.of(T_0, 1, 1),
            LocalDate.of(T_E, 1, 1),
            initialPopulationSize,
            DISTRIBUTIONS_PATH,
            tempDir,
            "testing",
            tempDir);

        config.setDeterministic(true).setSeed(SEED_FOR_DETERMINISTIC_RUN);

        final OBDModel model = new OBDModel(config);
        model.runSimulation();

        final IPersonCollection population = model.getPopulation().getPeople();
        population.setDescription("initial size=" + initialPopulationSize + ", seed=" + SEED_FOR_DETERMINISTIC_RUN);
        return population;
    }
}
