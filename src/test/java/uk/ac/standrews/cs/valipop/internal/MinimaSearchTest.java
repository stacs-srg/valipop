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
package uk.ac.standrews.cs.valipop.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.population.OBDModel;
import uk.ac.standrews.cs.valipop.population.SpaceExploredException;
import uk.ac.standrews.cs.valipop.population.minimaSearch.Control;
import uk.ac.standrews.cs.valipop.population.minimaSearch.MinimaSearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearchTest {

    OBDModel model;

    @BeforeEach
    public void setup() throws IOException {

        final Path tempDir;
        try {
            tempDir = Files.createTempDirectory("valipopTests");
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final Config config = new Config(
                LocalDate.of(1,1,1),
                LocalDate.of(200,1,1),
                LocalDate.of(300,1,1),
                0,
                Paths.get("src/test/resources/valipop/distributions"),
                tempDir, "MINIMA_SEARCH_TEST",
                tempDir);

        config.setDeterministic( true);

        model = new OBDModel(config);
    }

    @Test
    public void nanTesting() throws SpaceExploredException {

        final double startingFactor = 0.0;

        MinimaSearch.startFactor = startingFactor;
        MinimaSearch.step = 0.5;
        MinimaSearch.initStep = 0.5;

        final Control control = Control.RF;

        MinimaSearch.setControllingFactor(control, MinimaSearch.startFactor);
        double rf = MinimaSearch.getControllingFactor(control);

        assertEquals(startingFactor, rf, 1E-6);

        MinimaSearch.setControllingFactor(control, MinimaSearch.getNextFactorValue());
        rf = MinimaSearch.getControllingFactor(control);
        assertEquals(startingFactor, rf, 1E-6);

        MinimaSearch.logFactortoV(rf, 0.2078297837489273);

        MinimaSearch.setControllingFactor(control, MinimaSearch.getNextFactorValue());
        rf = MinimaSearch.getControllingFactor(control);
        assertEquals(startingFactor + 0.5, rf, 1E-6);
    }
}
